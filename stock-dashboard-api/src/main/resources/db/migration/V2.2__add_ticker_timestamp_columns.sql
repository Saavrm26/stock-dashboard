ALTER TABLE tickers ADD COLUMN created_at timestamptz NOT NULL DEFAULT now();
ALTER TABLE tickers ADD COLUMN updated_at timestamptz NOT NULL DEFAULT now();

CREATE TRIGGER update_ticker_modtime
    BEFORE UPDATE ON tickers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();