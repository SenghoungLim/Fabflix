let resultData;

function displayCartData2(GetResultData) {
    console.log("displayCartData 2 started");
    console.log(GetResultData);
    resultData = GetResultData;
    $("#cart-items").empty();
    if (resultData.cart) {
        let totalCartPrice = 0;

        resultData.cart.forEach(function (item, index) {
            //const itemTotal = parseFloat(item.moviePrice) * parseInt(item.quantity);

            totalCartPrice += parseFloat(item.moviePrice) * parseInt(item.quantity);
            let buttonContainer =
                "<div class='button-group'>" +
                "<button class='increase-button' data-index='" + index + "'>Increase</button>" +
                "<button class='decrease-button' data-index='" + index + "'>Decrease</button>" +
                "<button class='delete-button' data-index='" + index + "'>Delete</button>" +
                "</div>";

            let displayString =
                "Purchased: " + item.movieTitle + "<br>" +
                "Price: $" + item.moviePrice + "<br>" +
                "Quantity: <input class='quantity-input' type='number' value='" + item.quantity + "' data-index='" + index + "'>" + "<br>"

                $("#cart-items").append("<p>" + displayString + "</p>" + buttonContainer);
        });

        // Display the total cart price
        $("#cart-items").append("<p>Total Cart Price: $" + totalCartPrice + "</p>");

        // Update total price when quantity is changed
        $(document).on("change", ".quantity-input", function () {
            const index = $(this).data("index");
            const newQuantity = parseInt($(this).val());
            updateCartQuantity(index, newQuantity);
        });

        // Event listeners for increase and decrease buttons
        $(document).on("click", ".increase-button", function () {
            const index = $(this).data("index");
            increaseCartItemQuantity(index);
        });

        $(document).on("click", ".decrease-button", function () {
            const index = $(this).data("index");
            decreaseCartItemQuantity(index);
        });

        // Event listener for delete button
        $(document).on("click", ".delete-button", function () {
            const index = $(this).data("index");
            deleteCartItem(index);
        });
        $(document).ready(function () {
            // Handle "Proceed to Payment" button click
            $("#proceed-to-payment-button").on("click", function () {
                // Navigate to the payment page with the total cart price as a URL parameter
                window.location.href = "payment.html?totalPrice=" + totalCartPrice;
            });

            // Display the total cart price in the payment page
            // $("#total-cart-price").text("$" + totalCartPrice.toFixed(2));
        });

    } else {
        // Handle the case where the 'cart' is empty or missing
        $("#cart-items").text("Cart is empty");
    }

    console.log("displayCartData ended");
}

// Function to update quantity in the cart
const updateCartQuantity = (index, newQuantity) => {
    const cartItem = resultData.cart[index];
    cartItem.quantity = newQuantity;
    displayCartData2(resultData);
};

// Function to increase item quantity
const increaseCartItemQuantity = (index) => {
    const cartItem = resultData.cart[index];
    cartItem.quantity++;
    displayCartData2(resultData);
};

// Function to decrease item quantity
const decreaseCartItemQuantity = (index) => {
    const cartItem = resultData.cart[index];
    console.log("CartItem: " + cartItem)
    if (cartItem.quantity > 1) {
        cartItem.quantity--;
        displayCartData2(resultData);
    } else {
        // Remove the item from the cart
        deleteCartItem(index);
    }
};

// Function to delete a cart item
// Function to delete a cart item
const deleteCartItem = (index) => {
    if (resultData.cart && index >= 0 && index < resultData.cart.length) {
        if (resultData.cart[index].quantity > 0) {
            resultData.cart[index].quantity = 0;
        }
        resultData.cart.splice(index, 1);
        displayCartData2(resultData);
    }
};

$.ajax("api/cart", {
    method: "GET",
    success: resultDataString2 => {
        // Handle the response from the CartServlet
        // You can update the cart or display a message here
        let resultDataJson2 = JSON.parse(resultDataString2);
        displayCartData2(resultDataJson2);
        console.log("result send back from servlet 2: " + resultDataJson2);
    }
});