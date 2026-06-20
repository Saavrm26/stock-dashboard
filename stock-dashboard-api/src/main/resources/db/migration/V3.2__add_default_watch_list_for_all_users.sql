INSERT INTO
  watch_list (name, description, created_by)
SELECT
  'My watchlist',
  'My personal watchlist',
  id
FROM
  users;
