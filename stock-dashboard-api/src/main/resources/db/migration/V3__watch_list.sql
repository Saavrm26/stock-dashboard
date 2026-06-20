CREATE TABLE watch_list (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    created_by TEXT,
    visibility TEXT NOT NULL DEFAULT 'PRIVATE',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    type TEXT NOT NULL DEFAULT 'FIXED',
    screen_query TEXT,
    search_vector tsvector
    GENERATED ALWAYS AS (
        to_tsvector('english'::regconfig, coalesce(name, '')) ||
        to_tsvector('english'::regconfig, coalesce(description, ''))
    ) STORED
);


CREATE TABLE watch_list_tickers (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    watch_list_id bigint NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    ticker_code TEXT NOT NULL,
    ticker_long_name TEXT NOT NULL,
    ticker_exchange TEXT NOT NULL,
    ticker_details JSONB NOT NULL,
    FOREIGN KEY (watch_list_id) REFERENCES watch_list(id) ON DELETE CASCADE
);

-- ticker details jsonb index
CREATE INDEX idx_watch_list_tickers_gin_ticker_details ON watch_list_tickers USING GIN (ticker_details jsonb_path_ops);

-- index on watch_list_id and ticker_code for faster lookups
CREATE UNIQUE INDEX idx_watch_list_tickers_watch_list_id_ticker_code ON watch_list_tickers (watch_list_id, ticker_code);

-- full-text search index on name and description
CREATE INDEX idx_watch_list_fts_name_description ON watch_list USING GIN (search_vector);

CREATE TRIGGER update_watch_list_modtime
    BEFORE UPDATE ON watch_list
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_watch_list_tickers_modtime
    BEFORE UPDATE ON watch_list_tickers
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
