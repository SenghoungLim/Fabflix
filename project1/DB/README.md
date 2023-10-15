### Step 1: Create the Database
If you do not have USER mytestuser setup in MySQL, follow the below steps to create it:


1. Login to mysql as a root user, (local>):

```sql
mysql -u root -p
```

2. Create a test user and grant privileges in SQL, (mysql>):

```sql
CREATE USER 'mytestuser'@'localhost' IDENTIFIED BY 'My6$Password';
GRANT ALL PRIVILEGES ON * . * TO 'mytestuser'@'localhost';
quit;
```

3. Login to USER mytestuser, (local>):

```sql
mysql -u mytestuser -p
```

4. [NO LONGER NEEDED, command line added to movie-data.sql]Prepare the database moviedb, (mysql>):

```sql
CREATE DATABASE IF NOT EXISTS moviedb;
quit;
```

### Step 2: Create the Tables

In the terminal, make sure you are in the directory containing the `createtable.sql` file. Then, run the following command to create the tables, (local>):

Local:
```sql
mysql -u mytestuser -p -D moviedb < createtable.sql
```
AWS: 
```sql
mysql -u mytestuser -p < create_table.sql
```
### Step 3: Populate the Tables

Still in the terminal and ensuring you are in the directory containing the `movie-data.sql` file, run the following command to populate the tables with data, (local>):

Local:
```sql
mysql -u mytestuser -p -D moviedb < movie-data.sql
```
AWS:
```sql
mysql -u mytestuser -p --database=moviedb < /home/ubuntu/2023-fall-cs122b-jake/project1/DB/movie-data.sql
```
