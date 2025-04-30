CREATE TABLE loan_payment (
                               id SERIAL PRIMARY KEY,
                               loan_id INTEGER NOT NULL REFERENCES loan(id) ON DELETE CASCADE,
                               payment_date DATE,
                               amount NUMERIC,
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);