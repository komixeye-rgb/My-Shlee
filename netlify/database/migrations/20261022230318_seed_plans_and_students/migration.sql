-- Seed the subscription plans and starter leaderboard candidates
INSERT INTO "subscription_plans" ("id", "title", "price_ugx", "period", "duration_days", "description", "features", "is_popular", "sort_order") VALUES
  ('weekly', '7-Day Quick Sprint', 5000, '7 Days Access', 7, 'Ideal for pre-mock revision', '["All Subject Quizzes","AI Tutor","Daily Study Targets"]', false, 1),
  ('termly', 'Full Term Pass', 25000, 'Full Term (3 Months)', 90, 'Complete UNEB syllabus prep with past papers', '["Unlimited AI High-Thinking Tutor","Predicted UNEB Mock Papers","National Leaderboard Access","Offline Radar Analytics"]', true, 2),
  ('uneb_pass', 'Final Candidate Pass', 60000, 'Until Final UNEB Exams', 365, 'Full candidate pass until final examinations', '["All Features Included","1-on-1 UNEB Examiner Reviews","Parent Performance Reports","Guaranteed Pass Strategy"]', false, 3)
ON CONFLICT ("id") DO NOTHING;
--> statement-breakpoint
INSERT INTO "students" ("id", "name", "school", "district", "region", "level", "aggregate", "streak_days", "xp") VALUES
  ('UG-KLA-101', 'Kato Emmanuel', 'St. Mary''s College Kisubi', 'Wakiso', 'Central', 'UCE', 8, 28, 4890),
  ('UG-WST-204', 'Nakato Grace', 'Mt. St. Mary''s Namagunga', 'Mukono', 'Central', 'UCE', 9, 24, 4650),
  ('UG-KLA-309', 'Okello Brian', 'King''s College Budo', 'Wakiso', 'Central', 'UCE', 10, 19, 4410),
  ('UG-EST-412', 'Mbabazi Faith', 'Gayaza High School', 'Wakiso', 'Central', 'UCE', 11, 15, 4120),
  ('UG-NTH-515', 'Akena Samuel', 'Namilyango College', 'Mukono', 'Central', 'UCE', 12, 12, 3980),
  ('UG-WST-618', 'Atuhairwe Joan', 'Uganda Martyrs SS Namugongo', 'Wakiso', 'Central', 'UCE', 13, 10, 3720),
  ('UG-WST-721', 'Wasswa Derrick', 'Kigezi High School', 'Kabale', 'Western', 'UCE', 14, 9, 3510),
  ('UG-CTR-824', 'Aceng Patricia', 'St. Henry''s College Kitovu', 'Masaka', 'Central', 'UCE', 15, 7, 3300)
ON CONFLICT ("id") DO NOTHING;
