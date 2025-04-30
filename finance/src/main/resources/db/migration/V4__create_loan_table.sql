CREATE TABLE loan (
                       id SERIAL PRIMARY KEY,
                       user_id INTEGER NOT NULL,
                       principal_amount NUMERIC,
                       interest_rate FLOAT,
                       start_date DATE,
                       end_date DATE,
                       status VARCHAR(20),
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);