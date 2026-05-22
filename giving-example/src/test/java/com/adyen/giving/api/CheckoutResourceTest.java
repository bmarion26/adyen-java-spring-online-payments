package com.adyen.giving.api;

import com.adyen.giving.ApplicationProperty;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.DonationsApi;
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
    private DonationsApi donationsApi;

    @BeforeEach
    void setUp() throws Exception {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setApiKey("testApiKey");
        prop.setMerchantAccount("TestMerchant");
        prop.setDonationMerchantAccount("DonationMerchant");

        checkoutResource = new CheckoutResource(prop);

        paymentsApi = mock(PaymentsApi.class);
        donationsApi = mock(DonationsApi.class);

        Field paymentsField = CheckoutResource.class.getDeclaredField("paymentsApi");
        paymentsField.setAccessible(true);
        paymentsField.set(checkoutResource, paymentsApi);

        Field donationsField = CheckoutResource.class.getDeclaredField("donationsApi");
        donationsField.setAccessible(true);
        donationsField.set(checkoutResource, donationsApi);
    }

    @Test
    void constructor_withNullApiKey_throwsException() {
        ApplicationProperty prop = new ApplicationProperty();
        assertThrows(RuntimeException.class, () -> new CheckoutResource(prop));
    }

    @Test
    void paymentMethods_returnsResponse() throws IOException, ApiException {
        PaymentMethodsResponse mockResponse = new PaymentMethodsResponse();
        when(paymentsApi.paymentMethods(any())).thenReturn(mockResponse);

        ResponseEntity<PaymentMethodsResponse> response = checkoutResource.paymentMethods();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void paymentMethods_handlesException() throws IOException, ApiException {
        when(paymentsApi.paymentMethods(any())).thenThrow(new RuntimeException("API error"));

        ResponseEntity<PaymentMethodsResponse> response = checkoutResource.paymentMethods();

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void payments_returnsResponse() throws IOException, ApiException {
        PaymentResponse mockResponse = new PaymentResponse();
        mockResponse.setResultCode(PaymentResponse.ResultCodeEnum.AUTHORISED);
        mockResponse.setDonationToken("testDonationToken");
        mockResponse.setPspReference("testPspRef");
        when(paymentsApi.payments(any(PaymentRequest.class))).thenReturn(mockResponse);

        PaymentRequest body = new PaymentRequest();
        body.setBrowserInfo(new BrowserInfo());
        body.setPaymentMethod(new CheckoutPaymentMethod());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setRemoteAddr("127.0.0.1");

        ResponseEntity<PaymentResponse> response = checkoutResource.payments("localhost:8080", body, request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void paymentsDetails_returnsResponse() throws IOException, ApiException {
        PaymentDetailsResponse mockResponse = new PaymentDetailsResponse();
        mockResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(mockResponse);

        PaymentDetailsRequest detailsRequest = new PaymentDetailsRequest();

        ResponseEntity<PaymentDetailsResponse> response = checkoutResource.payments(detailsRequest);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void paymentsDetails_handlesException() throws IOException, ApiException {
        when(paymentsApi.paymentsDetails(any())).thenThrow(new RuntimeException("error"));

        PaymentDetailsRequest detailsRequest = new PaymentDetailsRequest();

        ResponseEntity<PaymentDetailsResponse> response = checkoutResource.payments(detailsRequest);

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void redirect_withRedirectResult_authorised() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult", "orderRef123");

        assertTrue(result.getUrl().contains("/result/success"));
    }

    @Test
    void redirect_withRedirectResult_pending() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.PENDING);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult", "orderRef123");

        assertTrue(result.getUrl().contains("/result/pending"));
    }

    @Test
    void redirect_withRedirectResult_received() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.RECEIVED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult", "orderRef123");

        assertTrue(result.getUrl().contains("/result/pending"));
    }

    @Test
    void redirect_withRedirectResult_refused() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.REFUSED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult", "orderRef123");

        assertTrue(result.getUrl().contains("/result/failed"));
    }

    @Test
    void redirect_withRedirectResult_error() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.ERROR);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult", "orderRef123");

        assertTrue(result.getUrl().contains("/result/error"));
    }

    @Test
    void redirect_withPayload() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any())).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect("testPayload", null, "orderRef123");

        assertTrue(result.getUrl().contains("/result/success"));
    }

    @Test
    void donations_handlesNotFoundException() throws IOException, ApiException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");

        Amount amount = new Amount().currency("EUR").value(500L);

        ResponseEntity<DonationPaymentResponse> response = checkoutResource.donations(amount, "localhost:8080", request);

        // Session doesn't have donation attributes, so it returns an error
        assertTrue(response.getStatusCode().value() == 404 || response.getStatusCode().value() == 500);
    }

    @Test
    void donations_withValidSession_callsDonationsApi() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.getSession().setAttribute("DonationToken", "testToken");
        request.getSession().setAttribute("PaymentOriginalPspReference", "testPsp");

        DonationPaymentResponse mockResponse = new DonationPaymentResponse();
        when(donationsApi.donations(any())).thenReturn(mockResponse);

        Amount amount = new Amount().currency("EUR").value(500L);

        ResponseEntity<DonationPaymentResponse> response = checkoutResource.donations(amount, "localhost:8080", request);

        assertEquals(200, response.getStatusCode().value());
        verify(donationsApi).donations(any());
    }
}
