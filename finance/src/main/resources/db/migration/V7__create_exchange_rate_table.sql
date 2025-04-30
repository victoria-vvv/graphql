CREATE TABLE exchange_rate (
                                id SERIAL PRIMARY KEY,
                                base_currency VARCHAR(3) REFERENCES currency(currency_code),
                                target_currency VARCHAR(3) REFERENCES currency(currency_code),
                                rate FLOAT,
                                rate_date DATE DEFAULT CURRENT_DATE
);