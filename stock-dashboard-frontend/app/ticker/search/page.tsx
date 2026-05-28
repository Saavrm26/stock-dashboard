"use client";

import { SearchPageView } from "@/app/components/SearchPageView";
import { searchTickers } from "@/app/lib/stockDashboardApiClient";
import { TickerSearchResponse } from "@/model/generated/v1/ticker_search";
import { useSearchParams } from "next/navigation";
import { useEffect, useState, Suspense } from "react";

function SearchPageContent() {
  const searchParams = useSearchParams();
  const query = searchParams.get("q");
  const [data, setData] = useState<TickerSearchResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!query) {
      setData(null);
      setLoading(false);
      return;
    }
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await searchTickers(query);
        setData(data);
      } catch (e: unknown) {
        if (e instanceof Error) {
          setError(e.message);
        } else {
          setError(`An unknown error occurred while fetching ticker details. ${e}`);
        }
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [query]);

  if (loading) {
    return (
      <main className="flex min-h-screen items-center justify-center">
        <p className="text-lg">Loading tickers...</p>
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
  return <SearchPageView data={data} />;
}

export default function SearchPage() {
  return (
    <Suspense fallback={<main className="flex min-h-screen items-center justify-center"><p className="text-lg">Loading...</p></main>}>
      <SearchPageContent />
    </Suspense>
  );
}
