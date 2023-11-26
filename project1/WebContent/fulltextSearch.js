const fulltext = getParameterByName('fulltext');
let currentPage = parseInt(getParameterByName('page')) || 1;
let moviePerPage = 25;

$(document).ready(function () {
    const apiUrl = `api/fulltext?fulltext=${fulltext}&page=${currentPage}`;

    // Make an AJAX request with the form data
    $.ajax({
        dataType: "json",
        method: "GET",
        url: apiUrl,
        success: (resultData) => handleResult(resultData) // Setting a callback function to handle data returned successfully by the MovieListServlet
    });
});

document.getElementById("sortButton").addEventListener("click", function () {
    let selectedOption = document.getElementById("sortOptions").value;

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
    const apiUrl = `api/fulltext?fulltext=${fulltext}&page=${currentPage}&sortField=${sortField}&sortOrder=${sortOrder}`;

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

    console.log("HandleMovieResult: populating Search Results");
    console.log(resultData);


    let search_results = jQuery("#fulltext-search-results");

    search_results.empty();

    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the HTML tags with resultData jsonObject
        let rowHTML = "<tr>";
        rowHTML += "<td><a href='single-movie.html?id=" + resultData[i]["id"] + "'>" + resultData[i]["title"] + "</a></td>";
        rowHTML += "<td>" + resultData[i]["year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["director"] + "</td>";

        let genres = resultData[i]["genres"];
        if (genres != null){
            genres = genres.split(', ');
            rowHTML += "<td>";
            // Create hyperlinks for each star
            for (let j = 0; j < genres.length; j++) {
                rowHTML += "<a href='genre-detail.html?name=" + genres[j] + "'>" + genres[j] + "</a>";
                if (j < genres.length - 1) {
                    rowHTML += ", ";
                }
            }
            rowHTML += "</td>";
        }
        else{
            rowHTML += "<td>" + genres + "</td>";
        }

        let stars = resultData[i]["stars"];
        let starIds = resultData[i]["star_ids"];

        if(stars != null && starIds != null) {
            stars = stars.split(', ');
            starIds = starIds.split(', ');

            rowHTML += "<td>";
            // Create hyperlinks for each star
            for (let j = 0; j < stars.length; j++) {
                rowHTML += "<a href='single-star.html?id=" + starIds[j] + "'>" + stars[j] + "</a>";
                if (j < stars.length - 1) {
                    rowHTML += ", ";
                }
            }
            rowHTML += "</td>";
        }
        else{
            rowHTML += "<td>" + stars + "</td>";
        }

        rowHTML += "<td>" + resultData[i]["rating"] + "</td>";
        rowHTML += "<td><input type='number' class='quantity-input' value='1'></td>";
        rowHTML += "<td><p>$9.99</p></td>";
        rowHTML += "<td><button class='add-button' data-movie-id='" + resultData[i]["id"] + "' data-movie-title='" + resultData[i]["title"] + "'>Add</button></td>";
        rowHTML += "</tr>";

        // Append the row created to the table body
        search_results.append(rowHTML);
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
            `<li class="page-item"><a class="page-link" href="fulltextSearch.html?fulltext=${fulltext}&page=1">First</a></li>`
        );
        pagination.append(
            `<li class="page-item"><a class="page-link" href="fulltextSearch.html?fulltext=${fulltext}&page=${currentPage - 1}">Previous</a></li>`
        );
    }

    if (currentPage < totalPages) {
        pagination.append(
            `<li class="page-item"><a class="page-link" href="fulltextSearch.html?fulltext=${fulltext}&page=${currentPage + 1}">Next</a></li>`
        );
    }
}

$(document).on("click", ".add-button", function () {
    const movieId = $(this).data("movie-id");
    const movieTitle = $(this).data("movie-title");
    const moviePrice = 9.99; // Fixed price
    const quantity = $(this).closest("tr").find(".quantity-input").val();
    // Send data to the CartServlet using an AJAX request
    $.ajax("api/cart", {
        method: "POST",
        data: {
            movieId: movieId,
            movieTitle: movieTitle,
            moviePrice: moviePrice,
            quantity: quantity
        },
        success: resultDataString => {
            // Handle the response from the CartServlet
            // You can update the cart or display a message here
            let resultDataJson = JSON.parse(resultDataString);
            displayCartData(resultDataJson);
            console.log("Result send back from servlet" + resultDataJson);
        }
    });
});

function displayCartData(resultData) {
    console.log("DisplayCartData started");
    console.log(resultData);
    // Check if the 'cart' property exists and it's an array with at least one item
    if (Array.isArray(resultData.cart) && resultData.cart.length > 0) {
        // Access the first item in the 'cart' array
        let movie = resultData.cart[0];
        // Create a string to display the movie information
        let displayString =
            "Purchased: " + movie.movieTitle +
            " Price: $" + movie.moviePrice +
            " Quantity: " + movie.quantity;

        // Update the HTML element with the displayString
        let displayString2 = "<p> Hello 2 </p>";
        $("#testItem").append(displayString2);
        $("#testItem").append(displayString);
        // $("#testItem2").append(displayString2);
        // $("#testItem2").append(displayString);

    } else {
        // Handle the case where the 'cart' is empty or missing
        $("#testItem").text("Cart is empty");
    }

    console.log("DisplayCartData ended");
}
