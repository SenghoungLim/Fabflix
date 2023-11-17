DELIMITER //

CREATE PROCEDURE add_movie(
    IN p_title VARCHAR(100),
    IN p_year INTEGER,
    IN p_director VARCHAR(100),
    IN p_star_name VARCHAR(100),
    IN p_genre_name VARCHAR(32),
    OUT p_movie_id VARCHAR(10),
    OUT p_star_id VARCHAR(10),
    OUT p_genre_id INTEGER,
    OUT message VARCHAR(100)
)
BEGIN
    DECLARE v_star_id VARCHAR(10);
    DECLARE v_genre_id INT;
    DECLARE v_movie_id VARCHAR(10);
    DECLARE if_movie_inserted BOOLEAN DEFAULT FALSE;
    DECLARE if_genre_inserted BOOLEAN DEFAULT FALSE;
    DECLARE if_star_inserted BOOLEAN DEFAULT FALSE; 
    
    -- MOVIE
    -- Check if the movie already exists
    SELECT m.id INTO v_movie_id
    FROM movies m
    WHERE m.title = p_title AND m.year = p_year AND m.director = p_director;

    -- If the ID is null, assign a random value using UUID
    IF v_movie_id IS NULL THEN
        SET v_movie_id = SUBSTRING(UUID(), 1, 8);
        -- Insert the movie
        INSERT INTO movies (id, title, year, director)
        VALUES (v_movie_id, p_title, p_year, p_director);
        SET if_movie_inserted = TRUE;
    END IF;   

    -- GENRE
    -- Check if the genre already exists
    SELECT g.id INTO v_genre_id
    FROM genres g
    WHERE g.name = p_genre_name;

    -- If the genre doesn't exist, create a new one
    IF v_genre_id IS NULL THEN
        SET v_genre_id = (SELECT MAX(id) + 1 FROM genres);
         -- Insert the genre
        INSERT INTO genres (id, name)
        VALUES (v_genre_id, p_genre_name);
        SET if_genre_inserted = TRUE;
    END IF;

    -- STAR
    -- Check if the star already exists
    SELECT s.id INTO v_star_id
    FROM stars s
    WHERE s.name = p_star_name;

    -- If the star doesn't exist, create a new one
    IF v_star_id IS NULL THEN
        SET v_star_id = SUBSTRING(UUID(), 1, 8);
        -- Insert the star
        INSERT INTO stars (id, name, birthYear)
        VALUES (v_star_id, p_star_name, p_year);
        SET if_star_inserted = TRUE;
    END IF;

    IF if_movie_inserted = TRUE THEN
        INSERT INTO stars_in_movies (starId, movieId)
        VALUES (v_star_id, v_movie_id);

        INSERT INTO genres_in_movies (genreId, movieId)
        VALUES (v_genre_id, v_movie_id);
    END IF;

    SET p_movie_id = v_movie_id;
    SET p_star_id = v_star_id;
    SET p_genre_id = v_genre_id;

    -- Set the message based on the conditions
    IF if_movie_inserted = FALSE AND if_genre_inserted = FALSE AND if_star_inserted = FALSE THEN
        SET @MESSAGE_TEXT = CONCAT('Duplicate movie entry and genre and star. Movie ID: ', v_movie_id, ', Genre ID: ', v_genre_id, ', Star ID: ', v_star_id);
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @MESSAGE_TEXT;
    ELSEIF if_movie_inserted = FALSE AND if_genre_inserted = FALSE AND if_star_inserted = TRUE THEN
        SET @MESSAGE_TEXT = CONCAT('Duplicate movie entry and genre. Movie ID: ', v_movie_id, ', Genre ID: ', v_genre_id);
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @MESSAGE_TEXT;
    ELSEIF if_movie_inserted = FALSE AND if_genre_inserted = TRUE AND if_star_inserted = FALSE THEN
        SET @MESSAGE_TEXT = CONCAT('Duplicate movie entry. Movie ID: ', v_movie_id, ', Star ID: ', v_star_id);
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @MESSAGE_TEXT;
    ELSEIF if_movie_inserted = TRUE AND if_genre_inserted = FALSE AND if_star_inserted = FALSE THEN
        SET @MESSAGE_TEXT = CONCAT('Duplicate movie entry. Genre ID: ', v_genre_id, ', Star ID: ', v_star_id);
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @MESSAGE_TEXT;
    ELSEIF if_movie_inserted = FALSE THEN
        SET @MESSAGE_TEXT = CONCAT('Duplicate movie entry. Movie ID: ', v_movie_id);
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @MESSAGE_TEXT;
    ELSEIF if_genre_inserted = FALSE THEN
        SET @MESSAGE_TEXT = CONCAT('Duplicate movie entry. Genre ID: ', v_genre_id);
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @MESSAGE_TEXT;
    ELSEIF if_star_inserted = FALSE THEN
        SET @MESSAGE_TEXT = CONCAT('Duplicate movie entry. Star ID: ', v_star_id);
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @MESSAGE_TEXT;
    END IF;

    SET message = CONCAT('Added movie successfully. Movie ID: ', v_movie_id, ', Genre ID: ', v_genre_id, ', Star ID: ', v_star_id);

END //

DELIMITER ;
