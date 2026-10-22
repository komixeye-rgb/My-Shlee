import type { Config } from "@netlify/functions";
import { and, eq } from "drizzle-orm";
import { db } from "../../db/index.js";
import { payments, subscriptionPlans } from "../../db/schema.js";
import { error, json, readJson, str } from "./_lib/http.js";

// POST /api/payments — a student reports a Mobile Money payment. An admin verifies it in the admin panel.
export default async (req: Request) => {
  if (req.method !== "POST") return error("Method not allowed", 405);
  const body = await readJson(req);
  if (!body) return error("Invalid JSON body");

  const studentId = str(body.studentId, 40).toUpperCase();
  const payerName = str(body.payerName, 120);
  const planId = str(body.planId, 40);
  const provider = str(body.provider, 20);
  const phone = str(body.phone, 20).replace(/[^\d+]/g, "");
  const reference = str(body.reference, 60);

  if (!studentId || !payerName || !planId || !reference) return error("Please fill in every field.");
  if (provider !== "MTN" && provider !== "Airtel") return error("Provider must be MTN or Airtel.");
  if (!/^\+?\d{9,13}$/.test(phone)) return error("Enter a valid Ugandan phone number.");

  const [plan] = await db
    .select()
    .from(subscriptionPlans)
    .where(and(eq(subscriptionPlans.id, planId), eq(subscriptionPlans.isActive, true)));
  if (!plan) return error("Unknown plan.");

  const [duplicate] = await db
    .select({ id: payments.id })
    .from(payments)
    .where(and(eq(payments.reference, reference), eq(payments.provider, provider)));
  if (duplicate) return error("This transaction ID has already been submitted.", 409);

  const [payment] = await db
    .insert(payments)
    .values({ studentId, payerName, planId, amountUgx: plan.priceUgx, provider, phone, reference })
    .returning({ id: payments.id, status: payments.status, amountUgx: payments.amountUgx });

  return json({ payment }, 201);
};

export const config: Config = { path: "/api/payments" };
