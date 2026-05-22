package com.adyen.paybylink.api;

import com.adyen.model.checkout.PaymentLinkResponse;
import com.adyen.paybylink.ApplicationProperty;
import com.adyen.paybylink.model.NewLinkRequest;
import com.adyen.paybylink.service.PaymentLinkService;
import com.adyen.service.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PaymentLinkControllerTest {

    private PaymentLinkController controller;
    private PaymentLinkService paymentLinkService;

    @BeforeEach
    void setUp() throws Exception {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setApiKey("testApiKey");
        prop.setMerchantAccount("testMerchant");

        controller = new PaymentLinkController(prop);

        paymentLinkService = mock(PaymentLinkService.class);
        Field plsField = PaymentLinkController.class.getDeclaredField("paymentLinkService");
        plsField.setAccessible(true);
        plsField.set(controller, paymentLinkService);
    }

    @Test
    void constructorWithNullApiKey_throwsException() {
        ApplicationProperty prop = new ApplicationProperty();
        assertThrows(RuntimeException.class, () -> new PaymentLinkController(prop));
    }

    @Test
    void createLink_returnsPaymentLinkResponse() throws IOException, ApiException {
        PaymentLinkResponse mockResponse = new PaymentLinkResponse();
        mockResponse.setId("PL123");
        when(paymentLinkService.addLink(any(), any())).thenReturn(mockResponse);

        NewLinkRequest request = new NewLinkRequest();
        request.setAmount(100L);
        request.setReference("test-ref");

        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setScheme("http");
        httpRequest.setServerName("localhost");
        httpRequest.setServerPort(8080);

        PaymentLinkResponse result = controller.createLink(request, httpRequest);

        assertEquals("PL123", result.getId());
        verify(paymentLinkService).addLink(eq(request), eq("http://localhost:8080"));
    }

    @Test
    void getAllLinks_returnsList() {
        List<PaymentLinkResponse> mockLinks = new ArrayList<>();
        mockLinks.add(new PaymentLinkResponse());
        when(paymentLinkService.getLinks()).thenReturn(mockLinks);

        List<PaymentLinkResponse> result = controller.getAllLinks();

        assertEquals(1, result.size());
        verify(paymentLinkService).getLinks();
    }

    @Test
    void getLink_returnsLink() {
        PaymentLinkResponse mockLink = new PaymentLinkResponse();
        mockLink.setId("PL123");
        when(paymentLinkService.getLink("PL123")).thenReturn(mockLink);

        PaymentLinkResponse result = controller.getLink("PL123");

        assertEquals("PL123", result.getId());
        verify(paymentLinkService).getLink("PL123");
    }
}
