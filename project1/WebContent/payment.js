// In payment.html
$(document).ready(function () {
    // Retrieve total cart price from URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const totalPrice = urlParams.get("totalPrice");

    // Display the total cart price in the payment page
    $("#total-cart-price").text("$" + parseFloat(totalPrice).toFixed(2));
});
