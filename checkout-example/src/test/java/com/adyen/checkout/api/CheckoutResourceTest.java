package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.checkout.CreateCheckoutSessionResponse;
import com.adyen.model.checkout.PaymentDetailsResponse;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckoutResourceTest {

    private CheckoutResource checkoutResource;
    private PaymentsApi paymentsApi;
    private ApplicationProperty applicationProperty;

    @BeforeEach
    void setUp() throws Exception {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setApiKey("testApiKey");
        applicationProperty.setMerchantAccount("TestMerchant");
        applicationProperty.setClientKey("testClientKey");

        checkoutResource = new CheckoutResource(applicationProperty);

        paymentsApi = mock(PaymentsApi.class);
        Field field = CheckoutResource.class.getDeclaredField("paymentsApi");
        field.setAccessible(true);
        field.set(checkoutResource, paymentsApi);
    }

    @Test
    void constructor_withNullApiKey_throwsException() {
        ApplicationProperty prop = new ApplicationProperty();
        assertThrows(RuntimeException.class, () -> new CheckoutResource(prop));
    }

    @Test
    void sessions_returnsSessionResponse() throws IOException, ApiException {
        CreateCheckoutSessionResponse mockResponse = new CreateCheckoutSessionResponse();
        mockResponse.setId("test-session-id");
        when(paymentsApi.sessions(any())).thenReturn(mockResponse);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");

        ResponseEntity<CreateCheckoutSessionResponse> response = checkoutResource.sessions("localhost:8080", request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("test-session-id", response.getBody().getId());
        verify(paymentsApi).sessions(any());
    }

    @Test
    void redirect_withRedirectResult_authorised() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/success"));
    }

    @Test
    void redirect_withRedirectResult_pending() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.PENDING);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/pending"));
    }

    @Test
    void redirect_withRedirectResult_received() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.RECEIVED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/pending"));
    }

    @Test
    void redirect_withRedirectResult_refused() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.REFUSED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/failed"));
    }

    @Test
    void redirect_withRedirectResult_error() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.ERROR);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/error"));
    }

    @Test
    void redirect_withPayload() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect("testPayload", null);

        assertTrue(result.getUrl().contains("/result/success"));
    }

    @Test
    void redirect_withEmptyParams() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect("", "");

        assertTrue(result.getUrl().contains("/result/success"));
    }
}
