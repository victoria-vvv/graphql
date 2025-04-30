CREATE TABLE account (
                          id SERIAL PRIMARY KEY,
                          user_id INTEGER NOT NULL,
                          account_type VARCHAR(20),
                          currency VARCHAR(3),
                          balance NUMERIC DEFAULT 0,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);