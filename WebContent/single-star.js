/**
 * Retrieve parameter from the request URL, matching by parameter name
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
    console.log("HandleResult: populating star details from resultData");

    let starTable = jQuery("#star-table");

    // Clear the table if it contains data
    starTable.empty();

    let starName = resultData[0]["StarName"];
    let yearOfBirth = resultData[0]["YearOfBirth"];
    let movieTitles = resultData[0]["MovieTitles"].split(', ');
    let movieIds = resultData[0]["MovieIds"].split(', ');

    // Create a table row for each star detail
    let nameRow = '<tr><th>Name:</th><td>' + starName + '</td></tr>';
    let yearRow = '<tr><th>Year:</th><td>' + yearOfBirth + '</td></tr>';

    let moviesRow = '<tr><th>Movies:</th><td>';
    // Create hyperlinks for each movie
    for (let j = 0; j < movieTitles.length; j++) {
        moviesRow += "<a href='single-movie.html?id=" +  movieIds[j] + "'>"  + movieTitles[j] +  "</a>";
        if (j < movieTitles.length - 1) {
            moviesRow += ", ";
        }
    }
    moviesRow += '</td></tr>';

    // Append the rows to the table
    starTable.append(nameRow);
    starTable.append(yearRow);
    starTable.append(moviesRow);
}

// Get the star ID from the URL
let starId = getParameterByName('id');

// Make an AJAX request to retrieve star details
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-star?id=" + starId,
    success: (resultData) => handleResult(resultData)
});
