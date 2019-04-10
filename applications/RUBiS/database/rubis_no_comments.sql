CREATE TABLE categories (
   id INTEGER UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT, 
   name VARCHAR(50),
   PRIMARY KEY(id)
);

CREATE TABLE regions (
   id INTEGER UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT, 
   name VARCHAR(25),
   PRIMARY KEY(id)
);

CREATE TABLE users (
   id            INTEGER UNSIGNED NOT NULL UNIQUE,
   firstname     VARCHAR(20),
   lastname      VARCHAR(20),
   nickname      VARCHAR(20) NOT NULL UNIQUE,
   password      VARCHAR(20) NOT NULL,
   email         VARCHAR(50) NOT NULL,
   rating        INTEGER,
   balance       FLOAT,
   creation_date TIMESTAMP, 
   region        INTEGER UNSIGNED NOT NULL,
   PRIMARY KEY(id),
   INDEX auth (nickname,password),
   INDEX region_id (region)
);

CREATE TABLE items (
   id            INTEGER UNSIGNED NOT NULL UNIQUE,
   name          VARCHAR(100),
   description   TEXT,
   initial_price FLOAT UNSIGNED NOT NULL,
   quantity      INTEGER UNSIGNED NOT NULL,
   reserve_price FLOAT UNSIGNED DEFAULT 0,
   buy_now       FLOAT UNSIGNED DEFAULT 0,
   nb_of_bids    INTEGER UNSIGNED DEFAULT 0,
   max_bid       FLOAT UNSIGNED DEFAULT 0,
   start_date    TIMESTAMP, 
   end_date      TIMESTAMP, 
   seller        INTEGER UNSIGNED NOT NULL,
   category      INTEGER UNSIGNED NOT NULL,
   winnerid		  INTEGER UNSIGNED DEFAULT NULL, 
   PRIMARY KEY(id),
   INDEX seller_id (seller),
   INDEX category_id (category)
);

CREATE TABLE bids (
   id      INTEGER UNSIGNED NOT NULL UNIQUE,
   user_id INTEGER UNSIGNED NOT NULL,
   item_id INTEGER UNSIGNED NOT NULL,
   qty     INTEGER UNSIGNED NOT NULL,
   bid     FLOAT UNSIGNED NOT NULL,
   max_bid FLOAT UNSIGNED NOT NULL,
   date    TIMESTAMP, 
   PRIMARY KEY(id),
   INDEX item (item_id),
   INDEX user (user_id)
);

CREATE TABLE comments (
   id           INTEGER UNSIGNED NOT NULL UNIQUE,
   from_user_id INTEGER UNSIGNED NOT NULL,
   to_user_id   INTEGER UNSIGNED NOT NULL,
   item_id      INTEGER UNSIGNED NOT NULL,
   rating       INTEGER,
   date         TIMESTAMP, 
   comment      TEXT,
   PRIMARY KEY(id),
   INDEX from_user (from_user_id),
   INDEX to_user (to_user_id),
   INDEX item (item_id)
);

CREATE TABLE buy_now (
   id       INTEGER UNSIGNED NOT NULL UNIQUE, 
   buyer_id INTEGER UNSIGNED NOT NULL,
   item_id  INTEGER UNSIGNED NOT NULL,
   qty      INTEGER UNSIGNED NOT NULL,
   date     TIMESTAMP, 
   PRIMARY KEY(id),
   INDEX buyer (buyer_id),
   INDEX item (item_id)
);
