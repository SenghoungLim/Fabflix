DELIMITER //

CREATE PROCEDURE add_movie(
    IN p_title VARCHAR(100),
    IN p_year INTEGER,
    IN p_director VARCHAR(100),
    IN p_star_name VARCHAR(100),
    IN p_genre_name VARCHAR(32),
    OUT p_movie_id VARCHAR(10),
    OUT p_star_id VARCHAR(10),
    OUT p_genre_id INTEGER
)
BEGIN
    DECLARE v_star_id VARCHAR(10);
    DECLARE v_genre_id INT;
    DECLARE v_movie_id VARCHAR(10);
    
    -- MOVIE
    -- Check if the movie already exists
    SELECT id INTO v_movie_id
    FROM movies
    WHERE title = p_title AND year = p_year AND director = p_director;
    
    -- If the ID is null, assign a random value using UUID
    IF v_movie_id IS NULL THEN
        SET v_movie_id = SUBSTRING(UUID(), 1, 8);
    END IF;   

    -- Insert the movie
    INSERT INTO movies (id, title, year, director)
    VALUES (v_movie_id, p_title, p_year, p_director);

    -- STAR
    -- Check if the star already exists
    SELECT id INTO v_star_id
    FROM stars
    WHERE name = p_star_name;

    -- If the star doesn't exist, create a new one
    IF v_star_id IS NULL THEN
        SET v_star_id = SUBSTRING(UUID(), 1, 8);
    END IF;

    -- Insert the star
    INSERT INTO stars (id, name, birthYear)
    VALUES (v_star_id, p_star_name, p_year);

    -- Link star to the movie
    INSERT INTO stars_in_movies (starId, movieId)
    VALUES (v_star_id, v_movie_id);

    -- GENRE
    -- Check if the genre already exists
    SELECT id INTO v_genre_id
    FROM genres
    WHERE name = p_genre_name;

    -- If the genre doesn't exist, create a new one
    IF v_genre_id IS NULL THEN
        SET v_genre_id = (SELECT MAX(id) + 1 FROM genres);
    END IF;
    -- Insert the genre
    INSERT INTO genres (id, name)
    VALUES (v_genre_id, p_genre_name);

    -- Link genre to the movie
    INSERT INTO genres_in_movies (genreId, movieId)
    VALUES (v_genre_id, v_movie_id);
	
    -- Assign values to output parameters
    SET p_movie_id = v_movie_id;
    SET p_star_id = v_star_id;
    SET p_genre_id = v_genre_id;


    SELECT 'Movie added successfully.' AS message;
    
END //

DELIMITER ;