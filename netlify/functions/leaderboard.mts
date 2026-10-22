import type { Config } from "@netlify/functions";
import { and, asc, desc, eq, sql } from "drizzle-orm";
import { db } from "../../db/index.js";
import { students } from "../../db/schema.js";
import { division, error, json, str } from "./_lib/http.js";

// GET /api/leaderboard?category=aggregate|streak|xp&region=&district=&school=&level=&limit=
export default async (req: Request) => {
  if (req.method !== "GET") return error("Method not allowed", 405);
  const url = new URL(req.url);
  const category = url.searchParams.get("category") ?? "aggregate";
  const limit = Math.min(100, Math.max(1, Number(url.searchParams.get("limit")) || 50));

  const filters = [
    ["region", students.region],
    ["district", students.district],
    ["school", students.school],
    ["level", students.level],
  ] as const;
  const where = filters
    .map(([param, column]) => {
      const value = str(url.searchParams.get(param));
      return value ? eq(column, value) : undefined;
    })
    .filter((c) => c !== undefined);

  const order =
    category === "streak"
      ? [desc(students.streakDays), asc(students.aggregate)]
      : category === "xp"
        ? [desc(students.xp), asc(students.aggregate)]
        : [asc(students.aggregate), desc(students.xp)];

  const rows = await db
    .select({
      id: students.id,
      name: students.name,
      school: students.school,
      district: students.district,
      region: students.region,
      level: students.level,
      aggregate: students.aggregate,
      streakDays: students.streakDays,
      xp: students.xp,
    })
    .from(students)
    .where(and(...where))
    .orderBy(...order)
    .limit(limit);

  const [{ total }] = await db.select({ total: sql<number>`count(*)::int` }).from(students);

  return json({
    category,
    totalCandidates: total,
    candidates: rows.map((r, i) => ({ rank: i + 1, ...r, division: division(r.aggregate) })),
  });
};

export const config: Config = { path: "/api/leaderboard" };
