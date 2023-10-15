/**
 * Handles the data returned by the API, reads the jsonObject, and populates data into HTML elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie list table from resultData");

    // Find the empty container by ID "movieCard"
    let movieList = jQuery("#movieList");

    // Create a row for every three movies
    for (let i = 0; i < 20; i += 3) {
        // Create a new row
        let rowHTML = "<div class='row'>" ;
        // Loop through up to three movies within this row
        for (let j = 0; j < 3 && (i + j) < resultData.length; j++) {
            rowHTML += "<div class='col-sm movie-card'>";
            rowHTML += "<h5><a href='single-movie.html?id=" + resultData[i + j]["id"] + "'>" + resultData[i + j]["title"] + "</a>"+"</h5>";
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

/**
 * Once this .js is loaded, the following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers the success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movie-list", // Setting request URL, which should return movie data
    success: (resultData) => handleMovieResult(resultData) // Setting a callback function to handle data returned successfully by the MovieListServlet
});
