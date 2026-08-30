export function getApiBase(): string {
  return process.env.NEXT_PUBLIC_API_BASE_URL ?? "";
}