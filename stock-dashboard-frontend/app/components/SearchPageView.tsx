import { TickerSearchResponse } from "@/model/generated/v1/ticker_search";
import { useRouter, useSearchParams } from "next/navigation";
import Link from "next/link";

interface Props {
  data: TickerSearchResponse | null;
}

export const SearchPageView: React.FC<Props> = ({ data }) => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const query = searchParams.get("q") || "";
  const showDefaultMessage = data === null;
  const hasNoResults = data && data.quotes.length === 0;

  const handleViewDetails = (symbol: string) => {
    router.push(`/ticker/${symbol}`);
  };

  return (
    <main className="min-h-screen bg-background px-6 py-12 pt-navbar text-on-surface md:px-16">
      <div className="mx-auto max-w-7xl">
        <section className="mb-12">
          <div className="mb-4 flex items-center gap-2 text-xs text-on-surface-variant">
            <Link className="transition-colors hover:text-primary" href="/">
              Dashboard
            </Link>
            <span className="material-symbols-outlined text-sm">chevron_right</span>
            <span>Search Results</span>
          </div>
          <h1 className="mb-2 text-5xl font-semibold tracking-tight">Search Results</h1>
          <p className="text-lg text-on-surface-variant">
            Showing matches for <span className="italic text-on-surface">&quot;{query}&quot;</span>
          </p>
        </section>

        {showDefaultMessage ? (
          <div className="flex min-h-[400px] flex-col items-center justify-center">
            <p className="text-lg text-on-surface-variant">Enter a search query to find tickers.</p>
          </div>
        ) : hasNoResults ? (
          <div className="flex min-h-[400px] flex-col items-center justify-center">
            <p className="text-lg text-on-surface-variant">No tickers found for your search.</p>
          </div>
        ) : (
          <div className="space-y-8">
            <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
              <div className="border border-outline-variant bg-white p-6 shadow-sm">
                <p className="mb-2 text-xs uppercase tracking-widest text-on-surface-variant">Total Matches</p>
                <p className="text-3xl font-medium text-on-surface">{data.quotes.length}</p>
              </div>
              <div className="border border-outline-variant bg-white p-6 shadow-sm">
                <p className="mb-2 text-xs uppercase tracking-widest text-on-surface-variant">Search Term</p>
                <p className="truncate text-3xl font-medium text-primary">{query || "-"}</p>
              </div>
              <div className="border border-outline-variant bg-white p-6 shadow-sm">
                <p className="mb-2 text-xs uppercase tracking-widest text-on-surface-variant">Result Status</p>
                <p className="text-3xl font-medium text-on-surface">Ready</p>
              </div>
            </div>

            <section className="overflow-hidden border border-outline-variant bg-white shadow-sm">
              <div className="overflow-x-auto">
                <table className="w-full border-collapse text-left">
                  <thead>
                    <tr className="border-b border-outline-variant bg-slate-50">
                      <th className="px-6 py-4 text-xs uppercase tracking-widest text-on-surface-variant">
                        Symbol
                      </th>
                      <th className="px-6 py-4 text-xs uppercase tracking-widest text-on-surface-variant">
                        Company Name
                      </th>
                      <th className="px-6 py-4 text-xs uppercase tracking-widest text-on-surface-variant">
                        Exchange
                      </th>
                      <th className="px-6 py-4 text-xs uppercase tracking-widest text-on-surface-variant">
                        Sector / Industry
                      </th>
                      <th className="px-6 py-4 text-right text-xs uppercase tracking-widest text-on-surface-variant">
                        Action
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {data.quotes.map((quote) => (
                      <tr key={quote.symbol} className="group transition-colors hover:bg-slate-50">
                        <td className="px-6 py-6">
                          <span className="text-2xl font-medium text-primary group-hover:underline">
                            {quote.symbol}
                          </span>
                        </td>
                        <td className="px-6 py-6">
                          <div className="text-lg text-on-surface">
                            {quote.longname || quote.shortname}
                          </div>
                          {quote.typeDisp && (
                            <div className="text-xs text-on-surface-variant">
                              {quote.typeDisp}
                            </div>
                          )}
                        </td>
                        <td className="px-6 py-6">
                          <span className="inline-block border border-outline-variant bg-slate-50 px-2 py-1 text-xs text-on-surface-variant">
                            {quote.exchDisp || quote.exchange}
                          </span>
                        </td>
                        <td className="px-6 py-6 text-sm text-on-surface-variant">
                          {quote.sectorDisp && quote.industryDisp
                            ? `${quote.sectorDisp} / ${quote.industryDisp}`
                            : quote.sectorDisp || quote.industryDisp || "-"}
                        </td>
                        <td className="px-6 py-6 text-right">
                          <button
                            onClick={() => handleViewDetails(quote.symbol)}
                            className="border border-outline-variant px-4 py-2 text-xs text-on-surface transition-all hover:bg-primary hover:text-on-primary"
                          >
                            View Details
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>

            <div className="flex items-center justify-between">
              <span className="text-xs text-on-surface-variant">
                Showing {data.quotes.length} result{data.quotes.length !== 1 ? "s" : ""}
              </span>
            </div>
          </div>
        )}
      </div>
    </main>
  );
};
