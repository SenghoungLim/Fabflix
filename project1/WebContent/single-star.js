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
    console.log("handleResult: populating star details from resultData");

    let starNameElement = jQuery("#star_name");
    let starYearOfBirthElement = jQuery("#star_year_of_birth");
    let starMoviesElement = jQuery("#star_movies");

    let StarName = resultData[0]["StarName"];
    let YearOfBirth = resultData[0]["YearOfBirth"];
    let MovieTitles = resultData[0]["MovieTitles"];

    starNameElement.text("Name: " + StarName);
    starYearOfBirthElement.text("Year of Birth: " + YearOfBirth);
    starMoviesElement.text("Movies: " + MovieTitles);
}

console.log(getParameterByName('starID'));
let starId = getParameterByName('starID');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-star?id=" + starId,
    success: (resultData) => handleResult(resultData)
});
