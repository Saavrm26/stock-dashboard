"""Maps yfinance stock.info dict to TickerDetails protobuf."""

from app.models.generated.ticker_details_pb2 import TickerDetails

# yfinance (camelCase) -> TickerDetails (snake_case)
YF_TO_PB = {
    "symbol": "symbol",
    "address1": "address1",
    "address2": "address2",
    "city": "city",
    "zip": "zip",
    "country": "country",
    "phone": "phone",
    "fax": "fax",
    "website": "website",
    "industry": "industry",
    "industryKey": "industry_key",
    "industryDisp": "industry_disp",
    "sector": "sector",
    "sectorKey": "sector_key",
    "sectorDisp": "sector_disp",
    "longBusinessSummary": "long_business_summary",
    "fullTimeEmployees": "full_time_employees",
    "previousClose": "previous_close",
    "open": "open",
    "dayLow": "day_low",
    "dayHigh": "day_high",
    "regularMarketPreviousClose": "regular_market_previous_close",
    "regularMarketOpen": "regular_market_open",
    "regularMarketDayLow": "regular_market_day_low",
    "regularMarketDayHigh": "regular_market_day_high",
    "dividendRate": "dividend_rate",
    "dividendYield": "dividend_yield",
    "exDividendDate": "ex_dividend_date",
    "payoutRatio": "payout_ratio",
    "fiveYearAvgDividendYield": "five_year_avg_dividend_yield",
    "beta": "beta",
    "trailingPE": "trailing_pe",
    "volume": "volume",
    "regularMarketVolume": "regular_market_volume",
    "averageVolume": "average_volume",
    "averageVolume10days": "average_volume10days",
    "averageDailyVolume10Day": "average_daily_volume10_day",
    "bid": "bid",
    "ask": "ask",
    "marketCap": "market_cap",
    "fiftyTwoWeekLow": "fifty_two_week_low",
    "fiftyTwoWeekHigh": "fifty_two_week_high",
    "priceToSalesTrailing12Months": "price_to_sales_trailing12_months",
    "fiftyDayAverage": "fifty_day_average",
    "twoHundredDayAverage": "two_hundred_day_average",
    "trailingAnnualDividendRate": "trailing_annual_dividend_rate",
    "trailingAnnualDividendYield": "trailing_annual_dividend_yield",
    "currency": "currency",
    "tradeable": "tradeable",
    "enterpriseValue": "enterprise_value",
    "profitMargins": "profit_margins",
    "floatShares": "float_shares",
    "sharesOutstanding": "shares_outstanding",
    "heldPercentInsiders": "held_percent_insiders",
    "heldPercentInstitutions": "held_percent_institutions",
    "impliedSharesOutstanding": "implied_shares_outstanding",
    "bookValue": "book_value",
    "priceToBook": "price_to_book",
    "lastFiscalYearEnd": "last_fiscal_year_end",
    "nextFiscalYearEnd": "next_fiscal_year_end",
    "mostRecentQuarter": "most_recent_quarter",
    "earningsQuarterlyGrowth": "earnings_quarterly_growth",
    "netIncomeToCommon": "net_income_to_common",
    "trailingEps": "trailing_eps",
    "enterpriseToRevenue": "enterprise_to_revenue",
    "enterpriseToEbitda": "enterprise_to_ebitda",
    "52WeekChange": "_52_week_change",
    "sandP52WeekChange": "sand_p52_week_change",
    "lastDividendValue": "last_dividend_value",
    "lastDividendDate": "last_dividend_date",
    "quoteType": "quote_type",
    "currentPrice": "current_price",
    "recommendationKey": "recommendation_key",
    "totalCash": "total_cash",
    "totalCashPerShare": "total_cash_per_share",
    "ebitda": "ebitda",
    "totalDebt": "total_debt",
    "totalRevenue": "total_revenue",
    "debtToEquity": "debt_to_equity",
    "revenuePerShare": "revenue_per_share",
    "grossProfits": "gross_profits",
    "earningsGrowth": "earnings_growth",
    "revenueGrowth": "revenue_growth",
    "grossMargins": "gross_margins",
    "ebitdaMargins": "ebitda_margins",
    "operatingMargins": "operating_margins",
    "financialCurrency": "financial_currency",
    "language": "language",
    "region": "region",
    "typeDisp": "type_disp",
    "quoteSourceName": "quote_source_name",
    "triggerable": "triggerable",
    "hasPrePostMarketData": "has_pre_post_market_data",
    "firstTradeDateMilliseconds": "first_trade_date_milliseconds",
    "regularMarketChange": "regular_market_change",
    "regularMarketDayRange": "regular_market_day_range",
    "fullExchangeName": "full_exchange_name",
    "averageDailyVolume3Month": "average_daily_volume3_month",
    "fiftyTwoWeekLowChange": "fifty_two_week_low_change",
    "fiftyTwoWeekLowChangePercent": "fifty_two_week_low_change_percent",
    "fiftyTwoWeekRange": "fifty_two_week_range",
    "fiftyTwoWeekHighChange": "fifty_two_week_high_change",
    "fiftyTwoWeekHighChangePercent": "fifty_two_week_high_change_percent",
    "fiftyTwoWeekChangePercent": "fifty_two_week_change_percent",
    "earningsTimestampStart": "earnings_timestamp_start",
    "earningsTimestampEnd": "earnings_timestamp_end",
    "isEarningsDateEstimate": "is_earnings_date_estimate",
    "exchange": "exchange",
    "messageBoardId": "message_board_id",
    "exchangeTimezoneName": "exchange_timezone_name",
    "exchangeTimezoneShortName": "exchange_timezone_short_name",
    "gmtOffSetMilliseconds": "gmt_off_set_milliseconds",
    "market": "market",
    "esgPopulated": "esg_populated",
    "regularMarketTime": "regular_market_time",
    "marketState": "market_state",
    "regularMarketChangePercent": "regular_market_change_percent",
    "regularMarketPrice": "regular_market_price",
    "epsTrailingTwelveMonths": "eps_trailing_twelve_months",
    "fiftyDayAverageChange": "fifty_day_average_change",
    "fiftyDayAverageChangePercent": "fifty_day_average_change_percent",
    "twoHundredDayAverageChange": "two_hundred_day_average_change",
    "twoHundredDayAverageChangePercent": "two_hundred_day_average_change_percent",
    "sourceInterval": "source_interval",
    "exchangeDataDelayedBy": "exchange_data_delayed_by",
    "prevName": "prev_name",
    "nameChangeDate": "name_change_date",
    "cryptoTradeable": "crypto_tradeable",
    "shortName": "short_name",
    "trailingPegRatio": "trailing_peg_ratio",
    "maxAge": "max_age",
    "priceHint": "price_hint",
    "compensationAsOfEpochDate": "compensation_as_of_epoch_date",
    "nonDilutedMarketCap": "non_diluted_market_cap",
    "allTimeHigh": "all_time_high",
    "allTimeLow": "all_time_low",
    "customPriceAlertConfidence": "custom_price_alert_confidence",
}


def _set_field(pb: TickerDetails, field: str, value) -> None:
    """Set a protobuf field, skipping None and handling type mismatches."""
    if value is None or not hasattr(pb, field):
        return
    try:
        setattr(pb, field, value)
    except (TypeError, ValueError):
        try:
            if isinstance(value, (int, float)):
                setattr(pb, field, int(value))
            else:
                setattr(pb, field, float(value) if "." in str(value) else int(value))
        except (TypeError, ValueError):
            pass


def from_yfinance(info: dict) -> TickerDetails:
    """Populate TickerDetails from yfinance stock.info dict."""
    pb = TickerDetails()
    for yf_key, pb_field in YF_TO_PB.items():
        if yf_key in info:
            _set_field(pb, pb_field, info[yf_key])
    _map_officers(pb, info)
    return pb


def _map_officers(pb: TickerDetails, info: dict) -> None:
    """Map companyOfficers and executiveTeam from yfinance to protobuf Officer messages."""
    for key, pb_attr in [("companyOfficers", "company_officers"), ("executiveTeam", "executive_team")]:
        officers = info.get(key)
        if not isinstance(officers, list):
            continue
        for o in officers:
            if not isinstance(o, dict):
                continue
            officer = getattr(pb, pb_attr).add()
            for yf_k, pb_f in [
                ("maxAge", "max_age"),
                ("name", "name"),
                ("age", "age"),
                ("title", "title"),
                ("yearBorn", "year_born"),
                ("fiscalYear", "fiscal_year"),
                ("totalPay", "total_pay"),
                ("exercisedValue", "exercised_value"),
                ("unexercisedValue", "unexercised_value"),
            ]:
                if yf_k in o and o[yf_k] is not None:
                    try:
                        setattr(officer, pb_f, o[yf_k])
                    except (TypeError, ValueError):
                        pass
