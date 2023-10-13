/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

/**
 * Handles the data returned by the API, reads the jsonObject and populates data into html elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {
    console.log("handleResult: populating movie details from resultData");

    // Find the empty div elements by their IDs
    let movieTitleElement = jQuery("#movie_title");
    let movieYearElement = jQuery("#movie_year");
    let movieDirectorElement = jQuery("#movie_director");
    let movieGenresElement = jQuery("#movie_genres");
    let movieStarsElement = jQuery("#movie_stars");
    let movieRatingElement = jQuery("#movie_rating");

    // Extract movie details from the resultData
    let title = resultData[0]["title"];
    let year = resultData[0]["year"];
    let director = resultData[0]["director"];
    let genres = resultData[0]["genres"];
    let stars = resultData[0]["stars"];
    let rating = resultData[0]["rating"];

    // Populate the HTML elements with the movie details
    movieTitleElement.text("Title: " + title);
    movieYearElement.text("Year: " + year);
    movieDirectorElement.text("Director: " + director);
    movieGenresElement.text("Genres: " + genres);
    movieStarsElement.text("Stars: " + stars);
    movieRatingElement.text("Rating: " + rating);
}

// Get the movie ID from the URL
let movieId = getParameterByName('id');

// Make an HTTP GET request to retrieve movie details
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-movie?id=" + movieId,
    success: (resultData) => handleResult(resultData)
});
