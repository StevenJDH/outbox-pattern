CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS customers (
  id UUID PRIMARY KEY,
  "name" VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  shipping_address VARCHAR(255) NOT NULL
);

INSERT INTO customers (id, name, email, shipping_address)
  VALUES ('41e86e9a-115e-4561-a77a-8e3e40156a24', 'John Doe', 'john.doe@example.com', '1313 Mockingbird Lane');

CREATE TABLE IF NOT EXISTS orders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
  order_date TIMESTAMP NOT NULL DEFAULT now(),
  "status" VARCHAR(50) NOT NULL,
  total_items INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS order_items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  "name" VARCHAR(255) NOT NULL,
  quantity INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS outbox_event (
  id UUID PRIMARY KEY,
  aggregate_type VARCHAR(255) NOT NULL,        -- e.g., "Order"
  aggregate_id VARCHAR(255) NOT NULL,          -- e.g., "12345"
  "type" VARCHAR(255) NOT NULL,                -- e.g., "OrderCreated"
  payload JSONB NULL,                          -- Event data
  tracing_span_context VARCHAR(256),           -- Optional, tracing baggage
  "timestamp" TIMESTAMP NOT NULL DEFAULT now() -- Optional, for tracking
);

-- Debezium uses this publication with pgoutput
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_publication WHERE pubname = 'debezium_pub'
  ) THEN
    CREATE PUBLICATION debezium_pub FOR TABLE outbox_event;
  END IF;
END $$;
