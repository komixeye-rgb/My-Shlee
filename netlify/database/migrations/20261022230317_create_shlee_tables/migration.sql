CREATE TABLE "payments" (
	"id" serial PRIMARY KEY,
	"student_id" text NOT NULL,
	"payer_name" text NOT NULL,
	"plan_id" text NOT NULL,
	"amount_ugx" integer NOT NULL,
	"provider" text NOT NULL,
	"phone" text NOT NULL,
	"reference" text NOT NULL,
	"status" text DEFAULT 'PENDING' NOT NULL,
	"admin_note" text DEFAULT '' NOT NULL,
	"reviewed_by" text,
	"reviewed_at" timestamp with time zone,
	"created_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "students" (
	"id" text PRIMARY KEY,
	"name" text NOT NULL,
	"school" text NOT NULL,
	"district" text DEFAULT '' NOT NULL,
	"region" text DEFAULT 'Central' NOT NULL,
	"level" text DEFAULT 'UCE' NOT NULL,
	"aggregate" integer DEFAULT 36 NOT NULL,
	"streak_days" integer DEFAULT 0 NOT NULL,
	"xp" integer DEFAULT 0 NOT NULL,
	"phone" text DEFAULT '' NOT NULL,
	"plan_id" text,
	"plan_expires_at" timestamp with time zone,
	"created_at" timestamp with time zone DEFAULT now() NOT NULL,
	"updated_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "subject_scores" (
	"id" serial PRIMARY KEY,
	"student_id" text NOT NULL,
	"subject" text NOT NULL,
	"score_pct" real NOT NULL,
	"grade" text NOT NULL,
	"assessed_on" date DEFAULT now() NOT NULL,
	"created_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "subscription_plans" (
	"id" text PRIMARY KEY,
	"title" text NOT NULL,
	"price_ugx" integer NOT NULL,
	"period" text NOT NULL,
	"duration_days" integer NOT NULL,
	"description" text DEFAULT '' NOT NULL,
	"features" jsonb DEFAULT '[]' NOT NULL,
	"is_popular" boolean DEFAULT false NOT NULL,
	"is_active" boolean DEFAULT true NOT NULL,
	"sort_order" integer DEFAULT 0 NOT NULL,
	"created_at" timestamp with time zone DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE INDEX "payments_status_idx" ON "payments" ("status");--> statement-breakpoint
CREATE INDEX "payments_student_idx" ON "payments" ("student_id");--> statement-breakpoint
CREATE INDEX "students_aggregate_idx" ON "students" ("aggregate");--> statement-breakpoint
CREATE INDEX "students_school_idx" ON "students" ("school");--> statement-breakpoint
CREATE INDEX "subject_scores_student_idx" ON "subject_scores" ("student_id");--> statement-breakpoint
ALTER TABLE "payments" ADD CONSTRAINT "payments_plan_id_subscription_plans_id_fkey" FOREIGN KEY ("plan_id") REFERENCES "subscription_plans"("id");--> statement-breakpoint
ALTER TABLE "students" ADD CONSTRAINT "students_plan_id_subscription_plans_id_fkey" FOREIGN KEY ("plan_id") REFERENCES "subscription_plans"("id") ON DELETE SET NULL;--> statement-breakpoint
ALTER TABLE "subject_scores" ADD CONSTRAINT "subject_scores_student_id_students_id_fkey" FOREIGN KEY ("student_id") REFERENCES "students"("id") ON DELETE CASCADE;