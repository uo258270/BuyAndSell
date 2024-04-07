package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import com.service.PayPalService;

public class PayPalServiceTest {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Test
    public void testCreateOrder() throws IOException {
        HttpResponse<Object> mockResponse = Mockito.mock(HttpResponse.class);
        Order mockOrder = new Order();
        mockOrder.id("TEST_ORDER_ID");
 Mockito.when(mockResponse.result()).thenReturn(mockOrder);

        PayPalHttpClient mockClient = Mockito.mock(PayPalHttpClient.class);
        Mockito.when(mockClient.execute(Mockito.any())).thenReturn(mockResponse);

        PayPalService payPalService = new PayPalService();
        PayPalService.client = mockClient;

        Map<String, Object> orderRequest = new HashMap<>();

        String orderId = payPalService.createOrder(orderRequest);

        assertEquals("TEST_ORDER_ID", orderId);
    }



    @Test
    public void testGetApprovalUrl() {
        PayPalService payPalService = new PayPalService();

        String orderId = "TEST_ORDER_ID";
        String approvalUrl = payPalService.getApprovalUrl(orderId);

        // Verificar que la URL de aprobación se haya generado correctamente
        assertEquals("https://www.sandbox.paypal.com/checkoutnow?token=TEST_ORDER_ID", approvalUrl);
    }
}

