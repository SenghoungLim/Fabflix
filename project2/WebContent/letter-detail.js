// Get the movie ID from the URL
let letter = getParameterByName('letter');

// Retrieve the page number from the URL
let currentPage = parseInt(getParameterByName('page')) || 1;

let moviePerPage = 25;
document.getElementById("sortButton").addEventListener("click", function () {
    var selectedOption = document.getElementById("sortOptions").value;

    // Define your sorting logic here based on the selectedOption
    let sortField = null;
    let sortOrder = null;

    console.log(selectedOption)

    if (selectedOption === "ratingTitleASC") {
        sortField = "Rating";
        sortOrder = "ASC";
    } else if (selectedOption === "ratingTitleDESC") {
        sortField = "Rating";
        sortOrder = "DESC";
    } else if (selectedOption === "titleRatingASC") {
        sortField = "Title";
        sortOrder = "ASC";
    } else if (selectedOption === "titleRatingsDESC") {
        sortField = "Title";
        sortOrder = "DESC";
    }

    const apiUrl = `api/browse-letter?letter=${letter}&page=${currentPage}&sortField=${sortField}&sortOrder=${sortOrder}`;
    // Make an AJAX request with the form data
    $.ajax({
        dataType: "json",
        method: "GET",
        url: apiUrl,
        success: (resultData) => handleResult(resultData) // Setting a callback function to handle data returned successfully by the MovieListServlet
    });
});

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
        letter_results.append(rowHTML);
        // Update pagination controls
        updatePagination(resultData);
    }
}

/**
 * Update the pagination controls
 * @param resultData jsonObject
 */
function updatePagination(resultData) {
    let totalPages = Math.ceil(parseInt(resultData[0]["totalRows"]) / moviePerPage); // Assuming 15 items per page

    let pagination = jQuery("#pagination");
    pagination.empty();

    if (currentPage > 1) {
        pagination.append(
            `<li class="page-item"><a class="page-link" href="letter-detail.html?letter=${letter}&page=1">First</a></li>`
        );
        pagination.append(
            `<li class="page-item"><a class="page-link" href="letter-detail.html?letter=${letter}&page=${currentPage - 1}">Previous</a></li>`
        );
    }

    if (currentPage < totalPages) {
        pagination.append(
            `<li class="page-item"><a class="page-link" href="letter-detail.html?letter=${letter}&page=${currentPage + 1}">Next</a></li>`
        );
    }
}

// Make an AJAX request to retrieve movie details
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/browse-letter?letter=" + letter + "&page=" + currentPage,
    success: (resultData) => handleResult(resultData)
});
