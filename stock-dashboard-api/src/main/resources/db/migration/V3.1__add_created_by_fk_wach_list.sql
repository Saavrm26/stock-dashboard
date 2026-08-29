ALTER TABLE watch_list
ADD CONSTRAINT fk_watch_list_users
FOREIGN KEY (created_by) REFERENCES
users(id);
