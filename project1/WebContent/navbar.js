$(document).ready(function () {
    // Add a submit event listener to the search form
    $("#fulltextForm").submit(function (event) {
        // Prevent the default form submission
        event.preventDefault();

        // Get the form data
        const formData = $(this).serialize();

        // Check if all form fields are empty (no input provided)
        const allFieldsEmpty = checkIfAllFieldsEmpty(this);

        console.log(this);
        console.log(formData);



        if (allFieldsEmpty) {
            // If all fields are empty, show an alert or provide user feedback
            window.location = "index.html"
        } else {
            // If at least one field has input, proceed to the search results
            window.location = "fulltextSearch.html?" + formData;
        }
    });

    function checkIfAllFieldsEmpty(form) {
        // Get all form fields within the specified form
        const formFields = $(form).find('input[type="text"]');

        // Use Array.prototype.every() to check if all fields are empty
        const allEmpty = Array.from(formFields).every(function (field) {
            return field.value.trim() === '';
        });

        return allEmpty;
    }
});
