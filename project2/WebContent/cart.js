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

            totalCartPrice = 9.99 * parseInt(item.quantity)

            let displayString =
                "Purchased: " + item.movieTitle +
                " Price: $" + item.moviePrice +
                " Quantity: <input class='quantity-input' type='number' value='" + item.quantity + "' data-index='" + index + "'>"
                +
                "<button class='increase-button' onclick='increaseCartItemQuantity()'>Increase</button>" +
                "<button class='decrease-button' data-index='" + index + "'>Decrease</button>" +
                "<button class='delete-button' data-index='" + index + "'>Delete</button>";

            $("#cart-items").append("<p>" + displayString + "</p>");
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
    if (cartItem.quantity > 1) {
        cartItem.quantity--;
    }
    displayCartData2(resultData);
};

// Function to delete a cart item
const deleteCartItem = (index) => {
    resultData.cart.splice(index, 1);
    displayCartData2(resultData);
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