import { boolean, date, index, integer, jsonb, pgTable, real, serial, text, timestamp } from "drizzle-orm/pg-core";

// UNEB candidates. Also the source for the national leaderboard.
export const students = pgTable(
  "students",
  {
    id: text().primaryKey(), // UNEB-style index, e.g. "UG-2026-9042"
    name: text().notNull(),
    school: text().notNull(),
    district: text().notNull().default(""),
    region: text().notNull().default("Central"),
    level: text().notNull().default("UCE"), // PLE | UCE | UACE
    aggregate: integer().notNull().default(36),
    streakDays: integer("streak_days").notNull().default(0),
    xp: integer().notNull().default(0),
    phone: text().notNull().default(""),
    planId: text("plan_id").references(() => subscriptionPlans.id, { onDelete: "set null" }),
    planExpiresAt: timestamp("plan_expires_at", { withTimezone: true }),
    createdAt: timestamp("created_at", { withTimezone: true }).notNull().defaultNow(),
    updatedAt: timestamp("updated_at", { withTimezone: true }).notNull().defaultNow(),
  },
  (t) => [index("students_aggregate_idx").on(t.aggregate), index("students_school_idx").on(t.school)],
);

export const subjectScores = pgTable(
  "subject_scores",
  {
    id: serial().primaryKey(),
    studentId: text("student_id")
      .notNull()
      .references(() => students.id, { onDelete: "cascade" }),
    subject: text().notNull(),
    scorePct: real("score_pct").notNull(),
    grade: text().notNull(), // D1..F9, derived from score_pct
    assessedOn: date("assessed_on").notNull().defaultNow(),
    createdAt: timestamp("created_at", { withTimezone: true }).notNull().defaultNow(),
  },
  (t) => [index("subject_scores_student_idx").on(t.studentId)],
);

export const subscriptionPlans = pgTable("subscription_plans", {
  id: text().primaryKey(), // e.g. "weekly", "termly", "uneb_pass"
  title: text().notNull(),
  priceUgx: integer("price_ugx").notNull(),
  period: text().notNull(),
  durationDays: integer("duration_days").notNull(),
  description: text().notNull().default(""),
  features: jsonb().$type<string[]>().notNull().default([]),
  isPopular: boolean("is_popular").notNull().default(false),
  isActive: boolean("is_active").notNull().default(true),
  sortOrder: integer("sort_order").notNull().default(0),
  createdAt: timestamp("created_at", { withTimezone: true }).notNull().defaultNow(),
});

// Mobile Money payments submitted by students, verified by an admin.
export const payments = pgTable(
  "payments",
  {
    id: serial().primaryKey(),
    studentId: text("student_id").notNull(),
    payerName: text("payer_name").notNull(),
    planId: text("plan_id")
      .notNull()
      .references(() => subscriptionPlans.id),
    amountUgx: integer("amount_ugx").notNull(),
    provider: text().notNull(), // "MTN" | "Airtel"
    phone: text().notNull(),
    reference: text().notNull(), // Mobile Money transaction ID from the SMS receipt
    status: text().notNull().default("PENDING"), // PENDING | COMPLETED | FAILED
    adminNote: text("admin_note").notNull().default(""),
    reviewedBy: text("reviewed_by"),
    reviewedAt: timestamp("reviewed_at", { withTimezone: true }),
    createdAt: timestamp("created_at", { withTimezone: true }).notNull().defaultNow(),
  },
  (t) => [index("payments_status_idx").on(t.status), index("payments_student_idx").on(t.studentId)],
);
