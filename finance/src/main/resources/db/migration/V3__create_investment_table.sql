CREATE TABLE investment (
                             id SERIAL PRIMARY KEY,
                             user_id INTEGER NOT NULL,
                             investment_type VARCHAR(50),
                             amount NUMERIC,
                             start_date DATE,
                             expiration_date DATE,
                             interest_rate FLOAT,
                             created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);