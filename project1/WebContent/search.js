$(document).ready(function () {
    // Extract the search results from the URL
    const searchResults = getParameterByName('results');
    if (searchResults) {
        handleResult(JSON.parse(decodeURIComponent(searchResults)));
    }
});

// Function to extract query parameters from a URL
function getParameterByName(target) {
    target = target.replace(/[\[\]]/g, "\\$&");
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(window.location.href);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {
    console.log("handleMovieResult: populating Search Results");

    let search_results = jQuery("#search-results");
    search_results.empty();


    // Iterate through resultData, no more than 20 entries
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
        search_results.append(rowHTML);
    }
}


