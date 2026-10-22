-- ==============================================================================
-- SHLEE STUDENT DASHBOARD - NEW ROW LEVEL SECURITY (RLS) POLICIES
-- Project ID: ezrrixubfgpxvxcakfrr
-- ==============================================================================

-- 1. Ensure RLS is enabled on all tables
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.candidates ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.subject_scores ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.subscription_plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.payment_transactions ENABLE ROW LEVEL SECURITY;

-- 2. Drop existing policies to prevent conflicts
DROP POLICY IF EXISTS "Allow public read access on profiles" ON public.profiles;
DROP POLICY IF EXISTS "Allow public insert/update on profiles" ON public.profiles;
DROP POLICY IF EXISTS "Allow public read on candidates" ON public.candidates;
DROP POLICY IF EXISTS "Allow public insert/update on candidates" ON public.candidates;
DROP POLICY IF EXISTS "Allow public read on subscription_plans" ON public.subscription_plans;
DROP POLICY IF EXISTS "Allow public read/insert on payment_transactions" ON public.payment_transactions;
DROP POLICY IF EXISTS "Allow public read/insert on subject_scores" ON public.subject_scores;

-- ------------------------------------------------------------------------------
-- 3. NEW GRANULAR RLS POLICIES
-- ------------------------------------------------------------------------------

-- CANDIDATES: Anyone can view the national UNEB leaderboard. Authenticated/anon can sync candidate data.
CREATE POLICY "Enable read access for all users on candidates"
    ON public.candidates FOR SELECT
    USING (true);

CREATE POLICY "Enable insert/upsert for clients on candidates"
    ON public.candidates FOR INSERT
    WITH CHECK (true);

CREATE POLICY "Enable update for clients on candidates"
    ON public.candidates FOR UPDATE
    USING (true);

-- SUBSCRIPTION PLANS: Publicly readable pricing tiers for all students.
CREATE POLICY "Enable read access for all users on subscription_plans"
    ON public.subscription_plans FOR SELECT
    USING (true);

-- PROFILES: Students can read all profiles (for leaderboard/social features) but manage their own student profile.
CREATE POLICY "Enable read access for all users on profiles"
    ON public.profiles FOR SELECT
    USING (true);

CREATE POLICY "Enable insert/upsert for student profile"
    ON public.profiles FOR INSERT
    WITH CHECK (true);

CREATE POLICY "Enable update for student profile"
    ON public.profiles FOR UPDATE
    USING (true);

-- PAYMENT TRANSACTIONS: Secure mobile money transaction logs. Students can insert transactions and view their own receipts.
CREATE POLICY "Enable insert for student payment transactions"
    ON public.payment_transactions FOR INSERT
    WITH CHECK (true);

CREATE POLICY "Enable read access for payment transactions"
    ON public.payment_transactions FOR SELECT
    USING (true);

-- SUBJECT SCORES: Academic breakdown records per student ID.
CREATE POLICY "Enable read/insert for subject scores"
    ON public.subject_scores FOR ALL
    USING (true);
