# --- !Ups  

create table "role" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL);


create table "category" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"status" BOOLEAN NOT NULL);


create table "users" ("id" UUID NOT NULL PRIMARY KEY,"username" VARCHAR NOT NULL,"password" VARCHAR NOT NULL,"email" VARCHAR NOT NULL);


create table "user_profile" ("id" UUID NOT NULL PRIMARY KEY,"full_name" VARCHAR NOT NULL,"phone_number" VARCHAR NOT NULL,"address" VARCHAR NOT NULL,"no_nik" VARCHAR NOT NULL,"date_of_birth" BIGINT NOT NULL,"user_id" UUID NOT NULL);
alter table "user_profile" add constraint "user_id" foreign key("user_id") references "users"("id") on update NO ACTION on delete NO ACTION;


create table "session" ("id" UUID NOT NULL PRIMARY KEY,"secret_token" VARCHAR NOT NULL,"secret_token_exp" TIMESTAMP NOT NULL,"user_id" UUID NOT NULL);
alter table "session" add constraint "user_id" foreign key("user_id") references "users"("id") on update NO ACTION on delete NO ACTION;


create table "staff" ("id" UUID NOT NULL PRIMARY KEY,"user_id" UUID NOT NULL,"role_id" UUID NOT NULL,"status" BOOLEAN NOT NULL);
alter table "staff" add constraint "role_id" foreign key("role_id") references "role"("id") on update NO ACTION on delete NO ACTION;
alter table "staff" add constraint "user_id" foreign key("user_id") references "users"("id") on update NO ACTION on delete NO ACTION;


create table "customer" ("id" UUID NOT NULL PRIMARY KEY,"user_id" UUID NOT NULL,"status" BOOLEAN NOT NULL);
alter table "customer" add constraint "user_id" foreign key("user_id") references "users"("id") on update NO ACTION on delete NO ACTION;


create table "products" ("id" UUID NOT NULL PRIMARY KEY,"sku" VARCHAR NOT NULL,"name" VARCHAR NOT NULL,"stock" INTEGER NOT NULL,"category_id" UUID NOT NULL,"status" BOOLEAN NOT NULL);
alter table "products" add constraint "category_id" foreign key("category_id") references "category"("id") on update NO ACTION on delete NO ACTION;


create table "product_stock" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"status" BOOLEAN NOT NULL);


create table "product_detail" ("id" UUID NOT NULL PRIMARY KEY,"product_stock_id" UUID NOT NULL,"selling_price" DECIMAL(21,2) NOT NULL,"purchase_price" DECIMAL(21,2) NOT NULL,"value" INTEGER NOT NULL,"product_id" UUID NOT NULL,"status" BOOLEAN NOT NULL);
alter table "product_detail" add constraint "product_id" foreign key("product_id") references "products"("id") on update NO ACTION on delete NO ACTION;
alter table "product_detail" add constraint "product_stock_id" foreign key("product_stock_id") references "product_stock"("id") on update NO ACTION on delete NO ACTION;


create table "supplier" ("id" UUID NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"phone_number" VARCHAR NOT NULL,"address" VARCHAR NOT NULL,"status" BOOLEAN NOT NULL);


create table "store" ("id" UUID NOT NULL PRIMARY KEY,"store_name" VARCHAR NOT NULL,"phone_number" VARCHAR NOT NULL,"address" VARCHAR NOT NULL);


create table "transactions" ("id" UUID NOT NULL PRIMARY KEY,"payment_status" INTEGER NOT NULL,"staff_id" UUID,"customer_id" UUID,"total_price" DECIMAL(21,2) NOT NULL,"status" INTEGER NOT NULL,"date" BIGINT NOT NULL);
alter table "transactions" add constraint "customer_id" foreign key("customer_id") references "customer"("id") on update NO ACTION on delete NO ACTION;
alter table "transactions" add constraint "staff_id" foreign key("staff_id") references "staff"("id") on update NO ACTION on delete NO ACTION;


create table "transaction_detail" ("id" UUID NOT NULL PRIMARY KEY,"transaction_id" UUID NOT NULL,"product_detail_id" UUID NOT NULL,"number_of_purchases" INTEGER NOT NULL,"subtotal_price" DECIMAL(21,2) NOT NULL,"status" INTEGER NOT NULL);
alter table "transaction_detail" add constraint "product_detail_id" foreign key("product_detail_id") references "product_detail"("id") on update NO ACTION on delete NO ACTION;
alter table "transaction_detail" add constraint "transaction_id" foreign key("transaction_id") references "transactions"("id") on update NO ACTION on delete NO ACTION;


create table "shipment" ("id" UUID NOT NULL PRIMARY KEY,"address" VARCHAR NOT NULL,"price" DECIMAL(21,2) NOT NULL,"transaction_id" UUID NOT NULL);
alter table "shipment" add constraint "transaction_id" foreign key("transaction_id") references "transactions"("id") on update NO ACTION on delete NO ACTION;




# --- !Downs

drop table if exists "supplier";


drop table if exists "store";


drop table if exists "transaction_detail";


drop table if exists "transactions";


drop table if exists "session";


drop table if exists "customer";


drop table if exists "user_profile";


drop table if exists "users";


drop table if exists "product_detail";


drop table if exists "product_stock";


drop table if exists "staff";


drop table if exists "shipment";
