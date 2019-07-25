
-- Users schema

-- !Ups

create table category (
    id uuid NOT NULL,
    category_name varchar(40) NOT NULL ,
    PRIMARY KEY (id)
);

create table users (
    id uuid NOT NULL,
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    email varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

create table user_profile (
    id uuid NOT NULL,
    full_name varchar(255) NOT NULL,
    phone_number varchar(20) NOT NULL,
    address varchar (255) NOT NULL,
    no_nik varchar(50) NOT NULL,
    date_of_birth bigint,
    user_id uuid,
    CONSTRAINT user_profile_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
    PRIMARY KEY (id)
);

create table products (
    id uuid NOT NULL,
    product_sku varchar(40) NOT NULL,
    product_name varchar(40) NOT NULL,
    product_stock integer,
    category_id uuid,
    PRIMARY KEY (id),
    CONSTRAINT product_category_id_fkey FOREIGN KEY (category_id)
      REFERENCES category (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

create table product_detail (
    id uuid NOT NULL,
    product_stock_name varchar(40) NOT NULL,
    product_stock_price decimal(13,2),
    product_stock_value integer,
    product_id uuid NOT NULL,
    primary key (id),
    CONSTRAINT product_detail_product_id_fkey FOREIGN KEY (product_id)
      REFERENCES products (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

create table session (
  id                            uuid not null,
  secret_token                  varchar(300),
  secret_token_exp              timestamptz,
  user_id                       uuid,
  primary key (id),
  CONSTRAINT session_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

create table role (
    id uuid NOT NULL,
    name varchar(255) NOT NULL,
    description varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

insert into role values('00e33ac8-9d68-11e9-a2a3-2a2ae2dbcce4', 'Cashier', 'Staff');
insert into role values('00e33866-9d68-11e9-a2a3-2a2ae2dbcce4', 'Checker', 'Staff');
insert into role values('48f44592-adf2-11e9-a2a3-2a2ae2dbcce4', 'Admin', 'SuperUser');

create table staff (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    role_id uuid NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT staff_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT staff_role_id_fkey FOREIGN KEY (role_id)
      REFERENCES role (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

create table customer (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT customer_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
    PRIMARY KEY (id)
);

create table supplier (
    id uuid NOT NULL,
    supplier_name varchar(255) NOT NULL,
    phone_number varchar(20) NOT NULL,
    supplier_address varchar(225) NOT NULL,
    PRIMARY KEY (id)
);

create table store (
    id uuid NOT NULL,
    store_name varchar(80),
    phone_number varchar(80),
    address text,
    primary key(id)
);

create table transactions (
    id uuid NOT NULL,
    transaction_status int,
    staff_id uuid,
    customer_id uuid,
    store_id uuid,
    total_price decimal(13,2),
    primary key (id),
    CONSTRAINT transaction_staff_id_fkey FOREIGN KEY (staff_id)
      REFERENCES staff(id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT transaction_customer_id_fkey FOREIGN KEY (customer_id)
      REFERENCES customer(id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT transaction_store_id_fkey FOREIGN KEY (store_id)
      REFERENCES store(id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

create table shipment (
  id uuid NOT NULL ,
  address text,
  phone varchar (20),
  price decimal(13,2),
  transaction_id uuid,
  primary key (id),
  CONSTRAINT shipment_transaction_id_fkey FOREIGN KEY (transaction_id)
      REFERENCES transactions (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

create table payment_handle (
    id uuid NOT NULL ,
    transaction_id uuid,
    payment_status integer,
    CONSTRAINT payment_transaction_id_fkey FOREIGN KEY (transaction_id)
        REFERENCES transactions (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    primary key (id)
);

create table transaction_detail (
    id uuid NOT NULL,
    transaction_id uuid,
    product_detail_id uuid,
    number_of_purchases integer,
    subtotal_price decimal(13,2),
    primary key (id),
    CONSTRAINT transaction_detail_transaction_id_fkey FOREIGN KEY (transaction_id)
      REFERENCES transactions (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT transaction_detail_product_detail_id_fkey FOREIGN KEY (product_detail_id)
      REFERENCES product_detail (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- !Downs

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS user_profile cascade;
DROP TABLE IF EXISTS category cascade;
DROP TABLE IF EXISTS products cascade;
DROP TABLE IF EXISTS product_detail cascade;
DROP TABLE IF EXISTS session cascade;
DROP TABLE IF EXISTS role cascade;
DROP TABLE IF EXISTS staff cascade;
DROP TABLE IF EXISTS customer cascade;
DROP TABLE IF EXISTS supplier cascade;
DROP TABLE IF EXISTS store cascade;
DROP TABLE IF EXISTS shipment cascade;
DROP TABLE IF EXISTS payment_handle cascade;
DROP TABLE IF EXISTS transactions cascade;
DROP TABLE IF EXISTS transaction_detail cascade;


