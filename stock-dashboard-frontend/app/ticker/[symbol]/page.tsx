"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { TickerDetails } from "@/model/generated/v1/ticker_details";
import { getApiBase } from "@/app/lib/apiBase";
import { TickerDetailsView } from "@/app/components/TickerDetailsView";

export default function TickerPage() {
  const { symbol } = useParams<{ symbol: string }>();
  const [data, setData] = useState<TickerDetails | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!symbol) {
      setError("Ticker symbol missing in URL.");
      setLoading(false);
      return;
    }

    const fetchData = async () => {
      try {
        const base = getApiBase();
        console.log(`Fetching with base url ${base}`)
        const url = `${base}/v1/stocks/ticker-details?query=${encodeURIComponent(
          symbol
        )}`;
        const resp = await fetch(url, {
          credentials: 'include'
        });
        if (!resp.ok) {
          throw new Error(`API error: ${resp.status}`);
        }
        const json = await resp.json();
        setData(json as TickerDetails);
      } catch (e: any) {
        setError(e.message ?? "Unknown error");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [symbol]);

  if (loading) {
    return (
      <main className="flex min-h-screen items-center justify-center">
        <p className="text-lg">Loading ticker details…</p>
      </main>
    );
  }

  if (error) {
    return (
      <main className="flex min-h-screen items-center justify-center">
        <p className="text-red-600 text-lg">Error: {error}</p>
      </main>
    );
  }

  if (!data) {
    return (
      <main className="flex min-h-screen items-center justify-center">
        <p className="text-lg">No data available.</p>
      </main>
    );
  }

  return <TickerDetailsView details={data} />;
}