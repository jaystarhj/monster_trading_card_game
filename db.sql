-- Create Database monster_trading_card_game:
DROP DATABASE IF EXISTS monster_trading_card_game;
CREATE DATABASE monster_trading_card_game;

-- create table userTable:
DROP TABLE IF exists userTable;
CREATE TABLE IF NOT EXISTS userTable(
    id serial UNIQUE PRIMARY KEY,
    name VARCHAR ( 50 ) UNIQUE NOT NULL,
	password VARCHAR ( 50 ) NOT NULL);