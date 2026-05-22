package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckoutResourceTest {

    private ApplicationProperty applicationProperty;
    private PaymentsApi paymentsApi;
    private CheckoutResource checkoutResource;

    @BeforeEach
    void setUp() throws Exception {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setApiKey("testApiKey");
        applicationProperty.setMerchantAccount("TestMerchantAccount");

        checkoutResource = new CheckoutResource(applicationProperty);

        paymentsApi = mock(PaymentsApi.class);
        Field field = CheckoutResource.class.getDeclaredField("paymentsApi");
        field.setAccessible(true);
        field.set(checkoutResource, paymentsApi);
    }

    @Test
    void constructor_nullApiKey_throwsRuntimeException() {
        ApplicationProperty prop = new ApplicationProperty();
        assertThrows(RuntimeException.class, () -> new CheckoutResource(prop));
    }

    @Test
    void sessions_success_returnsSessionResponse() throws IOException, ApiException {
        CreateCheckoutSessionResponse mockResponse = new CreateCheckoutSessionResponse();
        mockResponse.setId("test-session-id");
        mockResponse.setSessionData("test-session-data");
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenReturn(mockResponse);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");

        var response = checkoutResource.sessions("localhost:8080", request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("test-session-id", response.getBody().getId());
        assertEquals("test-session-data", response.getBody().getSessionData());
        verify(paymentsApi).sessions(any(CreateCheckoutSessionRequest.class));
    }

    @Test
    void sessions_setsCorrectRequestFields() throws IOException, ApiException {
        CreateCheckoutSessionResponse mockResponse = new CreateCheckoutSessionResponse();
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenAnswer(invocation -> {
            CreateCheckoutSessionRequest req = invocation.getArgument(0);
            assertEquals("NL", req.getCountryCode());
            assertEquals("TestMerchantAccount", req.getMerchantAccount());
            assertEquals(CreateCheckoutSessionRequest.ChannelEnum.WEB, req.getChannel());
            assertNotNull(req.getReference());
            assertEquals("https://myshop.com/api/handleShopperRedirect", req.getReturnUrl());
            assertEquals(10000L, req.getAmount().getValue());
            assertEquals("EUR", req.getAmount().getCurrency());
            assertEquals(2, req.getLineItems().size());
            return mockResponse;
        });

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");

        checkoutResource.sessions("myshop.com", request);
        verify(paymentsApi).sessions(any(CreateCheckoutSessionRequest.class));
    }

    @Test
    void sessions_apiException_propagates() throws IOException, ApiException {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class)))
                .thenThrow(new ApiException("API error", 422));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");

        assertThrows(ApiException.class, () -> checkoutResource.sessions("localhost", request));
    }

    @Test
    void sessions_ioException_propagates() throws IOException, ApiException {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class)))
                .thenThrow(new IOException("Connection error"));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");

        assertThrows(IOException.class, () -> checkoutResource.sessions("localhost", request));
    }

    @Test
    void redirect_authorised_redirectsToSuccess() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/success"));
        assertTrue(result.getUrl().toLowerCase().contains("reason=authorised"));
    }

    @Test
    void redirect_pending_redirectsToPending() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.PENDING);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/pending"));
        assertTrue(result.getUrl().toLowerCase().contains("reason=pending"));
    }

    @Test
    void redirect_received_redirectsToPending() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.RECEIVED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/pending"));
        assertTrue(result.getUrl().toLowerCase().contains("reason=received"));
    }

    @Test
    void redirect_refused_redirectsToFailed() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.REFUSED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/failed"));
        assertTrue(result.getUrl().toLowerCase().contains("reason=refused"));
    }

    @Test
    void redirect_unknownResultCode_redirectsToError() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.ERROR);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/error"));
        assertTrue(result.getUrl().toLowerCase().contains("reason=error"));
    }

    @Test
    void redirect_cancelled_redirectsToError() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.CANCELLED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenReturn(detailsResponse);

        RedirectView result = checkoutResource.redirect(null, "testRedirectResult");

        assertTrue(result.getUrl().contains("/result/error"));
    }

    @Test
    void redirect_withRedirectResult_setsCompletionDetails() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenAnswer(invocation -> {
            PaymentDetailsRequest req = invocation.getArgument(0);
            assertEquals("myRedirectResult", req.getDetails().getRedirectResult());
            assertNull(req.getDetails().getPayload());
            return detailsResponse;
        });

        checkoutResource.redirect(null, "myRedirectResult");
        verify(paymentsApi).paymentsDetails(any(PaymentDetailsRequest.class));
    }

    @Test
    void redirect_withPayload_setsCompletionDetails() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenAnswer(invocation -> {
            PaymentDetailsRequest req = invocation.getArgument(0);
            assertNull(req.getDetails().getRedirectResult());
            assertEquals("myPayload", req.getDetails().getPayload());
            return detailsResponse;
        });

        checkoutResource.redirect("myPayload", null);
        verify(paymentsApi).paymentsDetails(any(PaymentDetailsRequest.class));
    }

    @Test
    void redirect_withBothParams_prefersRedirectResult() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenAnswer(invocation -> {
            PaymentDetailsRequest req = invocation.getArgument(0);
            assertEquals("myRedirectResult", req.getDetails().getRedirectResult());
            return detailsResponse;
        });

        checkoutResource.redirect("myPayload", "myRedirectResult");
        verify(paymentsApi).paymentsDetails(any(PaymentDetailsRequest.class));
    }

    @Test
    void redirect_withEmptyRedirectResult_usesPayload() throws IOException, ApiException {
        PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
        detailsResponse.setResultCode(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class))).thenAnswer(invocation -> {
            PaymentDetailsRequest req = invocation.getArgument(0);
            assertEquals("myPayload", req.getDetails().getPayload());
            return detailsResponse;
        });

        checkoutResource.redirect("myPayload", "");
        verify(paymentsApi).paymentsDetails(any(PaymentDetailsRequest.class));
    }

    @Test
    void redirect_apiException_propagates() throws IOException, ApiException {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenThrow(new ApiException("Redirect error", 500));

        assertThrows(ApiException.class, () -> checkoutResource.redirect(null, "testRedirect"));
    }
}
