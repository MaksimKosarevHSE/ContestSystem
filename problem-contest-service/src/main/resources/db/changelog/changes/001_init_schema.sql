CREATE TABLE IF NOT EXISTS problems (
    id SERIAL PRIMARY KEY,
    creator_id INTEGER NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    title VARCHAR(255) NOT NULL,
    statement TEXT NOT NULL,
    input TEXT,
    output TEXT,
    notes TEXT,
    sample_count INTEGER NOT NULL DEFAULT 0,
    sample_input TEXT[],
    sample_output TEXT[],
    complexity INTEGER NOT NULL,
    compile_time_limit DOUBLE PRECISION NOT NULL,
    time_limit DOUBLE PRECISION NOT NULL,
    memory_limit DOUBLE PRECISION NOT NULL
    );


CREATE TABLE IF NOT EXISTS contests (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author_id INTEGER NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS contest_problem (
    id SERIAL PRIMARY KEY,
   contest_id INTEGER NOT NULL,
   problem_id INTEGER NOT NULL,
   score INTEGER NOT NULL DEFAULT 0
    );


CREATE TABLE IF NOT EXISTS contest_user (
    contest_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    total_score INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT pk_contest_user PRIMARY KEY (contest_id, user_id)
    );


CREATE TABLE IF NOT EXISTS contest_user_task (
     contest_id INTEGER NOT NULL,
     user_id INTEGER NOT NULL,
     task_id INTEGER NOT NULL,
     solved BOOLEAN NOT NULL DEFAULT FALSE,
     attempts INTEGER NOT NULL DEFAULT 0,
     score INTEGER NOT NULL DEFAULT 0,
     solution_time TIMESTAMP,
     CONSTRAINT pk_contest_user_task PRIMARY KEY (contest_id, user_id, task_id)
    );

CREATE TABLE IF NOT EXISTS processed_events (
    event_id UUID PRIMARY KEY
);



-- contest_problem -> contests
ALTER TABLE contest_problem
    ADD CONSTRAINT fk_contest_problem_contest
        FOREIGN KEY (contest_id)
            REFERENCES contests(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;

-- contest_problem -> problems
ALTER TABLE contest_problem
    ADD CONSTRAINT fk_contest_problem_problem
        FOREIGN KEY (problem_id)
            REFERENCES problems(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;

-- contest_user -> contests
ALTER TABLE contest_user
    ADD CONSTRAINT fk_contest_user_contest
        FOREIGN KEY (contest_id)
            REFERENCES contests(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;

-- contest_user_task -> contests
ALTER TABLE contest_user_task
    ADD CONSTRAINT fk_contest_user_task_contest
        FOREIGN KEY (contest_id)
            REFERENCES contests(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;