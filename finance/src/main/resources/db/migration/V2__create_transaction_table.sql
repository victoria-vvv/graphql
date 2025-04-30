CREATE TABLE transaction (
                              id SERIAL PRIMARY KEY,
                              account_id INTEGER NOT NULL REFERENCES account(id) ON DELETE CASCADE,
                              amount NUMERIC NOT NULL,
                              transaction_type VARCHAR(10) CHECK (transaction_type IN ('debit', 'credit')),
                              category VARCHAR(50),
                              description TEXT,
                              transaction_date DATE DEFAULT CURRENT_DATE,
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);