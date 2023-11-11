# CS122B Project 1
- [Demo Video Link](https://drive.google.com/file/d/1PX6kdB6Y_srcQVtd_DNWvbYwHlsek_b2/view?usp=sharing)
- if the link above does not work, use this backup instead: https://youtu.be/MJYs5xoLV0Y
## Member Distribution
  - Both:
    - Everything
## Special Instruction
- NA

## XML Parsing
- Native Implementation: 15401 milliseconds
- Improved Implementation with Threading and Batch executing: 7546 milliseconds
### Improved Implementation
- Batch Processing:
  - Insertion.java performs individual SQL inserts for each record in the maps (movies, stars, genres, etc.). 
  - ImprovedInsertion.java uses batch processing to group multiple SQL statements into a single batch, improving efficiency.
- Multithreading:
  - Insertion.java executes the insertion tasks sequentially. 
  - ImprovedInsertion.java uses an ExecutorService with a fixed thread pool of 5 threads to execute the insertion tasks concurrently. This can potentially speed up the overall execution time, especially when dealing with a large amount of data.

