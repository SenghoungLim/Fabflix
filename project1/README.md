# CS122B Project 1
- [Demo Video Link](https://youtu.be/i1Pm3D06-nk?si=GCJwOPtvHI6Wlrxr)
## Member Distribution
  - Senghoung Lim
    - Task 1 - 5 & Demo
  - Thien Toan Vu
    - Task 6
## Special Instruction
- NA

## List of filenames with Prepared Statements
All extension is .java
- AddMovieServlet
- AddStarServlet
- BrowseGenreServlet
- BrowseLetterServlet
- DashboardServlet
- GenreServlet
- ImprovedInsertion
- Insertion
- LoginServlet
- MovieListServlet
- SearchServlet
- SingleStarServlet

## XML Parsing
- Native Implementation: 34388 milliseconds
- Improved Implementation with Threading and Batch executing: 22628 milliseconds
### Improved Implementation
- Batch Processing:
  - Insertion.java performs individual SQL inserts for each record in the maps (movies, stars, genres, etc.). 
  - ImprovedInsertion.java uses batch processing to group multiple SQL statements into a single batch, improving efficiency.
- Multithreading:
  - Insertion.java executes the insertion tasks sequentially. 
  - ImprovedInsertion.java uses an ExecutorService with a fixed thread pool of 5 threads to execute the insertion tasks concurrently. This can potentially speed up the overall execution time, especially when dealing with a large amount of data.

## Inconsistent data from xml Parsing  
### actors63.xml
- Some of the actors parsed from this file don't have a DOB, a null value is then assigned instead
- There are no movies included in this file indicating the movie(s) that the actor is in
- This file mainly contains information about actors, if an actor is the same in casts124, the DOB is updated
- Star ids are generated using their full names (removed all the spaces) + the current number of new stars added
### casts124.xml
- Updates the stars and stars_in_movies tables
- Star ids are generated using the movie they're in + the current number of new stars added
- No DOBs are provided in this file, a null value is then assigned instead
- if star name is empty, a null value is assigned
### mains243.xml
- Updates genres, genres_in_movies, movies tables
- genre ids are generated based on the current number of new genres added
- new genres are parsed from this file
- movie ids, titles, years, directors are provided, if the fields are empty, a null value is then assigned



