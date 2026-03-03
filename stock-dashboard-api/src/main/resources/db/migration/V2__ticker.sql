CREATE TABLE tickers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticker_code TEXT NOT NULL,
    ticker_long_name TEXT NOT NULL,
    ticker_exchange TEXT NOT NULL,
    ticker_metadata JSONB NOT NULL
);

CREATE INDEX ON tickers USING gin (ticker_metadata);

CREATE INDEX ON tickers (lower(ticker_long_name) text_pattern_ops);