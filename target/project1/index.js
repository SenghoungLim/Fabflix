/**
 * Handles the data returned by the API, reads the jsonObject, and populates data into HTML elements
 * @param resultData jsonObject

function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie list table from resultData");

    // Find the empty container by ID "movieList"
    let movieList = jQuery("#movieList");

    // Create a row for every three movies
    for (let i = 0; i < 20; i += 3) {
        // Create a new row
        let rowHTML = "<div class='row'>";
        // Loop through up to three movies within this row
        for (let j = 0; j < 3 && (i + j) < resultData.length; j++) {
            rowHTML += "<div class='col-sm movie-card'>";
            rowHTML += "<h5><a href='single-movie.html?id=" + resultData[i + j]["id"] + "'>" + resultData[i + j]["title"] + "</a></h5>";
            rowHTML += "<p>Year: " + resultData[i + j]["year"] + "</p>";
            rowHTML += "<p>Director: " + resultData[i + j]["director"] + "</p>";
            rowHTML += "<p>Genres: " + resultData[i + j]["genres"] + "</p>";

            let stars = resultData[i + j]["stars"].split(', ');
            let starIds = resultData[i + j]["star_ids"].split(', ');
            rowHTML += "<p>Stars: ";

            // Create hyperlinks for each star
            for (let k = 0; k < stars.length; k++) {
                rowHTML += "<a href='single-star.html?id=" + starIds[k] + "'>" + stars[k] + "</a>";
                if (k < stars.length - 1) {
                    rowHTML += ", ";
                }
            }
            rowHTML += "</p>";
            rowHTML += "<p>Rating: " + resultData[i + j]["rating"] + "</p>";
            rowHTML += "</div>";
        }

        rowHTML += "</div>"; // Close the row
        movieList.append(rowHTML); // Append the row to the container
    }
}

// Makes the HTTP GET request and registers the success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movie-list", // Setting request URL, which should return movie data
    success: (resultData) => handleMovieResult(resultData) // Setting a callback function to handle data returned successfully by the MovieListServlet
});
 */

$(document).ready(function () {
    // Add a submit event listener to the search form
    $("#searchForm").submit(function (event) {
        // Prevent the default form submission
        event.preventDefault();

        // Get the form data
        const formData = $(this).serialize();

        // Check if all form fields are empty (no input provided)
        const allFieldsEmpty = checkIfAllFieldsEmpty(this);

        if (allFieldsEmpty) {
            // If all fields are empty, show an alert or provide user feedback
            window.location = "index.html"
        } else {
            // If at least one field has input, proceed to the search results
            window.location = "search.html?" + formData;
        }
    });

    function checkIfAllFieldsEmpty(form) {
        // Get all form fields within the specified form
        const formFields = $(form).find('input[type="text"]');

        // Use Array.prototype.every() to check if all fields are empty
        const allEmpty = Array.from(formFields).every(function (field) {
            return field.value.trim() === '';
        });

        return allEmpty;
    }
});

function handleGenres(resultData) {
    console.log("HandleMovieResult: populating genres");

    // Find the empty container by ID "movieList"
    let movieList = jQuery("#genres");

    for (let i = 0; i < resultData.length; i += 6) {
        // Create a new row for genres
        let rowHTML = "<div class='row'>";
        // Loop through up to six genres within this row
        for (let j = 0; j < 6 && (i + j) < resultData.length; j++) {
            rowHTML += "<div class='col-sm genre-card'>";
            rowHTML += "<h5><a href='genre-detail.html?name=" + resultData[i + j]["name"] + "'>" + resultData[i + j]["name"] + "</a></h5>";
            rowHTML += "</div>";
        }

        rowHTML += "</div>"; // Close the row
        movieList.append(rowHTML); // Append the row to the container
    }
}

function handleLetters() {
    console.log("HandleLetters: populating alphabet letters");

    // Find the empty container by ID "letters"
    let lettersList = jQuery("#letters");

    // Define an array with the 24 alphabet letters
    const alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ*".split("");

    for (let i = 0; i < alphabet.length; i += 6) {
        // Create a new row for letters
        let rowHTML = "<div class='row'>";
        // Loop through up to six letters within this row
        for (let j = 0; j < 6 && (i + j) < alphabet.length; j++) {
            rowHTML += "<div class='col-sm letter-card'>";
            rowHTML += "<h5><a href='letter-detail.html?letter=" + alphabet[i + j] + "'>" + alphabet[i + j] + "</a></h5>";
            rowHTML += "</div>";
        }

        rowHTML += "</div>"; // Close the row
        lettersList.append(rowHTML); // Append the row to the container
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/genres", // Setting request URL, which should return movie data
    success: (resultData) => handleGenres(resultData) // Setting a callback function to handle data returned successfully by the MovieListServlet
});

// Call the function to handle alphabet letters
handleLetters();

