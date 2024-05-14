package com.tfg.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersCreateRequest;

@Service
public class PayPalService {

	@Value("${paypal.client-id}")
	private static String clientId;

	@Value("${paypal.client-secret}")
	private static String clientSecret;

	public static PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
	public static PayPalHttpClient client = new PayPalHttpClient(environment);

	public String createOrder(Map<String, Object> orderRequest) throws IOException {
		OrdersCreateRequest request = new OrdersCreateRequest();
		request.requestBody(orderRequest);
		HttpResponse<Order> response = client.execute(request);
		return ( response).result().id();
	}

	public  String getApprovalUrl(String orderId) {
		return "https://www.sandbox.paypal.com/checkoutnow?token=" + orderId; // En Sandbox
	}

}
