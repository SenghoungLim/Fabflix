To set up the server.

First, to create the db:
local> mysql -u root -p
mysql> CREATE DATABASE IF NOT EXISTS moviedb;

Then, to create the tables:
local> mysql -u root -p -D moviedb < createtable.sql

Finally, to populate the tables:
local> mysql -u root -p -D moviedb < movie-data.sql

Notes: This will create a db in your root (user), and the instructions 
also assumed that your terminal is in the file location of createtable.sql 
and movie-data.sql 

