CREATE TABLE IF NOT EXISTS orders
(
    id   SERIAL PRIMARY KEY,
    customerId Int UNIQUE NOT NULL,
    shippingAddressId Int UNIQUE
);

CREATE TABLE IF NOT EXISTS productQuantity
(
    orderId INT NOT NULL,
    productId INT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_order_id
    FOREIGN KEY (orderId)
    REFERENCES orders (id)
);
