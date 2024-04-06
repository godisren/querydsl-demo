CREATE TABLE `user`
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `first_name` VARCHAR(45)  NULL,
    `last_name`  VARCHAR(45)  NULL,
    `email`      VARCHAR(255) NULL,
    `password`   VARCHAR(255) NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `product`
(
    `id`          INT            NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(255)   NULL,
    `description` VARCHAR(255)   NULL,
    `price`       DECIMAL(10, 2) NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `order`
(
    `id`      INT      NOT NULL AUTO_INCREMENT,
    `user_id` INT      NULL,
    `date`    DATETIME NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `order_product`
(
    `order_id`   INT NOT NULL,
    `product_id` INT NOT NULL,
    PRIMARY KEY (`order_id`, `product_id`)
);