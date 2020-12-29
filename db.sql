-- create package
DROP TABLE IF exists package cascade;
CREATE TABLE IF NOT EXISTS package(
                                      id serial PRIMARY KEY,
                                      status int4,
                                      user_id int4);


-- create card table
-- 添加外键约束时，首先删除已经存在的外键约束
ALTER TABLE card
    DROP CONSTRAINT card_package_id_fkey;
DROP TABLE IF exists card cascade;
CREATE TABLE IF NOT EXISTS card(
                                   id varchar (50) PRIMARY KEY,
                                   name VARCHAR ( 50 ) NOT NULL,
                                   damage float4 NOT NULL,
                                   type varchar (50) NOT NULL,
                                   package_id int4 NOT NULL REFERENCES package(id) ON DELETE CASCADE);


-- create usertable
DROP TABLE IF exists userTable cascade;
CREATE TABLE IF NOT EXISTS userTable
(
    id       serial PRIMARY KEY,
    name     VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50)        NOT NULL,
    bio      VARCHAR(50),
    image    VARCHAR(50),
    coin     int4 default 20);


DROP TABLE IF exists deck cascade;
CREATE TABLE IF NOT EXISTS deck(
                                   id serial PRIMARY KEY,
                                   card_id varchar (50) unique not null references card(id) ON DELETE CASCADE,
                                   user_id int4);


DROP TABLE IF exists stats cascade;
CREATE TABLE IF NOT EXISTS stats(
                                    id serial PRIMARY KEY,
                                    win int4,
                                    loss int4,
                                    Elo int4 not null default 100,
                                    user_id int4 not null unique references usertable (id) on delete cascade);

DROP TABLE IF exists trade cascade;
CREATE TABLE IF NOT EXISTS trade(
                                    id varchar (50) PRIMARY KEY,
                                    card_id varchar unique not null references card (id) on delete cascade,
                                    user_id int4 references usertable (id) on delete cascade);

DROP TABLE IF exists stack cascade;
CREATE TABLE IF NOT EXISTS stack(
                                    id serial PRIMARY KEY,
                                    card_id varchar unique not null references card (id) on delete cascade,
                                    user_id int4 references usertable (id) on delete cascade);
