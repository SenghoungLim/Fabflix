/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, reads the jsonObject and populates data into HTML elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {
    console.log("handleResult: populating movie details from resultData");

    let movieTitleElement = jQuery("#movie_title");
    let movieYearElement = jQuery("#movie_year");
    let movieDirectorElement = jQuery("#movie_director");
    let movieGenresElement = jQuery("#movie_genres");
    let movieStarsElement = jQuery("#movie_stars");
    let movieRatingElement = jQuery("#movie_rating");

    let title = resultData[0]["title"];
    let year = resultData[0]["year"];
    let director = resultData[0]["director"];
    let genres = resultData[0]["genres"];
    let stars = resultData[0]["stars"];
    let rating = resultData[0]["rating"];

    movieTitleElement.text("Title: " + title);
    movieYearElement.text("Year: " + year);
    movieDirectorElement.text("Director: " + director);
    movieGenresElement.text("Genres: " + genres);
    movieStarsElement.text("Stars: " + stars);
    movieRatingElement.text("Rating: " + rating);
}

console.log(getParameterByName('id'));
let movieId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-movie?id=" + movieId,
    success: (resultData) => handleResult(resultData)
});
