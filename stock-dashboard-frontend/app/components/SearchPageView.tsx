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
    <main className="max-w-[1440px] mx-auto px-[64px] py-12 min-h-screen pt-20">
      {/* Header Section */}
      <section className="mb-12">
        <div className="flex items-center gap-2 text-[#c4c7c8] text-[12px] mb-4">
          <Link className="hover:text-white transition-colors" href="/">
            Dashboard
          </Link>
          <span className="material-symbols-outlined text-[14px]">chevron_right</span>
          <span className="text-white">Search Results</span>
        </div>
        <h1 className="text-[48px] leading-[1.1] tracking-[-0.04em] font-semibold text-white mb-2">
          Search Results
        </h1>
        <p className="text-[18px] leading-[1.6] text-[#c4c7c8]">
          Showing matches for <span className="text-white italic">"{query}"</span>
        </p>
      </section>

      {showDefaultMessage ? (
        <div className="flex flex-col items-center justify-center min-h-[400px]">
          <p className="text-lg text-[#c4c7c8]">Enter a search query to find tickers.</p>
        </div>
      ) : hasNoResults ? (
        <div className="flex flex-col items-center justify-center min-h-[400px]">
          <p className="text-lg text-[#c4c7c8]">No tickers found for your search.</p>
        </div>
      ) : (
        <div className="space-y-8">
          {/* Stats Grid */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-[24px]">
            <div className="bg-[#0e0e0e] border border-[#353534] p-6 rounded-[0.125rem]">
              <p className="text-[12px] text-[#c4c7c8] mb-2 uppercase tracking-widest">
                Total Matches
              </p>
              <p className="text-[32px] leading-[1.2] tracking-[-0.02em] font-medium text-white">
                {data.quotes.length}
              </p>
            </div>
          </div>

          {/* Data Table Section */}
          <section
            className="bg-[#0e0e0e] border border-[#353534] overflow-hidden rounded-[0.125rem]"
            style={{ borderColor: "rgb(53, 53, 52)", transition: "border-color 0.3s" }}
          >
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-[#353534] bg-[#1c1b1b]">
                    <th className="px-6 py-4 text-[12px] text-[#c4c7c8] uppercase tracking-widest">
                      Symbol
                    </th>
                    <th className="px-6 py-4 text-[12px] text-[#c4c7c8] uppercase tracking-widest">
                      Company Name
                    </th>
                    <th className="px-6 py-4 text-[12px] text-[#c4c7c8] uppercase tracking-widest">
                      Exchange
                    </th>
                    <th className="px-6 py-4 text-[12px] text-[#c4c7c8] uppercase tracking-widest">
                      Sector / Industry
                    </th>
                    <th className="px-6 py-4 text-[12px] text-[#c4c7c8] uppercase tracking-widest text-right">
                      Action
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#201f1f]">
                  {data.quotes.map((quote) => (
                    <tr key={quote.symbol} className="hover:bg-[#201f1f] transition-colors group">
                      <td className="px-6 py-6">
                        <span className="text-[24px] leading-[1.3] font-medium text-white group-hover:underline">
                          {quote.symbol}
                        </span>
                      </td>
                      <td className="px-6 py-6">
                        <div className="text-[18px] leading-[1.6] text-white">
                          {quote.longname || quote.shortname}
                        </div>
                        {quote.typeDisp && (
                          <div className="text-[12px] leading-[1.4] text-[#c4c7c8]">
                            {quote.typeDisp}
                          </div>
                        )}
                      </td>
                      <td className="px-6 py-6">
                        <span className="inline-block px-2 py-1 bg-[#2a2a2a] border border-[#444748] rounded-[0.125rem] text-[12px] text-[#c6c6c7]">
                          {quote.exchDisp || quote.exchange}
                        </span>
                      </td>
                      <td className="px-6 py-6 text-[14px] leading-[1.5] text-[#c4c7c8]">
                        {quote.sectorDisp && quote.industryDisp
                          ? `${quote.sectorDisp} / ${quote.industryDisp}`
                          : quote.sectorDisp || quote.industryDisp || "-"}
                      </td>
                      <td className="px-6 py-6 text-right">
                        <button
                          onClick={() => handleViewDetails(quote.symbol)}
                          className="border border-[#444748] px-4 py-2 text-[12px] hover:bg-white hover:text-black transition-all rounded-[0.125rem]"
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

          {/* Pagination Info */}
          <div className="flex justify-between items-center">
            <span className="text-[12px] text-[#c4c7c8]">
              Showing {data.quotes.length} result{data.quotes.length !== 1 ? "s" : ""}
            </span>
          </div>
        </div>
      )}
    </main>
  );
};