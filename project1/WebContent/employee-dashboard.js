function metaDataTable(metadataResult) {
    console.log("MetadataResult", metadataResult);

    let tableBody = $('#metaDataTable tbody');

    metadataResult.tables.forEach(function (table) {
        let row = '<tr><td>' + table.tableName + '</td><td>';
        // Adding columns
        table.columns.forEach(function (column) {
            row += column.columnName + ' (' + column.columnType + '), ';
        });
        // Removing the trailing comma and space
        row = row.slice(0, -2);
        row += '</td></tr>';
        tableBody.append(row);
    });
}

$.ajax(
    "api/_dashboard", {
        method: "GET",
        complete: function() {
            console.log("Meta data request completed");
        },
        success: metaDataTable
    }
);
// Define the addStar function
function displayAddStar(data) {
    console.log("Data received:", data); // Log the entire data object
    let starInfoDiv = $('#displayNewStarData');

    if (data.status === "success") {
        // Assuming 'data.star' contains the added star information
        let star = data.star;

        // Build HTML content for the added star
        let rowHTML = "<h1>Star Added Successfully</h1>";
        rowHTML += "<p>Star ID: " + data.starId + "</p>";
        rowHTML += "<p>Star Name: " + data.starName + "</p>";
        rowHTML += "<p>Birth Year: " + data.birthYear + "</p>";

        // Append the content to the starInfoDiv
        starInfoDiv.html(rowHTML);

        // Display an alert
        alert("Star added successfully!");
    } else {
        alert("Failed to add star. Reason: " + data.message);
    }
}
function addStar(event) {
    event.preventDefault();
    // Get input values
    let starName = $('#starNameInput').val();
    let birthYear = $('#birthYearInput').val();

    // Make AJAX request
    $.ajax(
        "api/addStar", {
            method: "POST",
            data: {
                starNameInput: starName,
                birthYearInput: birthYear
            },
            complete: function () {
                console.log("Add star request completed");
            },
            success: function(data) {
                console.log("AJAX success:", data);
                displayAddStar(data); // Call your displayAddStar function
            }
        }
    );
}

// Attach the addStar function to the click event of the "Add Star" button
$(document).ready(function () {
    $('#addStarFormId button').click(function (event) {
        addStar(event); // Call the addStar function when the button is clicked
    });
});

//Add movie
// Add the following JavaScript to your employee-dashboard.js
function displayAddMovie(data) {
    let movieInfoDiv = $('#displayNewMovieData');
    console.log("Add movie data: " + data);
    if (data.message === "Movie ID, Genre ID, and Star ID was found!") {
        console.log("GenreExisting");
        let existingMovieId = data.existingMovieId;
        let existingStarId = data.existingStarId;
        let existingGenreId = data.existingGenreId;
        let rowHTML = "<h2>Existing Movie, Genre, Star Id:</h2>";
        rowHTML += "<p>Existing Movie ID: " + existingMovieId + "</p>";
        rowHTML += "<p>Existing Star ID: " + existingStarId + "</p>";
        rowHTML += "<p>Existing Genre ID: " + existingGenreId + "</p>";
        movieInfoDiv.html(rowHTML);
    }
    else if (data.status === "success") {
        let movieId = data.movieId;
        let starId = data.starId;
        let genreId = data.genreId;

        // Build HTML content for the added movie
        let rowHTML = "<h2>Movie Added Successfully</h2>";
        rowHTML += "<p>Movie ID: " + movieId + "</p>";
        rowHTML += "<p>Star ID: " + starId + "</p>";
        rowHTML += "<p>Genre ID: " + genreId + "</p>";
        // Append the content to the movieInfoDiv
        movieInfoDiv.html(rowHTML);

        // Display an alert
        alert(data.message);
    }
    else {
        alert(data.message);
    }

}

function addMovie(event) {
    event.preventDefault();

    // Get input values
    let movieTitle = $('#movieTitleInput').val();
    let movieYear = $('#movieYearInput').val();
    let movieDirector = $('#movieDirectorInput').val();
    let starName = $('#starNameMovieInput').val();
    let genreName = $('#genreNameInput').val();

    // Make AJAX request
    $.ajax({
        url: "api/addMovie",
        method: "POST",
        data: {
            movieTitleInput: movieTitle,
            movieYearInput: movieYear,
            movieDirectorInput: movieDirector,
            starNameMovieInput: starName,
            genreNameInput: genreName
        },
        success: displayAddMovie,
        complete: function () {
            console.log("Add movie request completed");
        }
    });
}

// Attach the addMovie function to the click event of the "Add Movie" button
$(document).ready(function () {
    $('#addMovieButton').click(function (event) {
        addMovie(event);
    });
});


