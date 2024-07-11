ALTER TABLE users
    DROP COLUMN stripe_customer_id,
    DROP COLUMN stripe_subscription_id,
    DROP COLUMN stripe_price_id;

ALTER TABLE users
    ADD COLUMN is_pro BOOLEAN DEFAULT FALSE;