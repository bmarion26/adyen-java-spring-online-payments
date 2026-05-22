package com.adyen.paybylink.service;

import com.adyen.model.checkout.PaymentLinkResponse;
import com.adyen.paybylink.ApplicationProperty;
import com.adyen.paybylink.model.NewLinkRequest;
import com.adyen.service.checkout.PaymentLinksApi;
import com.adyen.service.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PaymentLinkServiceTest {

    private PaymentLinkService paymentLinkService;
    private PaymentLinksApi paymentLinksApi;

    @BeforeEach
    void setUp() throws Exception {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setApiKey("testApiKey");
        prop.setMerchantAccount("TestMerchant");

        paymentLinkService = new PaymentLinkService(prop);

        paymentLinksApi = mock(PaymentLinksApi.class);
        Field field = PaymentLinkService.class.getDeclaredField("paymentLinksApi");
        field.setAccessible(true);
        field.set(paymentLinkService, paymentLinksApi);

        // Clear static links map
        Field linksField = PaymentLinkService.class.getDeclaredField("links");
        linksField.setAccessible(true);
        ((java.util.HashMap<?, ?>) linksField.get(null)).clear();
    }

    @Test
    void constructor_withNullApiKey_throwsException() {
        ApplicationProperty prop = new ApplicationProperty();
        assertThrows(RuntimeException.class, () -> new PaymentLinkService(prop));
    }

    @Test
    void addLink_createsAndReturnsLink() throws IOException, ApiException {
        PaymentLinkResponse mockResponse = new PaymentLinkResponse();
        mockResponse.setId("PL123");
        when(paymentLinksApi.paymentLinks(any())).thenReturn(mockResponse);

        NewLinkRequest request = new NewLinkRequest(100L, "ref-123");

        PaymentLinkResponse result = paymentLinkService.addLink(request, "http://localhost:8080");

        assertEquals("PL123", result.getId());
        verify(paymentLinksApi).paymentLinks(any());
    }

    @Test
    void addLink_withNullReference_generatesUUID() throws IOException, ApiException {
        PaymentLinkResponse mockResponse = new PaymentLinkResponse();
        mockResponse.setId("PL456");
        when(paymentLinksApi.paymentLinks(any())).thenReturn(mockResponse);

        NewLinkRequest request = new NewLinkRequest(50L, null);

        PaymentLinkResponse result = paymentLinkService.addLink(request, "http://localhost:8080");

        assertEquals("PL456", result.getId());
    }

    @Test
    void getLinks_returnsAllLinks() throws IOException, ApiException {
        PaymentLinkResponse mockResponse = new PaymentLinkResponse();
        mockResponse.setId("PL789");
        when(paymentLinksApi.paymentLinks(any())).thenReturn(mockResponse);
        when(paymentLinksApi.getPaymentLink("PL789")).thenReturn(mockResponse);

        paymentLinkService.addLink(new NewLinkRequest(100L, "ref1"), "http://localhost");

        List<PaymentLinkResponse> links = paymentLinkService.getLinks();
        assertEquals(1, links.size());
    }

    @Test
    void getLink_returnsSpecificLink() throws IOException, ApiException {
        PaymentLinkResponse mockResponse = new PaymentLinkResponse();
        mockResponse.setId("PL100");
        when(paymentLinksApi.paymentLinks(any())).thenReturn(mockResponse);
        when(paymentLinksApi.getPaymentLink("PL100")).thenReturn(mockResponse);

        paymentLinkService.addLink(new NewLinkRequest(100L, "ref1"), "http://localhost");

        PaymentLinkResponse link = paymentLinkService.getLink("PL100");
        assertEquals("PL100", link.getId());
    }

    @Test
    void updateLink_handlesApiException() throws IOException, ApiException {
        when(paymentLinksApi.getPaymentLink(anyString())).thenThrow(new IOException("API error"));

        // Should not throw - exception is caught internally
        assertDoesNotThrow(() -> paymentLinkService.updateLink("nonexistent"));
    }
}
