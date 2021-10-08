$(document).ready(function() {

	$(".minusButton").on("click", function(evt) {
		evt.preventDefault();
		descreaseQuantity($(this));

	});
	$(".plusButton").on("click", function(evt) {
		evt.preventDefault();
		increaseQuantity($(this));
	});

//	$(".link-remove").on("click", function(evt) {
//		evt.preventDefault();
//		removeFromCart($(this))
//	});
	updateTotal();
});

//function removeFromCart(link) {
//	$.ajax({
//		type: "POST",
//		url: link.attr("href")
//	}).done(function(response) {
//		alert('AAAA');
//		$("#modelTitle").text("Shopping Cart");
//		if (response.includes("removed")) {
//			$("#myModel").on("hide.bs.modal", function(e) {
//				rowNumber = link.attr("rowNumber");
//				removeProduct(rowNumber);
//				updateTotal();
//			});
//		}
//		$("#modelBody").text(response);
//		//		$("#myModel").modal();
//	}).fail(function() {
//		$("#modelTitle").text("Shopping Cart");
//		$("#modelBody").text("Error while adding product to shop.");
//		//		alert('You must login to add this product to your shopping cart.');
//		//		$("#myModel").modal();
//	});
//}

//function removeProduct() {
//	rowId = "row" + rowNumber;
//	$("#" + rowId).remove();
//}
function descreaseQuantity(link) {
	productId = link.attr("pid");
	qtyInput = $("#quantity" + productId);
	newQty = parseInt(qtyInput.val()) - 1;
	if (newQty > 0) {
		qtyInput.val(newQty + 1);
		updateQuantity(productId, newQty + 1);
	}
}
function increaseQuantity(link) {
	productId = link.attr("pid");
	qtyInput = $("#quantity" + productId);
	newQty = parseInt(qtyInput.val()) + 1;
	if (newQty > 0) {
		qtyInput.val(newQty - 1);
		updateQuantity(productId, newQty - 1);
	}
}
function updateQuantity(productId, quantity) {
	$.ajax({
		type: "POST",
		url: "/cart/update/" + productId + "/" + quantity
	}).done(function(newSubtotal) {
		updateSubtotal(newSubtotal, productId);
		updateTotal();

	}).fail(function() {
		$("#modelTitle").text("Shopping Cart");
		$("#modelBody").text("Error while adding product to shop.");
		//		alert('You must login to add this product to your shopping cart.');
		//		$("#myModel").modal();
	});
}
function updateSubtotal(newSubtotal, productId) {
	$("#subtotal" + productId).text(newSubtotal);


}
function updateTotal() {
	total = 0.0;
	$(".productSubtotal").each(function(index, element) {
		total = total + parseFloat(element.innerHTML);
	});
	$("#totalAmount").text('$ ' + total.toFixed(1));
}