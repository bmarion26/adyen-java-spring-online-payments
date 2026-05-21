package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
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
        applicationProperty.setApiKey("test-api-key");
        applicationProperty.setMerchantAccount("TestMerchant");
        applicationProperty.setClientKey("test-client-key");
        applicationProperty.setHmacKey("test-hmac-key");

        checkoutResource = new CheckoutResource(applicationProperty);

        paymentsApi = mock(PaymentsApi.class);
        Field paymentsApiField = CheckoutResource.class.getDeclaredField("paymentsApi");
        paymentsApiField.setAccessible(true);
        paymentsApiField.set(checkoutResource, paymentsApi);
    }

    @Test
    void constructor_throwsException_whenApiKeyIsNull() {
        ApplicationProperty props = new ApplicationProperty();
        assertThrows(RuntimeException.class, () -> new CheckoutResource(props));
    }

    @Test
    void sessions_returnsSessionResponse() throws IOException, ApiException {
        var sessionResponse = new CreateCheckoutSessionResponse();
        sessionResponse.setSessionData("test-session-data");
        sessionResponse.setId("test-session-id");
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenReturn(sessionResponse);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");

        var response = checkoutResource.sessions("localhost:8080", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-session-data", response.getBody().getSessionData());
        assertEquals("test-session-id", response.getBody().getId());
        verify(paymentsApi).sessions(any(CreateCheckoutSessionRequest.class));
    }

    @Test
    void sessions_setsCorrectRequestFields() throws IOException, ApiException {
        var sessionResponse = new CreateCheckoutSessionResponse();
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenAnswer(invocation -> {
            CreateCheckoutSessionRequest req = invocation.getArgument(0);
            assertEquals("NL", req.getCountryCode());
            assertEquals("TestMerchant", req.getMerchantAccount());
            assertEquals(CreateCheckoutSessionRequest.ChannelEnum.WEB, req.getChannel());
            assertNotNull(req.getReference());
            assertEquals("EUR", req.getAmount().getCurrency());
            assertEquals(10000L, req.getAmount().getValue());
            assertEquals("https://localhost:8080/api/handleShopperRedirect", req.getReturnUrl());
            assertNotNull(req.getLineItems());
            assertEquals(2, req.getLineItems().size());
            return sessionResponse;
        });

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");

        checkoutResource.sessions("localhost:8080", request);

        verify(paymentsApi).sessions(any(CreateCheckoutSessionRequest.class));
    }

    @Test
    void sessions_propagatesApiException() throws IOException, ApiException {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class)))
                .thenThrow(new ApiException("Unauthorized", 401));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");

        assertThrows(ApiException.class, () -> checkoutResource.sessions("localhost:8080", request));
    }

    @Test
    void sessions_propagatesIOException() throws IOException, ApiException {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class)))
                .thenThrow(new IOException("Connection failed"));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");

        assertThrows(IOException.class, () -> checkoutResource.sessions("localhost:8080", request));
    }

    @Test
    void redirect_authorised_redirectsToSuccess() throws IOException, ApiException {
        var detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "test-redirect-result");

        assertTrue(result.getUrl().contains("/result/success"));
    }

    @Test
    void redirect_pending_redirectsToPending() throws IOException, ApiException {
        var detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.PENDING);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "test-redirect-result");

        assertTrue(result.getUrl().contains("/result/pending"));
    }

    @Test
    void redirect_received_redirectsToPending() throws IOException, ApiException {
        var detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.RECEIVED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "test-redirect-result");

        assertTrue(result.getUrl().contains("/result/pending"));
    }

    @Test
    void redirect_refused_redirectsToFailed() throws IOException, ApiException {
        var detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.REFUSED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "test-redirect-result");

        assertTrue(result.getUrl().contains("/result/failed"));
    }

    @Test
    void redirect_unknownResultCode_redirectsToError() throws IOException, ApiException {
        var detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.ERROR);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "test-redirect-result");

        assertTrue(result.getUrl().contains("/result/error"));
    }

    @Test
    void redirect_usesPayloadWhenRedirectResultIsNull() throws IOException, ApiException {
        var detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenAnswer(invocation -> {
            PaymentDetailsRequest req = invocation.getArgument(0);
            assertNotNull(req.getDetails().getPayload());
            return detailsResponse;
        });

        checkoutResource.redirect("test-payload", null);

        verify(paymentsApi).paymentsDetails(any(PaymentDetailsRequest.class));
    }

    @Test
    void redirect_usesRedirectResultWhenBothProvided() throws IOException, ApiException {
        var detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenAnswer(invocation -> {
            PaymentDetailsRequest req = invocation.getArgument(0);
            assertNotNull(req.getDetails().getRedirectResult());
            return detailsResponse;
        });

        checkoutResource.redirect("test-payload", "test-redirect-result");

        verify(paymentsApi).paymentsDetails(any(PaymentDetailsRequest.class));
    }

    @Test
    void redirect_cancelled_redirectsToError() throws IOException, ApiException {
        var detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.CANCELLED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "test-redirect-result");

        assertTrue(result.getUrl().contains("/result/error"));
    }

    @Test
    void redirect_includesReasonInQueryParam() throws IOException, ApiException {
        var detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.REFUSED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "test-redirect-result");

        assertTrue(result.getUrl().contains("reason=Refused"));
    }
}
