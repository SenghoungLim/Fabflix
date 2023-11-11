CREATE DATABASE IF NOT EXISTS moviedb;
USE moviedb;
CREATE TABLE IF NOT EXISTS movies (
    id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    director VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS stars (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    birthYear INTEGER
);

CREATE TABLE IF NOT EXISTS stars_in_movies (
    starId VARCHAR(10) REFERENCES stars(id),
    movieId VARCHAR(10) REFERENCES movies(id),
    PRIMARY KEY (starId, movieId)
);

CREATE TABLE IF NOT EXISTS genres (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres_in_movies (
    genreId INTEGER REFERENCES genres(id),
    movieId VARCHAR(10) REFERENCES movies(id),
    PRIMARY KEY (genreId, movieId)
);

CREATE TABLE IF NOT EXISTS customers (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    ccId VARCHAR(20) REFERENCES creditcards(id),
    address VARCHAR(200) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS sales (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    customerId INTEGER REFERENCES customers(id),
    movieId VARCHAR(10) REFERENCES movies(id),
    saleDate DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS creditcards (
    id VARCHAR(20) PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    expiration DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS ratings (
    movieId VARCHAR(10) REFERENCES movies(id),
    rating FLOAT NOT NULL,
    numVotes INTEGER NOT NULL
);
