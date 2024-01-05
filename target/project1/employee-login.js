
    let employeeLoginForm = $("#employee_login_form");
    console.log(employeeLoginForm);
    function empLoginSuccess (resDataString){
        if (resDataString["status"] === "success"){
            window.location.replace("employee-dashboard.html");
        } else{
            alert("Your username or password is incorrect or you have not verify with recaptcha, please try again.");
        }
    };
    function employeeLoginSubmit (empSubmitEvent) {
            console.log("Submitting login form...");
            empSubmitEvent.preventDefault();
            $.ajax({
                url: "api/_dashboard",
                method: "POST",
                data: employeeLoginForm.serialize(),
                success: empLoginSuccess
                }).always(function(){
                console.log("Employee login request Completed");
            });
    };
    employeeLoginForm.submit(employeeLoginSubmit);