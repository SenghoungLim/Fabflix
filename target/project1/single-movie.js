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
    console.log("HandleResult: populating movie details from resultData");

    let movieTable = jQuery("#movie_table");

    // Clear the table if it contains data
    movieTable.empty();

    let title = resultData[0]["title"];
    let year = resultData[0]["year"];
    let director = resultData[0]["director"];
    let rating = resultData[0]["rating"];

    // Create a table row for each movie detail
    let titleRow = '<tr><th>Title:</th><td>' + title + '</td></tr>';
    let yearRow = '<tr><th>Year:</th><td>' + year + '</td></tr>';
    let directorRow = '<tr><th>Director:</th><td>' + director + '</td></tr>';

    let genres = resultData[0]["genres"];
    let genresRow;
    if (genres != null){
        genres = genres.split(', ');
        genresRow = '<tr><th>Genres:</th><td>';
        // Create hyperlinks for each star
        for (let j = 0; j < genres.length; j++) {
            genresRow += "<a href='genre-detail.html?name=" + genres[j] + "'>" + genres[j] + "</a>";
            if (j < genres.length - 1) {
                genresRow += ", ";
            }
        }
        genresRow += '</td></tr>';
    }else{
        genresRow = '<tr><th>Genres</th><td>' + genres + '</td></tr>';
    }

    let stars = resultData[0]["stars"];
    let starIds = resultData[0]["star_ids"];
    let starsRow;
    if(stars != null && starIds != null) {
        stars = stars.split(', ');
        starIds = starIds.split(', ');
        starsRow = '<tr><th>Stars:</th><td>';
        // Create hyperlinks for each star
        for (let j = 0; j < stars.length; j++) {
            starsRow += "<a href='single-star.html?id=" + starIds[j] + "'>" + stars[j] + "</a>";
            if (j < stars.length - 1) {
                starsRow += ", ";
            }
        }
        starsRow += '</td></tr>';
    }else{
        starsRow = '<tr><th>Stars</th><td>' + stars + '</td></tr>';
    }

    let ratingRow = '<tr><th>Rating:</th><td>' + rating + '</td></tr>';

    // Append the rows to the table
    movieTable.append(titleRow);
    movieTable.append(yearRow);
    movieTable.append(directorRow);
    movieTable.append(genresRow);
    movieTable.append(starsRow);
    movieTable.append(ratingRow);
}

// Get the movie ID from the URL
let movieId = getParameterByName('id');

// Make an AJAX request to retrieve movie details
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-movie?id=" + movieId,
    success: (resultData) => handleResult(resultData)
});
