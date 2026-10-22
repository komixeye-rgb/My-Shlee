-- ==============================================================================
-- SHLEE STUDENT DASHBOARD - SUPABASE SQL SCHEMA
-- Project ID: ezrrixubfgpxvxcakfrr
-- Includes: Tables, Fields, Constraints, Date Timestamps, Totals & Rollups,
--           Row Level Security (RLS) Policies, and Starter Seed Data
-- ==============================================================================

-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ------------------------------------------------------------------------------
-- 1. PROFILES TABLE (Student Profile & Settings)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.profiles (
    id TEXT PRIMARY KEY,                       -- Student ID (e.g., 'UG-2026-9042') or auth.uid()
    student_id TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    projected_aggregate TEXT DEFAULT 'Agg 8',
    division TEXT DEFAULT 'Division 1',
    current_streak INTEGER DEFAULT 14,
    target_hours NUMERIC(4,2) DEFAULT 3.5,
    target_quizzes INTEGER DEFAULT 5,
    reminder_time TEXT DEFAULT '07:30 PM',
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- ------------------------------------------------------------------------------
-- 2. CANDIDATES TABLE (National UNEB Leaderboard)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.candidates (
    id TEXT PRIMARY KEY,                       -- e.g., 'UG-KLA-101'
    name TEXT NOT NULL,
    school TEXT NOT NULL,
    region TEXT NOT NULL,                      -- 'Central', 'Western', 'Eastern', 'Northern'
    aggregate INTEGER NOT NULL CHECK (aggregate >= 4 AND aggregate <= 72),
    division TEXT NOT NULL,                    -- 'Division 1', 'Division 2', etc.
    streak INTEGER NOT NULL DEFAULT 0,
    avatar_color BIGINT DEFAULT 4280191205,    -- ARGB Long color integer for UI
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- ------------------------------------------------------------------------------
-- 3. SUBJECT_SCORES TABLE (Detailed Subject Breakdown & Grading)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.subject_scores (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id TEXT NOT NULL REFERENCES public.profiles(student_id) ON DELETE CASCADE,
    subject_name TEXT NOT NULL,                -- 'Mathematics', 'Physics', 'Biology', etc.
    score_percentage NUMERIC(5,2) NOT NULL CHECK (score_percentage >= 0 AND score_percentage <= 100),
    uneb_grade TEXT NOT NULL,                  -- 'D1', 'D2', 'C3', 'C4', 'C5', 'C6', 'P7', 'P8', 'F9'
    assessed_date DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- ------------------------------------------------------------------------------
-- 4. SUBSCRIPTION_PLANS TABLE
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.subscription_plans (
    id TEXT PRIMARY KEY,                       -- 'weekly', 'termly', 'uneb_pass'
    title TEXT NOT NULL,
    price_ugx BIGINT NOT NULL CHECK (price_ugx >= 0),
    period TEXT NOT NULL,                      -- '7 Days', 'Term (3 Months)', 'Until UNEB'
    description TEXT,
    features TEXT[] DEFAULT ARRAY[]::TEXT[],
    is_popular BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- ------------------------------------------------------------------------------
-- 5. PAYMENT_TRANSACTIONS TABLE (Mobile Money / Payments with Date and Total Amount)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.payment_transactions (
    id TEXT PRIMARY KEY,                       -- Transaction ID, e.g., 'TXN-UG-91823'
    student_id TEXT NOT NULL,
    plan_id TEXT NOT NULL,
    plan_title TEXT NOT NULL,
    amount_ugx BIGINT NOT NULL CHECK (amount_ugx > 0),   -- Total paid in UGX
    phone_number TEXT NOT NULL,                         -- Mobile money phone number
    provider TEXT NOT NULL,                             -- 'MTN Mobile Money' or 'Airtel Money'
    status TEXT NOT NULL DEFAULT 'COMPLETED',           -- 'PENDING', 'COMPLETED', 'FAILED'
    transaction_date DATE NOT NULL DEFAULT CURRENT_DATE,-- Date of transaction
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- ------------------------------------------------------------------------------
-- 6. VIEWS & TOTALS ROLLUPS (Aggregations, Totals, and Statistics)
-- ------------------------------------------------------------------------------

-- Total revenue and transaction count summary
CREATE OR REPLACE VIEW public.v_payment_totals AS
SELECT 
    COUNT(*) AS total_transactions,
    COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) AS total_successful_transactions,
    COALESCE(SUM(CASE WHEN status = 'COMPLETED' THEN amount_ugx ELSE 0 END), 0) AS total_revenue_ugx,
    COALESCE(AVG(CASE WHEN status = 'COMPLETED' THEN amount_ugx ELSE NULL END), 0) AS average_order_value_ugx,
    MAX(transaction_date) AS last_transaction_date
FROM public.payment_transactions;

-- Candidate rankings overview with national percentiles
CREATE OR REPLACE VIEW public.v_candidate_rankings AS
SELECT 
    id,
    name,
    school,
    region,
    aggregate,
    division,
    streak,
    RANK() OVER (ORDER BY aggregate ASC, streak DESC) AS national_rank,
    created_at,
    updated_at
FROM public.candidates;

-- ------------------------------------------------------------------------------
-- 7. AUTOMATIC TIMESTAMP TRIGGER
-- ------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = timezone('utc'::text, now());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_profiles_updated_at ON public.profiles;
CREATE TRIGGER trigger_profiles_updated_at
    BEFORE UPDATE ON public.profiles
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();

DROP TRIGGER IF EXISTS trigger_candidates_updated_at ON public.candidates;
CREATE TRIGGER trigger_candidates_updated_at
    BEFORE UPDATE ON public.candidates
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();

-- ------------------------------------------------------------------------------
-- 8. ROW LEVEL SECURITY (RLS) POLICIES
-- Allows reading public data (leaderboards & plans) and reading/writing records via anon key
-- ------------------------------------------------------------------------------
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.candidates ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.subject_scores ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.subscription_plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.payment_transactions ENABLE ROW LEVEL SECURITY;

-- Allow read access to all clients
CREATE POLICY "Allow public read access on profiles"
    ON public.profiles FOR SELECT USING (true);

CREATE POLICY "Allow public insert/update on profiles"
    ON public.profiles FOR ALL USING (true);

CREATE POLICY "Allow public read on candidates"
    ON public.candidates FOR SELECT USING (true);

CREATE POLICY "Allow public insert/update on candidates"
    ON public.candidates FOR ALL USING (true);

CREATE POLICY "Allow public read on subscription_plans"
    ON public.subscription_plans FOR SELECT USING (true);

CREATE POLICY "Allow public read/insert on payment_transactions"
    ON public.payment_transactions FOR ALL USING (true);

CREATE POLICY "Allow public read/insert on subject_scores"
    ON public.subject_scores FOR ALL USING (true);

-- ------------------------------------------------------------------------------
-- 9. INITIAL SEED DATA
-- ------------------------------------------------------------------------------

-- Seed Subscription Plans
INSERT INTO public.subscription_plans (id, title, price_ugx, period, description, features, is_popular)
VALUES
    ('weekly', '7-Day Quick Sprint', 5000, '7 Days', 'Ideal for pre-mock revision', ARRAY['All Subject Quizzes', 'Gemini AI Tutor', 'Daily Targets'], FALSE),
    ('termly', 'Full Term Pass', 25000, 'Term (3 Months)', 'Complete UNEB syllabus prep with past papers', ARRAY['Unlimited AI High-Thinking Tutor', 'Predicted UNEB Mock Papers', 'National Leaderboard Access', 'Offline Radar Analytics'], TRUE),
    ('uneb_pass', 'Final Candidate Pass', 60000, 'Until UNEB (Full Year)', 'Full candidate pass until final examinations', ARRAY['All Features Included', '1-on-1 UNEB Examiner Reviews', 'Parent Performance Reports', 'Guaranteed Pass Strategy'], FALSE)
ON CONFLICT (id) DO UPDATE SET
    title = EXCLUDED.title,
    price_ugx = EXCLUDED.price_ugx,
    features = EXCLUDED.features;

-- Seed National Candidates (Ugandan Schools)
INSERT INTO public.candidates (id, name, school, region, aggregate, division, streak, avatar_color)
VALUES
    ('UG-KLA-101', 'Kato Emmanuel', 'St. Mary''s College Kisubi', 'Central', 8, 'Division 1', 28, 4280191205),
    ('UG-WST-204', 'Nakato Grace', 'Mt. St. Mary''s Namagunga', 'Central', 9, 'Division 1', 24, 4282361254),
    ('UG-KLA-309', 'Okello Brian', 'King''s College Budo', 'Central', 10, 'Division 1', 19, 4280324068),
    ('UG-EST-412', 'Mbabazi Faith', 'Gayaza High School', 'Central', 11, 'Division 1', 15, 4293888362),
    ('UG-NTH-515', 'Akena Samuel', 'Namilyango College', 'Central', 12, 'Division 1', 12, 4294943770),
    ('UG-WST-618', 'Atuhairwe Joan', 'Uganda Martyrs SS Namugongo', 'Western', 13, 'Division 1', 10, 4281775825),
    ('UG-EST-721', 'Wasswa Derrick', 'Kigezi High School', 'Western', 14, 'Division 1', 9, 4282479017),
    ('UG-NTH-824', 'Aceng Patricia', 'St. Henry''s College Kitovu', 'Northern', 15, 'Division 1', 7, 4294938677)
ON CONFLICT (id) DO UPDATE SET
    aggregate = EXCLUDED.aggregate,
    streak = EXCLUDED.streak;

-- Seed Default Candidate Profile
INSERT INTO public.profiles (id, student_id, name, projected_aggregate, division, current_streak, target_hours, target_quizzes, reminder_time)
VALUES
    ('UG-2026-9042', 'UG-2026-9042', 'Kato Emmanuel', 'Agg 8', 'Division 1', 14, 3.5, 5, '07:30 PM')
ON CONFLICT (id) DO NOTHING;

-- Seed Sample Payment Transactions with Totals & Dates
INSERT INTO public.payment_transactions (id, student_id, plan_id, plan_title, amount_ugx, phone_number, provider, status, transaction_date)
VALUES
    ('TXN-UG-91823', 'UG-2026-9042', 'termly', 'Full Term Pass', 25000, '+256 772 123 456', 'MTN Mobile Money', 'COMPLETED', CURRENT_DATE - INTERVAL '14 days'),
    ('TXN-UG-91824', 'UG-2026-9042', 'weekly', '7-Day Quick Sprint', 5000, '+256 701 987 654', 'Airtel Money', 'COMPLETED', CURRENT_DATE - INTERVAL '30 days')
ON CONFLICT (id) DO NOTHING;
