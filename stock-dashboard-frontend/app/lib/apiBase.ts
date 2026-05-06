export function getApiBase(): string {
  const env = process.env.NODE_ENV;
  if (env === "production") {
    return process.env.NEXT_PUBLIC_API_BASE_URL_PROD ?? "";
  }
  if (env === "development") {
    return process.env.NEXT_PUBLIC_API_BASE_URL_LOCAL ?? "";
  }
  // Fallback to pre‑prod configuration
  return process.env.NEXT_PUBLIC_API_BASE_URL_PREPROD ?? "";
}