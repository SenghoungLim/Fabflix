CREATE TABLE employees (
    email VARCHAR(50) PRIMARY KEY,
    password VARCHAR(20) NOT NULL,
    fullname VARCHAR(100)
);

INSERT INTO employees (email, password, fullname) VALUES ('classta@email.edu', 'classta', 'TA CS122B');