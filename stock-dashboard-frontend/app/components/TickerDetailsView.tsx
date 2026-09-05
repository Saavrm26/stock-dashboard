import React from "react";
import { TickerDetails } from "@/model/generated/v1/ticker_details";
import { ColoredIcon } from "./ColoredIcon";
import arrowDropUpIcon from "@material-symbols/svg-400/rounded/arrow_drop_up.svg";
import arrowDropDownIcon from "@material-symbols/svg-400/rounded/arrow_drop_down.svg";
import analyticsIcon from "@material-symbols/svg-400/rounded/analytics.svg";
import languageIcon from "@material-symbols/svg-400/rounded/language.svg";

interface Props {
  details: TickerDetails;
}

export const TickerDetailsView: React.FC<Props> = ({ details }) => {
  const formatNumber = (value: number | undefined, decimals: number = 2): string => {
    if (value === undefined) return "N/A";
    return value.toFixed(decimals);
  };

  const formatLargeNumber = (value: number | undefined): string => {
    if (value === undefined) return "N/A";
    if (value >= 1e12) return `${(value / 1e12).toFixed(2)}T`;
    if (value >= 1e9) return `${(value / 1e9).toFixed(2)}B`;
    if (value >= 1e6) return `${(value / 1e6).toFixed(2)}M`;
    if (value >= 1e3) return `${(value / 1e3).toFixed(2)}K`;
    return value.toFixed(2);
  };

  const formatPercentage = (value: number | undefined): string => {
    if (value === undefined) return "N/A";
    return `${value.toFixed(2)}%`;
  };

  const getChangeColor = (value: number | undefined): string => {
    if (value === undefined) return "text-on-surface-variant";
    return value >= 0 ? "text-primary" : "text-error";
  };

  const getChangeIcon = (value: number | undefined): string => {
    if (value === undefined) return arrowDropUpIcon;
    return value >= 0 ? arrowDropUpIcon : arrowDropDownIcon;
  };

  return (
    <div className="flex flex-col min-h-screen">
      <main className="max-w-[1440px] mx-auto px-[64px] py-12 space-y-[120px] pt-20">
        {/* Header Section */}
        <section className="border-b border-outline-variant pb-10">
          <p className="font-mono text-xs font-semibold uppercase tracking-widest text-on-surface-variant">{details.exchange}</p>
          <h1 className="mt-2 font-mono text-5xl font-semibold tracking-tight text-on-surface">{details.symbol}</h1>
          {details.shortName && (
            <p className="mt-2 text-lg text-on-surface-variant">{details.shortName}</p>
          )}
          <p className="mt-8 font-mono text-4xl font-semibold text-primary">{formatNumber(details.currentPrice)} {details.currency || "INR"}</p>
          {details.regularMarketChange !== undefined && details.regularMarketChangePercent !== undefined && (
            <div className={`flex items-center gap-2 ${getChangeColor(details.regularMarketChange)}`}>
              <ColoredIcon
                src={getChangeIcon(details.regularMarketChange)}
                alt="change"
                className={getChangeColor(details.regularMarketChange)}
              />
              <span className="font-mono text-lg font-bold">
                {details.regularMarketChange >= 0 ? "+" : ""}
                {formatNumber(details.regularMarketChange)} ({formatPercentage(details.regularMarketChangePercent)})
              </span>
            </div>
          )}
          <div className="mt-4 flex flex-wrap gap-2">
            {details.sector && (
              <span className="border border-outline-variant px-2 py-1 text-xs text-on-surface-variant">
                {details.sector}
              </span>
            )}
            {details.industry && (
              <span className="border border-outline-variant px-2 py-1 text-xs text-on-surface-variant">
                {details.industry}
              </span>
            )}
          </div>
        </section>

        {/* Metric Grid (Primary Stats) */}
        <section className="grid grid-cols-1 gap-4 md:grid-cols-3 lg:grid-cols-6">
          <div className="border border-outline-variant bg-white p-6 shadow-sm">
            <p className="mb-2 text-xs uppercase text-on-surface-variant">Market Cap</p>
            <p className="font-mono text-2xl font-medium text-primary">
              {formatLargeNumber(details.marketCap)}
            </p>
          </div>
          <div className="border border-outline-variant bg-white p-6 shadow-sm">
            <p className="mb-2 text-xs uppercase text-on-surface-variant">Trailing P/E</p>
            <p className="font-mono text-2xl font-medium text-primary">
              {formatNumber(details.trailingPe)}
            </p>
          </div>
          <div className="border border-outline-variant bg-white p-6 shadow-sm">
            <p className="mb-2 text-xs uppercase text-on-surface-variant">Dividend Yield</p>
            <p className="font-mono text-2xl font-medium text-primary">
              {formatPercentage(details.dividendYield)}
            </p>
          </div>
          <div className="border border-outline-variant bg-white p-6 shadow-sm">
            <p className="mb-2 text-xs uppercase text-on-surface-variant">52W High</p>
            <p className="font-mono text-2xl font-medium text-primary">
              {formatNumber(details.fiftyTwoWeekHigh)}
            </p>
          </div>
          <div className="border border-outline-variant bg-white p-6 shadow-sm">
            <p className="mb-2 text-xs uppercase text-on-surface-variant">52W Low</p>
            <p className="font-mono text-2xl font-medium text-primary">
              {formatNumber(details.fiftyTwoWeekLow)}
            </p>
          </div>
          <div className="border border-outline-variant bg-white p-6 shadow-sm">
            <p className="mb-2 text-xs uppercase text-on-surface-variant">Avg Volume</p>
            <p className="font-mono text-2xl font-medium text-primary">
              {formatLargeNumber(details.averageVolume)}
            </p>
          </div>
        </section>

        {/* Performance Section (Bento Style) */}
        <section className="grid grid-cols-1 lg:grid-cols-12 gap-[24px]">
          <div className="flex flex-col gap-[24px] lg:col-span-12 md:flex-row">
            <div className="flex-1 border border-outline-variant bg-white p-6 shadow-sm">
              <div className="flex justify-between items-start mb-4">
                <span className="font-mono text-xs uppercase text-on-surface-variant">Beta (5Y Monthly)</span>
                <ColoredIcon src={analyticsIcon} alt="analytics" className="text-on-surface-variant" />
              </div>
              <div className="font-mono text-[32px] font-medium text-primary">
                {formatNumber(details.beta)}
              </div>
              <p className="mt-2 text-xs leading-[1.4] text-on-surface-variant">
                {details.beta !== undefined && details.beta < 1 ? "Low volatility relative to the market." : "High volatility relative to the market."}
              </p>
            </div>
            <div className="flex-1 border border-outline-variant bg-white p-6 shadow-sm">
              <div className="flex justify-between items-start mb-4">
                <span className="font-mono text-xs uppercase text-on-surface-variant">52 Week Change</span>
                <ColoredIcon 
                  src={getChangeIcon(details.week52Change)} 
                  alt="change" 
                  className={getChangeColor(details.week52Change)}
                />
              </div>
              <div className={`font-mono text-[32px] font-medium ${getChangeColor(details.week52Change)}`}>
                {formatPercentage(details.week52Change)}
              </div>
              {details.trailingEps !== undefined && (
                <div className="mt-4 flex justify-between text-on-surface-variant">
                    <span className="font-mono text-xs uppercase">Trailing EPS</span>
                    <span className="font-mono text-xs text-primary">{formatNumber(details.trailingEps)}</span>
                </div>
              )}
            </div>
          </div>
        </section>

        {/* Technical Averages */}
        <section className="grid grid-cols-1 md:grid-cols-2 gap-[24px]">
          <div className="flex items-center justify-between border border-outline-variant bg-white p-6 shadow-sm">
            <div>
              <p className="mb-1 text-xs uppercase text-on-surface-variant">50-Day Moving Average</p>
              <p className="font-mono text-2xl font-medium text-primary">
                {formatNumber(details.fiftyDayAverage)}
              </p>
            </div>
            {details.currentPrice !== undefined && details.fiftyDayAverage !== undefined && (
              <span className={`font-mono text-xs ${getChangeColor(details.currentPrice - details.fiftyDayAverage)}`}>
                {formatPercentage(((details.currentPrice - details.fiftyDayAverage) / details.fiftyDayAverage) * 100)} vs Current
              </span>
            )}
          </div>
          <div className="flex items-center justify-between border border-outline-variant bg-white p-6 shadow-sm">
            <div>
              <p className="mb-1 text-xs uppercase text-on-surface-variant">200-Day Moving Average</p>
              <p className="font-mono text-2xl font-medium text-primary">
                {formatNumber(details.twoHundredDayAverage)}
              </p>
            </div>
            {details.currentPrice !== undefined && details.twoHundredDayAverage !== undefined && (
              <span className={`font-mono text-xs ${getChangeColor(details.currentPrice - details.twoHundredDayAverage)}`}>
                {formatPercentage(((details.currentPrice - details.twoHundredDayAverage) / details.twoHundredDayAverage) * 100)} vs Current
              </span>
            )}
          </div>
        </section>

        {/* About Section */}
        <section className="grid grid-cols-1 lg:grid-cols-3 gap-16">
          <div className="lg:col-span-2 space-y-8">
            <h2 className="border-b border-outline-variant pb-4 text-3xl font-medium uppercase text-primary">
              Business Summary
            </h2>
            {details.longBusinessSummary && (
              <div className="border border-outline-variant bg-white p-6 text-lg leading-relaxed text-on-surface-variant shadow-sm">
                <p>{details.longBusinessSummary}</p>
              </div>
            )}
            <div className="pt-12">
              <h2 className="mb-8 border-b border-outline-variant pb-4 text-3xl font-medium uppercase text-primary">
                Corporate Leadership
              </h2>
              <div className="overflow-x-auto border border-outline-variant bg-white p-6 shadow-sm">
                <table className="w-full text-left">
                  <thead className="border-b border-outline-variant bg-slate-50">
                    <tr>
                      <th className="py-4 text-xs uppercase text-on-surface-variant">Name</th>
                      <th className="py-4 text-xs uppercase text-on-surface-variant">Title</th>
                      <th className="py-4 text-right text-xs uppercase text-on-surface-variant">Age</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {details.companyOfficers && details.companyOfficers.length > 0 ? (
                      details.companyOfficers.slice(0, 5).map((officer, index) => (
                        <tr key={index}>
                          <td className="py-4 text-sm leading-[1.5] text-on-surface">{officer.name}</td>
                          <td className="py-4 text-sm leading-[1.5] text-on-surface">{officer.title}</td>
                          <td className="py-4 text-right text-xs text-on-surface">{officer.age}</td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan={3} className="py-4 text-center text-sm leading-[1.5] text-on-surface-variant">
                          No leadership information available
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          <div className="space-y-12">
            <div className="space-y-6 border border-outline-variant bg-white p-6 shadow-sm">
              <h3 className="text-xs uppercase text-on-surface-variant">Corporate Identity</h3>
              <div className="space-y-4">
                {details.website && (
                  <div className="flex gap-4 items-center">
                    <ColoredIcon src={languageIcon} alt="language" className="text-on-surface-variant" />
                    <a
                      className="text-sm text-on-surface hover:underline decoration-1"
                      href={details.website}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      {details.website}
                    </a>
                  </div>
                )}
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
};
