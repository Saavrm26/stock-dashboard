import React from "react";
import { TickerDetails } from "@/model/generated/v1/ticker_details";

interface Props {
  details: TickerDetails;
}

export const TickerDetailsView: React.FC<Props> = ({ details }) => {
  return (
    <section className="max-w-4xl mx-auto p-4">
      <h2 className="text-2xl font-bold mb-4">{details.symbol}</h2>

      {/* Primary Info */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        {details.currentPrice !== undefined && (
          <div>
            <span className="font-medium">Current Price:</span>{" "}
            <span>{details.currentPrice}</span>
          </div>
        )}
        {details.longBusinessSummary && (
          <div>
            <span className="font-medium">Summary:</span>{" "}
            <p className="mt-1">{details.longBusinessSummary}</p>
          </div>
        )}
        {details.industry && (
          <div>
            <span className="font-medium">Industry:</span>{" "}
            <span>{details.industry}</span>
          </div>
        )}
        {details.sector && (
          <div>
            <span className="font-medium">Sector:</span>{" "}
            <span>{details.sector}</span>
          </div>
        )}
        {details.website && (
          <div>
            <span className="font-medium">Website:</span>{" "}
            <a
              href={details.website}
              target="_blank"
              rel="noopener noreferrer"
              className="text-blue-600 hover:underline"
            >
              {details.website}
            </a>
          </div>
        )}
      </div>

      {/* Optional numeric fields – render only when defined */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
        {details.marketCap !== undefined && (
          <div>
            <span className="font-medium">Market Cap:</span>{" "}
            <span>{details.marketCap}</span>
          </div>
        )}
        {details.beta !== undefined && (
          <div>
            <span className="font-medium">Beta:</span>{" "}
            <span>{details.beta}</span>
          </div>
        )}
        {details.dividendYield !== undefined && (
          <div>
            <span className="font-medium">Dividend Yield:</span>{" "}
            <span>{details.dividendYield}</span>
          </div>
        )}
        {details.trailingPe !== undefined && (
          <div>
            <span className="font-medium">PE Ratio:</span>{" "}
            <span>{details.trailingPe}</span>
          </div>
        )}
        {details.trailingPegRatio !== undefined && (
          <div>
            <span className="font-medium">Trailing PEG Ratio:</span>{" "}
            <span>{details.trailingPegRatio}</span>
          </div>
        )}
      </div>
    </section>
  );
};