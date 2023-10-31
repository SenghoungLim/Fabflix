$(document).ready(function () {
    const movieTitle = getParameterByName('title');
    const year = getParameterByName('year');
    const director = getParameterByName('director');
    const starName = getParameterByName('starName');

    const apiUrl = `api/form?title=${movieTitle}&year=${year}&director=${director}&starName=${starName}`;

    // Make an AJAX request with the form data
    $.ajax({
        dataType: "json",
        method: "GET",
        url: apiUrl,
        success: (resultData) => handleResult(resultData) // Setting a callback function to handle data returned successfully by the MovieListServlet
    });
});

function getParameterByName(target) {
     target = target.replace(/[\[\]]/g, "\\$&");
     let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
         results = regex.exec(window.location.href);
     if (!results) return null;
     if (!results[2]) return '';
     return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {
    console.log("handleMovieResult: populating search Results");

    let search_results = jQuery("#search-results");

    search_results.empty();

    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the HTML tags with resultData jsonObject
        let rowHTML = "<tr>";
        rowHTML += "<td><a href='single-movie.html?id=" + resultData[i]["id"] + "'>" + resultData[i]["title"] + "</a></td>";
        rowHTML += "<td>" + resultData[i]["year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["director"] + "</td>";

        let genres = resultData[i]["genres"].split(', ');

        rowHTML += "<td>";
        // Create hyperlinks for each star
        for (let j = 0; j < genres.length; j++) {
            rowHTML += "<a href='genre-detail.html?name=" + genres[j] + "'>" + genres[j] + "</a>";
            if (j < genres.length - 1) {
                rowHTML += ", ";
            }
        }
        rowHTML += "</td>";

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
