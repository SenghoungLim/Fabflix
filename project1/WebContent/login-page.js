let login_form = $("#login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    if (!resultDataString) {
        console.error("Empty response received");
        return;
    }
    try{
        let resultDataJson = JSON.parse(resultDataString);

        console.log("Handle login response");
        console.log(resultDataJson);
        console.log(resultDataJson["status"]);

        // If login succeeds, it will redirect the user to index.html
        if (resultDataJson["status"] === "success") {
            window.location.replace("index.html");
        } else {
            // If login fails, the web page will display
            // error messages on <div> with id "login_error_message"
            console.log("Show error message");
            console.log(resultDataJson["message"]);
            //$("#login_error_message").text(resultDataJson["message"]);
            alert("Your username or password is incorrect or you have not verify with recaptcha, please try again.");
        }
    } catch(error){
        console.error("Error parsing JSON:", error);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("Submit login form");
    console.log("HTTP");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/login", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_form.serialize(),
            complete: function() {
                console.log("AJAX request completed");
            },
            success: handleLoginResult
        }
    );
    console.log("After login Ajax");
}
// Bind the submit action of the form to a handler function
login_form.submit(submitLoginForm);

