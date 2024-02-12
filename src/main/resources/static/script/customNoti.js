$(document).ready(function() {

	// New code for the additional button
	$("#btnSayHola").click(function() {
		console.log("Hola");
		console.log("Custom notification script loaded");
	});


	$("#addToCart").click(function() {
		const message = $(this).data("message") || "";
		showNotification(message);
	});

	$("#close").click(function() {
		hideNotification();
	});


	function showNotification(message) {
		const notification = document.getElementById("notification");
		notification.classList.add("notification-show");
		const notificationText = document.querySelector(".notification p");
		notificationText.textContent = message;
	}

	function hideNotification() {
		notification.classList.remove("notification-show");
	}

});
