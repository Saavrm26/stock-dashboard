"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { TickerDetails } from "@/model/generated/v1/ticker_details";
import { fetchTickerDetails } from "@/app/lib/stockDashboardApiClient";
import { TickerDetailsView } from "@/app/components/TickerDetailsView";
import Footer from "@/app/components/Footer";

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
        const data = await fetchTickerDetails(symbol);
        setData(data as TickerDetails);
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

  return (
    <div className="min-h-screen bg-background text-on-surface pt-navbar">
      <TickerDetailsView details={data} />
      <Footer />
    </div>
  );
}
