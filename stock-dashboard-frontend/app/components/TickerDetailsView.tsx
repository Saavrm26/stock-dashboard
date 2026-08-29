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
        {/*d Header Section */}
        <section className="space-y-8">
          <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
            <div className="space-y-2">
              <div className="flex items-center gap-3">
                {details.exchange && (
                  <span className="font-label-mono text-[12px] px-2 py-0.5 bg-surface-container-high text-on-surface-variant rounded-sm">
                    {details.exchange}
                  </span>
                )}
                <h1 className="font-display text-[48px] uppercase leading-[1.1] tracking-[-0.04em] font-semibold text-primary">
                  {details.symbol}
                </h1>
              </div>
              {details.shortName && (
                <p className="font-headline-md text-[24px] leading-[1.3] font-medium text-on-surface-variant">
                  {details.shortName}
                </p>
              )}
              <div className="flex flex-wrap gap-2 pt-2">
                {details.sector && (
                  <span className="font-label-mono text-[12px] px-2 py-1 bg-surface-container text-outline border border-outline-variant rounded-[2px]">
                    {details.sector}
                  </span>
                )}
                {details.industry && (
                  <span className="font-label-mono text-[12px] px-2 py-1 bg-surface-container text-outline border border-outline-variant rounded-[2px]">
                    {details.industry}
                  </span>
                )}
              </div>
            </div>
            <div className="text-right">
              <div className="font-display text-[48px] text-primary font-label-mono leading-[1.1] tracking-[-0.04em] font-semibold">
                {formatNumber(details.currentPrice)}{" "}
                <span className="text-[24px] font-normal text-on-surface-variant ml-2">
                  {details.currency || "INR"}
                </span>
              </div>
              {details.regularMarketChange !== undefined && details.regularMarketChangePercent !== undefined && (
                <div className={`flex items-center justify-end gap-2 ${getChangeColor(details.regularMarketChange)}`}>
                  <ColoredIcon 
                    src={getChangeIcon(details.regularMarketChange)} 
                    alt="change" 
                    className={getChangeColor(details.regularMarketChange)}
                  />
                  <span className="font-label-mono text-[18px] leading-[1.6] font-bold">
                    {details.regularMarketChange >= 0 ? "+" : ""}
                    {formatNumber(details.regularMarketChange)} ({formatPercentage(details.regularMarketChangePercent)})
                  </span>
                </div>
              )}
            </div>
          </div>
        </section>

        {/* Metric Grid (Primary Stats) */}
        <section className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-6 gap-0 border border-outline-variant">
          <div className="p-6 border-b lg:border-b-0 lg:border-r border-outline-variant bg-surface-container-lowest">
            <p className="font-label-mono text-[12px] text-outline uppercase mb-2">Market Cap</p>
            <p className="font-headline-md text-[24px] leading-[1.3] font-medium text-primary font-label-mono">
              {formatLargeNumber(details.marketCap)}
            </p>
          </div>
          <div className="p-6 border-b lg:border-b-0 lg:border-r border-outline-variant bg-surface-container-lowest">
            <p className="font-label-mono text-[12px] text-outline uppercase mb-2">Trailing P/E</p>
            <p className="font-headline-md text-[24px] leading-[1.3] font-medium text-primary font-label-mono">
              {formatNumber(details.trailingPe)}
            </p>
          </div>
          <div className="p-6 border-b lg:border-b-0 lg:border-r border-outline-variant bg-surface-container-lowest">
            <p className="font-label-mono text-[12px] text-outline uppercase mb-2">Dividend Yield</p>
            <p className="font-headline-md text-[24px] leading-[1.3] font-medium text-primary font-label-mono">
              {formatPercentage(details.dividendYield)}
            </p>
          </div>
          <div className="p-6 border-b lg:border-b-0 lg:border-r border-outline-variant bg-surface-container-lowest">
            <p className="font-label-mono text-[12px] text-outline uppercase mb-2">52W High</p>
            <p className="font-headline-md text-[24px] leading-[1.3] font-medium text-primary font-label-mono">
              {formatNumber(details.fiftyTwoWeekHigh)}
            </p>
          </div>
          <div className="p-6 border-b lg:border-b-0 lg:border-r border-outline-variant bg-surface-container-lowest">
            <p className="font-label-mono text-[12px] text-outline uppercase mb-2">52W Low</p>
            <p className="font-headline-md text-[24px] leading-[1.3] font-medium text-primary font-label-mono">
              {formatNumber(details.fiftyTwoWeekLow)}
            </p>
          </div>
          <div className="p-6 bg-surface-container-lowest">
            <p className="font-label-mono text-[12px] text-outline uppercase mb-2">Avg Volume</p>
            <p className="font-headline-md text-[24px] leading-[1.3] font-medium text-primary font-label-mono">
              {formatLargeNumber(details.averageVolume)}
            </p>
          </div>
        </section>

        {/* Performance Section (Bento Style) */}
        <section className="grid grid-cols-1 lg:grid-cols-12 gap-[24px]">
          <div className="flex gap-[24px] lg:col-span-12 md:flex-row">
            <div className="flex-1 bg-surface-container-low border border-outline-variant p-6">
              <div className="flex justify-between items-start mb-4">
                <span className="font-label-mono text-[12px] text-outline uppercase">Beta (5Y Monthly)</span>
                <ColoredIcon src={analyticsIcon} alt="analytics" className="text-on-surface-variant" />
              </div>
              <div className="font-display text-[32px] leading-[1.2] tracking-[-0.02em] font-medium text-primary font-label-mono">
                {formatNumber(details.beta)}
              </div>
              <p className="font-caption text-[12px] leading-[1.4] text-on-surface-variant mt-2">
                {details.beta !== undefined && details.beta < 1 ? "Low volatility relative to the market." : "High volatility relative to the market."}
              </p>
            </div>
            <div className="flex-1 bg-surface-container-low border border-outline-variant p-6">
              <div className="flex justify-between items-start mb-4">
                <span className="font-label-mono text-[12px] text-outline uppercase">52 Week Change</span>
                <ColoredIcon 
                  src={getChangeIcon(details.week52Change)} 
                  alt="change" 
                  className={getChangeColor(details.week52Change)}
                />
              </div>
              <div className={`font-display text-[32px] leading-[1.2] tracking-[-0.02em] font-medium font-label-mono ${getChangeColor(details.week52Change)}`}>
                {formatPercentage(details.week52Change)}
              </div>
              {details.trailingEps !== undefined && (
                <div className="mt-4 flex justify-between text-on-surface-variant">
                  <span className="font-label-mono text-[12px] uppercase">Trailing EPS</span>
                  <span className="font-label-mono text-[12px] text-primary">{formatNumber(details.trailingEps)}</span>
                </div>
              )}
            </div>
          </div>
        </section>

        {/* Technical Averages */}
        <section className="grid grid-cols-1 md:grid-cols-2 gap-[24px]">
          <div className="p-8 border border-outline-variant flex items-center justify-between">
            <div>
              <p className="font-label-mono text-[12px] text-outline uppercase mb-1">50-Day Moving Average</p>
              <p className="font-headline-md text-[24px] leading-[1.3] font-medium text-primary font-label-mono">
                {formatNumber(details.fiftyDayAverage)}
              </p>
            </div>
            {details.currentPrice !== undefined && details.fiftyDayAverage !== undefined && (
              <span className={`font-label-mono text-[12px] ${getChangeColor(details.currentPrice - details.fiftyDayAverage)}`}>
                {formatPercentage(((details.currentPrice - details.fiftyDayAverage) / details.fiftyDayAverage) * 100)} vs Current
              </span>
            )}
          </div>
          <div className="p-8 border border-outline-variant flex items-center justify-between">
            <div>
              <p className="font-label-mono text-[12px] text-outline uppercase mb-1">200-Day Moving Average</p>
              <p className="font-headline-md text-[24px] leading-[1.3] font-medium text-primary font-label-mono">
                {formatNumber(details.twoHundredDayAverage)}
              </p>
            </div>
            {details.currentPrice !== undefined && details.twoHundredDayAverage !== undefined && (
              <span className={`font-label-mono text-[12px] ${getChangeColor(details.currentPrice - details.twoHundredDayAverage)}`}>
                {formatPercentage(((details.currentPrice - details.twoHundredDayAverage) / details.twoHundredDayAverage) * 100)} vs Current
              </span>
            )}
          </div>
        </section>

        {/* About Section */}
        <section className="grid grid-cols-1 lg:grid-cols-3 gap-16">
          <div className="lg:col-span-2 space-y-8">
            <h2 className="font-headline-lg text-[32px] leading-[1.2] tracking-[-0.02em] font-medium text-primary uppercase border-b border-outline-variant pb-4">
              Business Summary
            </h2>
            {details.longBusinessSummary && (
              <div className="font-body-lg text-[18px] leading-[1.6] text-on-surface-variant leading-relaxed space-y-4">
                <p>{details.longBusinessSummary}</p>
              </div>
            )}
            <div className="pt-12">
              <h2 className="font-headline-lg text-[32px] leading-[1.2] tracking-[-0.02em] font-medium text-primary uppercase border-b border-outline-variant pb-4 mb-8">
                Corporate Leadership
              </h2>
              <div className="overflow-x-auto">
                <table className="w-full text-left">
                  <thead className="border-b border-surface-container-highest">
                    <tr>
                      <th className="py-4 font-label-mono text-[12px] text-outline uppercase">Name</th>
                      <th className="py-4 font-label-mono text-[12px] text-outline uppercase">Title</th>
                      <th className="py-4 font-label-mono text-[12px] text-outline uppercase text-right">Age</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-surface-container-high">
                    {details.companyOfficers && details.companyOfficers.length > 0 ? (
                      details.companyOfficers.slice(0, 5).map((officer, index) => (
                        <tr key={index}>
                          <td className="py-4 font-body-md text-[14px] leading-[1.5] text-primary">{officer.name}</td>
                          <td className="py-4 font-body-md text-[14px] leading-[1.5] text-on-surface-variant">{officer.title}</td>
                          <td className="py-4 font-label-mono text-[12px] text-on-surface-variant text-right">{officer.age}</td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan={3} className="py-4 font-body-md text-[14px] leading-[1.5] text-on-surface-variant text-center">
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
            <div className="p-8 bg-surface-container-low border border-outline-variant space-y-6">
              <h3 className="font-label-mono text-[12px] text-primary uppercase">Corporate Identity</h3>
              <div className="space-y-4">
                {details.website && (
                  <div className="flex gap-4 items-center">
                    <ColoredIcon src={languageIcon} alt="language" className="text-outline" />
                    <a
                      className="font-label-mono text-[12px] text-primary hover:underline decoration-1"
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