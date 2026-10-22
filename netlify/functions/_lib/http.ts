import { getUser } from "@netlify/identity";

export const json = (data: unknown, status = 200) =>
  Response.json(data, { status, headers: { "cache-control": "no-store" } });

export const error = (message: string, status = 400) => json({ error: message }, status);

export async function readJson<T = Record<string, unknown>>(req: Request): Promise<T | null> {
  try {
    return (await req.json()) as T;
  } catch {
    return null;
  }
}

// Returns the signed-in Identity user if they hold the "admin" role, otherwise null.
export async function requireAdmin() {
  const user = await getUser();
  if (!user || !user.roles?.includes("admin")) return null;
  return user;
}

// UNEB grade for a percentage score.
export function unebGrade(score: number): string {
  if (score >= 80) return "D1";
  if (score >= 75) return "D2";
  if (score >= 70) return "C3";
  if (score >= 65) return "C4";
  if (score >= 60) return "C5";
  if (score >= 55) return "C6";
  if (score >= 50) return "P7";
  if (score >= 45) return "P8";
  return "F9";
}

// UCE division from a best-8 aggregate.
export function division(aggregate: number): string {
  if (aggregate <= 32) return "Division 1";
  if (aggregate <= 45) return "Division 2";
  if (aggregate <= 58) return "Division 3";
  if (aggregate <= 68) return "Division 4";
  return "Ungraded";
}

export const str = (v: unknown, max = 200) => (typeof v === "string" ? v.trim().slice(0, max) : "");
export const int = (v: unknown, min: number, max: number): number | null => {
  const n = typeof v === "number" ? v : typeof v === "string" && v.trim() !== "" ? Number(v) : NaN;
  if (!Number.isFinite(n)) return null;
  return Math.min(max, Math.max(min, Math.round(n)));
};
