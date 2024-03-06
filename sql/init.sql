CREATE TABLE IF NOT EXISTS orders
(
    id UUID PRIMARY KEY,
    customerId Int NOT NULL,
    shippingAddressId Int NOT NULL
);

CREATE TABLE IF NOT EXISTS productQuantity
(
    orderId UUID NOT NULL,
    productId INT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_order_id
    FOREIGN KEY (orderId)
    REFERENCES orders (id)
);
