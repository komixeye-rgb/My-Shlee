import type { Config, Context } from "@netlify/functions";
import { and, asc, desc, eq, ilike, or, sql } from "drizzle-orm";
import { db } from "../../db/index.js";
import { payments, students, subjectScores, subscriptionPlans } from "../../db/schema.js";
import { error, int, json, readJson, requireAdmin, str, unebGrade } from "./_lib/http.js";

type Body = Record<string, unknown>;
type Admin = NonNullable<Awaited<ReturnType<typeof requireAdmin>>>;

const LEVELS = ["PLE", "UCE", "UACE"];
const REGIONS = ["Central", "Eastern", "Northern", "Western"];
const STATUSES = ["PENDING", "COMPLETED", "FAILED"];

// Admin API. Every route requires a Netlify Identity user with the "admin" role.
//   GET    /api/admin/me
//   GET    /api/admin/stats
//   GET    /api/admin/students?q=        POST /api/admin/students
//   GET    /api/admin/students/:id       PUT/DELETE /api/admin/students/:id
//   POST   /api/admin/students/:id/scores   DELETE /api/admin/scores/:id
//   GET    /api/admin/plans              POST /api/admin/plans   PUT/DELETE /api/admin/plans/:id
//   GET    /api/admin/payments?status=   PUT /api/admin/payments/:id
export default async (req: Request, _context: Context) => {
  const admin = await requireAdmin();
  if (!admin) return error("Admin access required", 401);

  const url = new URL(req.url);
  const parts = url.pathname.replace(/^\/api\/admin\/?/, "").split("/").filter(Boolean).map(decodeURIComponent);
  const [resource, id, sub] = parts;
  const m = req.method;

  try {
    if (resource === "me" && m === "GET") return json({ email: admin.email, name: admin.name ?? null });
    if (resource === "stats" && m === "GET") return stats();

    if (resource === "students") {
      if (!id && m === "GET") return listStudents(url.searchParams.get("q") ?? "");
      if (!id && m === "POST") return saveStudent(null, await readJson(req));
      if (id && sub === "scores" && m === "POST") return addScore(id, await readJson(req));
      if (id && !sub && m === "GET") return getStudent(id);
      if (id && !sub && m === "PUT") return saveStudent(id, await readJson(req));
      if (id && !sub && m === "DELETE") return deleteStudent(id);
    }
    if (resource === "scores" && id && m === "DELETE") {
      const scoreId = int(id, 1, 2 ** 31 - 1);
      if (!scoreId) return error("Invalid score id");
      const deleted = await db.delete(subjectScores).where(eq(subjectScores.id, scoreId)).returning();
      return deleted.length ? json({ ok: true }) : error("Score not found", 404);
    }

    if (resource === "plans") {
      if (!id && m === "GET") return json({ plans: await db.select().from(subscriptionPlans).orderBy(asc(subscriptionPlans.sortOrder)) });
      if (!id && m === "POST") return savePlan(null, await readJson(req));
      if (id && m === "PUT") return savePlan(id, await readJson(req));
      if (id && m === "DELETE") return deletePlan(id);
    }

    if (resource === "payments") {
      if (!id && m === "GET") return listPayments(url.searchParams.get("status") ?? "");
      if (id && m === "PUT") return reviewPayment(id, await readJson(req), admin);
    }

    return error("Not found", 404);
  } catch (e) {
    console.error("Admin API error", e);
    return error("Something went wrong on the server", 500);
  }
};

async function stats() {
  const [s] = await db
    .select({
      students: sql<number>`count(*)::int`,
      subscribed: sql<number>`count(*) filter (where ${students.planExpiresAt} > now())::int`,
    })
    .from(students);
  const [p] = await db
    .select({
      pending: sql<number>`count(*) filter (where ${payments.status} = 'PENDING')::int`,
      completed: sql<number>`count(*) filter (where ${payments.status} = 'COMPLETED')::int`,
      revenueUgx: sql<number>`coalesce(sum(${payments.amountUgx}) filter (where ${payments.status} = 'COMPLETED'), 0)::int`,
      revenue30dUgx: sql<number>`coalesce(sum(${payments.amountUgx}) filter (where ${payments.status} = 'COMPLETED' and ${payments.reviewedAt} > now() - interval '30 days'), 0)::int`,
    })
    .from(payments);
  const byPlan = await db
    .select({
      planId: payments.planId,
      count: sql<number>`count(*)::int`,
      revenueUgx: sql<number>`coalesce(sum(${payments.amountUgx}), 0)::int`,
    })
    .from(payments)
    .where(eq(payments.status, "COMPLETED"))
    .groupBy(payments.planId);
  return json({ ...s, ...p, byPlan });
}

async function listStudents(q: string) {
  const term = q.trim().slice(0, 80);
  const pattern = `%${term.replace(/[%_\\]/g, "\\$&")}%`;
  const rows = await db
    .select()
    .from(students)
    .where(term ? or(ilike(students.name, pattern), ilike(students.id, pattern), ilike(students.school, pattern)) : undefined)
    .orderBy(asc(students.aggregate), asc(students.name))
    .limit(200);
  return json({ students: rows });
}

async function getStudent(id: string) {
  const [student] = await db.select().from(students).where(eq(students.id, id));
  if (!student) return error("Student not found", 404);
  const scores = await db
    .select()
    .from(subjectScores)
    .where(eq(subjectScores.studentId, id))
    .orderBy(desc(subjectScores.assessedOn), asc(subjectScores.subject));
  const history = await db
    .select()
    .from(payments)
    .where(eq(payments.studentId, id))
    .orderBy(desc(payments.createdAt));
  return json({ student, scores, payments: history });
}

async function saveStudent(id: string | null, body: Body | null) {
  if (!body) return error("Invalid JSON body");
  const name = str(body.name, 120);
  const school = str(body.school, 160);
  if (!name || !school) return error("Name and school are required.");

  const level = str(body.level, 10).toUpperCase() || "UCE";
  const region = str(body.region, 20) || "Central";
  if (!LEVELS.includes(level)) return error("Level must be PLE, UCE or UACE.");
  if (!REGIONS.includes(region)) return error("Unknown region.");

  const values = {
    name,
    school,
    level,
    region,
    district: str(body.district, 80),
    phone: str(body.phone, 20),
    aggregate: int(body.aggregate, 4, 72) ?? 36,
    streakDays: int(body.streakDays, 0, 10000) ?? 0,
    xp: int(body.xp, 0, 10_000_000) ?? 0,
    updatedAt: new Date(),
  };

  if (id) {
    const [updated] = await db.update(students).set(values).where(eq(students.id, id)).returning();
    return updated ? json({ student: updated }) : error("Student not found", 404);
  }

  const newId = str(body.id, 40).toUpperCase();
  if (!/^[A-Z0-9][A-Z0-9/-]{2,39}$/.test(newId)) return error("Student ID may only contain letters, numbers, - and /.");
  const [created] = await db.insert(students).values({ id: newId, ...values }).onConflictDoNothing().returning();
  return created ? json({ student: created }, 201) : error("A student with that ID already exists.", 409);
}

async function deleteStudent(id: string) {
  const deleted = await db.delete(students).where(eq(students.id, id)).returning({ id: students.id });
  return deleted.length ? json({ ok: true }) : error("Student not found", 404);
}

async function addScore(studentId: string, body: Body | null) {
  if (!body) return error("Invalid JSON body");
  const subject = str(body.subject, 80);
  const score = typeof body.scorePct === "number" ? body.scorePct : Number(body.scorePct);
  if (!subject) return error("Subject is required.");
  if (!Number.isFinite(score) || score < 0 || score > 100) return error("Score must be between 0 and 100.");
  const assessedOn = str(body.assessedOn, 10);
  if (assessedOn && !/^\d{4}-\d{2}-\d{2}$/.test(assessedOn)) return error("Date must be YYYY-MM-DD.");

  const [student] = await db.select({ id: students.id }).from(students).where(eq(students.id, studentId));
  if (!student) return error("Student not found", 404);

  const [created] = await db
    .insert(subjectScores)
    .values({ studentId, subject, scorePct: score, grade: unebGrade(score), ...(assessedOn ? { assessedOn } : {}) })
    .returning();
  return json({ score: created }, 201);
}

async function savePlan(id: string | null, body: Body | null) {
  if (!body) return error("Invalid JSON body");
  const title = str(body.title, 80);
  const priceUgx = int(body.priceUgx, 0, 100_000_000);
  const durationDays = int(body.durationDays, 1, 3650);
  if (!title || priceUgx === null || durationDays === null) return error("Title, price and duration are required.");
  const features = Array.isArray(body.features)
    ? body.features.map((f) => str(f, 120)).filter(Boolean).slice(0, 20)
    : str(body.features, 2000).split("\n").map((f) => f.trim()).filter(Boolean).slice(0, 20);

  const values = {
    title,
    priceUgx,
    durationDays,
    period: str(body.period, 60) || `${durationDays} Days`,
    description: str(body.description, 300),
    features,
    isPopular: body.isPopular === true,
    isActive: body.isActive !== false,
    sortOrder: int(body.sortOrder, 0, 1000) ?? 0,
  };

  if (id) {
    const [updated] = await db.update(subscriptionPlans).set(values).where(eq(subscriptionPlans.id, id)).returning();
    return updated ? json({ plan: updated }) : error("Plan not found", 404);
  }
  const newId = str(body.id, 40).toLowerCase();
  if (!/^[a-z0-9_-]{2,40}$/.test(newId)) return error("Plan ID may only contain lowercase letters, numbers, - and _.");
  const [created] = await db.insert(subscriptionPlans).values({ id: newId, ...values }).onConflictDoNothing().returning();
  return created ? json({ plan: created }, 201) : error("A plan with that ID already exists.", 409);
}

async function deletePlan(id: string) {
  const [used] = await db.select({ id: payments.id }).from(payments).where(eq(payments.planId, id)).limit(1);
  if (used) return error("This plan has payments on record. Deactivate it instead of deleting.", 409);
  const deleted = await db.delete(subscriptionPlans).where(eq(subscriptionPlans.id, id)).returning();
  return deleted.length ? json({ ok: true }) : error("Plan not found", 404);
}

async function listPayments(status: string) {
  const rows = await db
    .select({
      payment: payments,
      planTitle: subscriptionPlans.title,
      studentName: students.name,
    })
    .from(payments)
    .leftJoin(subscriptionPlans, eq(subscriptionPlans.id, payments.planId))
    .leftJoin(students, eq(students.id, payments.studentId))
    .where(STATUSES.includes(status) ? eq(payments.status, status) : undefined)
    .orderBy(desc(payments.createdAt))
    .limit(200);
  return json({
    payments: rows.map((r) => ({ ...r.payment, planTitle: r.planTitle, studentName: r.studentName })),
  });
}

// Approving a payment activates (or extends) the student's plan.
async function reviewPayment(rawId: string, body: Body | null, admin: Admin) {
  const id = int(rawId, 1, 2 ** 31 - 1);
  if (!id) return error("Invalid payment id");
  if (!body) return error("Invalid JSON body");
  const status = str(body.status, 20).toUpperCase();
  if (status !== "COMPLETED" && status !== "FAILED") return error("Status must be COMPLETED or FAILED.");

  const result = await db.transaction(async (tx) => {
    const [payment] = await tx
      .update(payments)
      .set({ status, adminNote: str(body.adminNote, 300), reviewedBy: admin.email ?? admin.id, reviewedAt: new Date() })
      .where(and(eq(payments.id, id), eq(payments.status, "PENDING")))
      .returning();
    if (!payment) return null;

    let studentActivated = false;
    if (status === "COMPLETED") {
      const [plan] = await tx.select().from(subscriptionPlans).where(eq(subscriptionPlans.id, payment.planId));
      const activated = await tx
        .update(students)
        .set({
          planId: payment.planId,
          planExpiresAt: sql`greatest(coalesce(${students.planExpiresAt}, now()), now()) + make_interval(days => ${plan?.durationDays ?? 0})`,
          updatedAt: new Date(),
        })
        .where(eq(students.id, payment.studentId))
        .returning({ id: students.id });
      studentActivated = activated.length > 0;
    }
    return { payment, studentActivated };
  });

  return result ? json(result) : error("Payment not found or already reviewed.", 409);
}

export const config: Config = { path: ["/api/admin", "/api/admin/*"] };
