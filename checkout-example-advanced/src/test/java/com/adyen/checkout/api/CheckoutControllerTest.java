package com.adyen.checkout.api;

import com.adyen.checkout.configurations.ApplicationConfiguration;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckoutControllerTest {

    private CheckoutController checkoutController;
    private PaymentsApi paymentsApi;
    private ApplicationConfiguration applicationConfiguration;

    @BeforeEach
    void setUp() {
        applicationConfiguration = new ApplicationConfiguration();
        applicationConfiguration.setAdyenApiKey("testApiKey");
        applicationConfiguration.setAdyenMerchantAccount("TestMerchant");
        applicationConfiguration.setAdyenClientKey("testClientKey");

        paymentsApi = mock(PaymentsApi.class);
        checkoutController = new CheckoutController(paymentsApi, applicationConfiguration);
    }

    @Test
    void paymentMethods_returnsResponse() throws IOException, ApiException {
        PaymentMethodsResponse mockResponse = new PaymentMethodsResponse();
        when(paymentsApi.paymentMethods(any())).thenReturn(mockResponse);

        ResponseEntity<PaymentMethodsResponse> response = checkoutController.paymentMethods();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(paymentsApi).paymentMethods(any());
    }

    @Test
    void payments_returnsResponse() throws IOException, ApiException {
        PaymentResponse mockResponse = new PaymentResponse();
        mockResponse.setResultCode(PaymentResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.payments(any(PaymentRequest.class), any())).thenReturn(mockResponse);

        PaymentRequest body = new PaymentRequest();
        body.setBrowserInfo(new BrowserInfo());
        body.setPaymentMethod(new CheckoutPaymentMethod());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setRemoteAddr("127.0.0.1");

        ResponseEntity<PaymentResponse> response = checkoutController.payments("localhost:8080", body, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(PaymentResponse.ResultCodeEnum.AUTHORISED, response.getBody().getResultCode());
    }

    @Test
    void paymentsDetails_returnsResponse() throws IOException, ApiException {
        PaymentDetailsResponse mockResponse = new PaymentDetailsResponse();
        mockResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(mockResponse);

        PaymentDetailsRequest detailsRequest = new PaymentDetailsRequest();

        ResponseEntity<PaymentDetailsResponse> response = checkoutController.paymentsDetails(detailsRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED, response.getBody().getResultCode());
    }

    @Test
    void redirect_withRedirectResult_authorised() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutController.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/success"));
    }

    @Test
    void redirect_withRedirectResult_pending() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.PENDING);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutController.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/pending"));
    }

    @Test
    void redirect_withRedirectResult_received() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.RECEIVED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutController.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/pending"));
    }

    @Test
    void redirect_withRedirectResult_refused() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.REFUSED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutController.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/failed"));
    }

    @Test
    void redirect_withRedirectResult_error() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.ERROR);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutController.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/error"));
    }

    @Test
    void redirect_withPayload() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutController.redirect("testPayload", null);

        assertTrue(result.getUrl().contains("/result/success"));
    }

    @Test
    void redirect_withEmptyParams() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutController.redirect("", "");

        assertTrue(result.getUrl().contains("/result/success"));
    }
}
