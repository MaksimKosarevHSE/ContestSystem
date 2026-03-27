CREATE TABLE IF NOT EXISTS submissions (
   id BIGSERIAL PRIMARY KEY,
   user_id INTEGER NOT NULL,
   problem_id INTEGER NOT NULL,
   contest_id INTEGER,
   is_upsolving BOOLEAN NOT NULL DEFAULT FALSE,
   time TIMESTAMP NOT NULL,
   source TEXT NOT NULL,
   language varchar(255) NOT NULL,
   status varchar(255) NOT NULL,
   execution_time INTEGER,
   used_memory INTEGER,
   test_num INTEGER,
   checker_message TEXT
);

CREATE TABLE IF NOT EXISTS outbox_event (
    id UUID PRIMARY KEY,
    event_id UUID,
    event_type TEXT,
    payload TEXT
)