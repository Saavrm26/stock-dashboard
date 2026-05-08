import { getApiBase } from "@/app/lib/apiBase";

/**
 * Fetch ticker details for a given symbol.
 * If the API response contains a `redirectUrl`, the browser will be redirected.
 * Otherwise, the parsed JSON response is returned.
 *
 * @param symbol - The ticker symbol to fetch details for.
 * @returns The ticker details JSON object.
 */
export async function fetchTickerDetails(symbol: string) {
  const base = getApiBase();
  const url = `${base}/v1/stocks/ticker-details?query=${encodeURIComponent(symbol)}`;
  const resp = await fetch(url, { credentials: "include" });

  if (!resp.ok) {
    throw new Error(`API error: ${resp.status}`);
  }

  const json = await resp.json();

  // If the API signals a redirect, perform it.
  if (json?.redirectUrl) {
    window.location.href = json.redirectUrl;
    // Return a never‑resolving promise to stop further processing.
    return new Promise(() => {});
  }

  return json;
}

/**
 * Initiates the login flow.
 * Calls the Cognito authorization endpoint and follows any redirect returned by the API.
 */
export async function login() {
  const base = getApiBase();
  const url = `${base}/oauth2/authorization/cognito`;
  const resp = await fetch(url, { credentials: "include" });

  if (!resp.ok) {
    throw new Error(`Login request failed: ${resp.status}`);
  }

  const json = await resp.json();

  if (json?.redirectUrl) {
    window.location.href = json.redirectUrl;
    return new Promise(() => {});
  }

  // No redirect – assume login succeeded; nothing further to do.
  return;
}