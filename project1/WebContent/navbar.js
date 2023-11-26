// Initialize an empty cache object
let queryCache = {};

/*
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
    console.log("Autocomplete initiated");

    // TODO: if you want to check past query results first, you can do it here - X
    if (queryCache.hasOwnProperty(query)) {
        console.log("Using cached results");
        // Retrieve result from cache
        const cachedResult = queryCache[query];
        console.log(cachedResult);
        // Call the callback function with cached result
        doneCallback({ suggestions: cachedResult });
    } else {
        console.log("Sending AJAX request to backend Java Servlet");
        // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
        // with the query data
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/autocomplete?autocomplete=" + escape(query),
            "success": function (data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback);
            },
            "error": function (error) {
                console.log("Lookup ajax error: " + error);
            }
        });
    }
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("Lookup ajax successfully");

    // parse the string into JSON
    let jsonData = JSON.parse(data);
    console.log(jsonData);

    // TODO: if you want to cache the result into a global variable you can do it here - X
    queryCache[query] = jsonData;

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback({ suggestions: jsonData });
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion - X
    window.location = "single-movie.html?id=" + suggestion["data"]["movieId"];
}

/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("Doing normal search with query: " + query);
    // TODO: you should do a normal search here - X
    // Check if the query is not empty
    if (query.trim() !== "") {
        window.location = "fulltextSearch.html?fulltext=" + query;
    } else {
        // If the query is empty, show an alert or provide user feedback
        console.log("Query is empty");
        window.location = "index.html";
    }
}

$(document).ready(function () {
    $('#autocomplete').autocomplete({
        // documentation of the lookup function can be found under the "Custom lookup function" section
        lookup: function (query, doneCallback) {
            handleLookup(query, doneCallback);
        },
        onSelect: function(suggestion) {
            handleSelectSuggestion(suggestion);
        },
        // set delay time
        deferRequestBy: 300,
        // there are some other parameters that you might want to use to satisfy all the requirements
        // TODO: add other parameters, such as minimum characters - X
        // minimum number of characters before triggering a request
        minChars: 3,
    });

    // bind pressing enter key to a handler function
    $('#autocomplete').keypress(function(event) {
        // keyCode 13 is the enter key
        if (event.keyCode == 13) {
            // pass the value of the input box to the handler function
            handleNormalSearch($('#autocomplete').val());
        }
    });

    // TODO: if you have a "search" button, you may want to bind the onClick event as well of that button - X
    $("#fulltextForm").submit(function (event) {
        // Prevent the default form submission
        event.preventDefault();
        handleNormalSearch($('#autocomplete').val());
    });
});






