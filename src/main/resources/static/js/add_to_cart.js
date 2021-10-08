$(document).ready(function() {
	$("#buttonAdd2Cart").on("click", function(e) {
		addToCart();
	});
});
function addToCart() {
	quantity =  $("#quantity" + productId).val();
	$.ajax({
		type: "POST",
		url: "/cart/add/" + productId + "/" + quantity
	}).done(function(response) {
//		$("#modelTitle").text("Shopping Cart");
		alert(quantity+ ' item(s) of this product were added to your shopping cart.');
		window.location.href="/cart"
//		$("#modelBody").text(response);
		//		$("#myModel").modal();
	}).fail(function() {
//		$("#modelTitle").text("Shopping Cart");
		//		$("#modelBody").text("Error while adding product to shop.");
		alert('You must login to add this product to your shopping cart.');
		//		$("#myModel").modal();
	});

}