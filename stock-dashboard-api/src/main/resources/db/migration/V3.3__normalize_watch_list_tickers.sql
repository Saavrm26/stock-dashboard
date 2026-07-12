ALTER TABLE tickers
    RENAME COLUMN ticker_metadata TO ticker_details;

ALTER TABLE tickers
    DROP CONSTRAINT tickers_pkey;

ALTER TABLE tickers
    DROP COLUMN id;

ALTER TABLE tickers
    ALTER COLUMN ticker_code SET NOT NULL;

ALTER TABLE tickers
    ADD CONSTRAINT tickers_pkey PRIMARY KEY (ticker_code);

INSERT INTO tickers (
    ticker_code,
    ticker_long_name,
    ticker_exchange,
    ticker_details
)
SELECT DISTINCT ON (ticker_code)
    ticker_code,
    ticker_long_name,
    ticker_exchange,
    ticker_details
FROM watch_list_tickers
ORDER BY ticker_code
ON CONFLICT (ticker_code) DO NOTHING;

DROP INDEX IF EXISTS idx_watch_list_tickers_gin_ticker_details;

ALTER TABLE watch_list_tickers
    DROP COLUMN ticker_details,
    DROP COLUMN ticker_exchange,
    DROP COLUMN ticker_long_name;

ALTER TABLE watch_list_tickers
    ADD CONSTRAINT fk_watch_list_tickers_tickers
        FOREIGN KEY (ticker_code)
            REFERENCES tickers (ticker_code);

CREATE INDEX idx_tickers_gin_ticker_details
    ON tickers
        USING GIN (ticker_details jsonb_path_ops);
