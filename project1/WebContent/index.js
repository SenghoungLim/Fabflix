/**
 * Handles the data returned by the API, reads the jsonObject and populates data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie list table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_list_table_body"
    let movieListTableBodyElement = jQuery("#movie_list_table_body");

    // Iterate through resultData, no more than 20 entries (as per your SQL query)
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        // Concatenate the HTML tags with resultData jsonObject
        let rowHTML = "<tr>";
        rowHTML += "<td><a href='single-movie.html?id=" + resultData[i]["id"] + "'>" + resultData[i]["title"] + "</a></td>";
        rowHTML += "<td>" + resultData[i]["year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["director"] + "</td>";
        rowHTML += "<td>" + resultData[i]["genres"] + "</td>";

        let stars = resultData[i]["stars"].split(',');
        let starIds = resultData[i]["star_ids"].split(',');

        rowHTML += "<td>";
        // Create hyperlinks for each star
        for (let j = 0; j < stars.length; j++) {
            rowHTML += "<a href='single-star.html?id=" +  starIds[j] + "'>"  + stars[j] +  "</a>";
            if (j < stars.length - 1) {
                rowHTML += ", ";
            }
        }
        rowHTML += "</td>";

        rowHTML += "<td>" + resultData[i]["rating"] + "</td>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieListTableBodyElement.append(rowHTML);
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
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
});