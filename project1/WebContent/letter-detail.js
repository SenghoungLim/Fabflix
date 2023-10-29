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
    console.log("handleMovieResult: populating letter Results");

    let letter_results = jQuery("#letter-results");

    letter_results.empty();

    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the HTML tags with resultData jsonObject
        let rowHTML = "<tr>";
        rowHTML += "<td><a href='single-movie.html?id=" + resultData[i]["id"] + "'>" + resultData[i]["title"] + "</a></td>";
        rowHTML += "<td>" + resultData[i]["year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["director"] + "</td>";
        rowHTML += "<td>" + resultData[i]["genres"] + "</td>";

        let stars = resultData[i]["stars"].split(', ');
        let starIds = resultData[i]["star_ids"].split(', ');

        rowHTML += "<td>";
        // Create hyperlinks for each star
        for (let j = 0; j < stars.length; j++) {
            rowHTML += "<a href='single-star.html?id=" + starIds[j] + "'>" + stars[j] + "</a>";
            if (j < stars.length - 1) {
                rowHTML += ", ";
            }
        }
        rowHTML += "</td>";

        rowHTML += "<td>" + resultData[i]["rating"] + "</td>";
        rowHTML += "</tr>";

        // Append the row created to the table body
        letter_results.append(rowHTML);
    }
}

// Get the movie ID from the URL
let letter = getParameterByName('letter');

// Make an AJAX request to retrieve movie details
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/browse-letter?letter=" + letter,
    success: (resultData) => handleResult(resultData)
});
