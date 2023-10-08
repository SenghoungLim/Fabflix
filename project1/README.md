# Setting Up the Server and Database

Follow these steps to set up the server and database for the project.

### Step 1: Create the Database

Open your terminal and run the following command to create the database:

```bash
mysql -u root -p
```

Enter your MySQL root password when prompted. Then, inside the MySQL shell, create the database (if it doesn't already exist) by running:

```sql
CREATE DATABASE IF NOT EXISTS moviedb;
```

### Step 2: Create the Tables

In the terminal, make sure you are in the directory containing the `createtable.sql` file. Then, run the following command to create the tables:

```bash
mysql -u root -p -D moviedb < createtable.sql
```

### Step 3: Populate the Tables

Still in the terminal and ensuring you are in the directory containing the `movie-data.sql` file, run the following command to populate the tables with data:

```bash
mysql -u root -p -D moviedb < movie-data.sql
```

**Notes:**
- The provided instructions assume that you have MySQL installed and configured properly.
- Make sure your terminal is in the directory where the `createtable.sql` and `movie-data.sql` files are located.

With these steps completed, your server and database should be set up and ready for use.
