import type { Config } from "@netlify/functions";
import { asc, eq } from "drizzle-orm";
import { db } from "../../db/index.js";
import { subscriptionPlans } from "../../db/schema.js";
import { error, json } from "./_lib/http.js";

export default async (req: Request) => {
  if (req.method !== "GET") return error("Method not allowed", 405);
  const plans = await db
    .select()
    .from(subscriptionPlans)
    .where(eq(subscriptionPlans.isActive, true))
    .orderBy(asc(subscriptionPlans.sortOrder));
  return json({ plans });
};

export const config: Config = { path: "/api/plans" };
