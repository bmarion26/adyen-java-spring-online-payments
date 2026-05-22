package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutResourceTest {

    private CheckoutResource checkoutResource;

    @Mock
    private PaymentsApi paymentsApi;

    @Mock
    private HttpServletRequest request;

    private ApplicationProperty applicationProperty;

    @BeforeEach
    void setUp() {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setApiKey("testApiKey");
        applicationProperty.setMerchantAccount("TestMerchantAccount");
        checkoutResource = new CheckoutResource(applicationProperty);
        ReflectionTestUtils.setField(checkoutResource, "paymentsApi", paymentsApi);
    }

    // --- Constructor tests ---

    @Test
    void constructor_throwsWhenApiKeyIsNull() {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setApiKey(null);
        assertThrows(RuntimeException.class, () -> new CheckoutResource(prop));
    }

    @Test
    void constructor_succeedsWithValidApiKey() {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setApiKey("validKey");
        assertDoesNotThrow(() -> new CheckoutResource(prop));
    }

    // --- POST /api/sessions tests ---

    @Test
    void sessions_returnsSuccessResponse() throws Exception {
        var expectedResponse = new CreateCheckoutSessionResponse();
        expectedResponse.setId("test-session-id");
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenReturn(expectedResponse);
        when(request.getScheme()).thenReturn("http");

        var response = checkoutResource.sessions("localhost:8080", request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("test-session-id", response.getBody().getId());
    }

    @Test
    void sessions_setsCorrectAmount() throws Exception {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenReturn(new CreateCheckoutSessionResponse());
        when(request.getScheme()).thenReturn("http");

        checkoutResource.sessions("localhost:8080", request);

        ArgumentCaptor<CreateCheckoutSessionRequest> captor = ArgumentCaptor.forClass(CreateCheckoutSessionRequest.class);
        verify(paymentsApi).sessions(captor.capture());
        var req = captor.getValue();
        assertEquals("EUR", req.getAmount().getCurrency());
        assertEquals(10000L, req.getAmount().getValue());
    }

    @Test
    void sessions_setsCorrectMerchantAccount() throws Exception {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenReturn(new CreateCheckoutSessionResponse());
        when(request.getScheme()).thenReturn("http");

        checkoutResource.sessions("localhost:8080", request);

        ArgumentCaptor<CreateCheckoutSessionRequest> captor = ArgumentCaptor.forClass(CreateCheckoutSessionRequest.class);
        verify(paymentsApi).sessions(captor.capture());
        assertEquals("TestMerchantAccount", captor.getValue().getMerchantAccount());
    }

    @Test
    void sessions_setsLineItems() throws Exception {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenReturn(new CreateCheckoutSessionResponse());
        when(request.getScheme()).thenReturn("http");

        checkoutResource.sessions("localhost:8080", request);

        ArgumentCaptor<CreateCheckoutSessionRequest> captor = ArgumentCaptor.forClass(CreateCheckoutSessionRequest.class);
        verify(paymentsApi).sessions(captor.capture());
        var lineItems = captor.getValue().getLineItems();
        assertEquals(2, lineItems.size());
        assertEquals("Sunglasses", lineItems.get(0).getDescription());
        assertEquals(5000L, lineItems.get(0).getAmountIncludingTax());
        assertEquals("Headphones", lineItems.get(1).getDescription());
        assertEquals(5000L, lineItems.get(1).getAmountIncludingTax());
    }

    @Test
    void sessions_setsReturnUrl() throws Exception {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenReturn(new CreateCheckoutSessionResponse());
        when(request.getScheme()).thenReturn("https");

        checkoutResource.sessions("example.com", request);

        ArgumentCaptor<CreateCheckoutSessionRequest> captor = ArgumentCaptor.forClass(CreateCheckoutSessionRequest.class);
        verify(paymentsApi).sessions(captor.capture());
        assertEquals("https://example.com/api/handleShopperRedirect", captor.getValue().getReturnUrl());
    }

    @Test
    void sessions_setsChannelToWeb() throws Exception {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenReturn(new CreateCheckoutSessionResponse());
        when(request.getScheme()).thenReturn("http");

        checkoutResource.sessions("localhost:8080", request);

        ArgumentCaptor<CreateCheckoutSessionRequest> captor = ArgumentCaptor.forClass(CreateCheckoutSessionRequest.class);
        verify(paymentsApi).sessions(captor.capture());
        assertEquals(CreateCheckoutSessionRequest.ChannelEnum.WEB, captor.getValue().getChannel());
    }

    @Test
    void sessions_setsCountryCodeNL() throws Exception {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenReturn(new CreateCheckoutSessionResponse());
        when(request.getScheme()).thenReturn("http");

        checkoutResource.sessions("localhost:8080", request);

        ArgumentCaptor<CreateCheckoutSessionRequest> captor = ArgumentCaptor.forClass(CreateCheckoutSessionRequest.class);
        verify(paymentsApi).sessions(captor.capture());
        assertEquals("NL", captor.getValue().getCountryCode());
    }

    @Test
    void sessions_setsReference() throws Exception {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class))).thenReturn(new CreateCheckoutSessionResponse());
        when(request.getScheme()).thenReturn("http");

        checkoutResource.sessions("localhost:8080", request);

        ArgumentCaptor<CreateCheckoutSessionRequest> captor = ArgumentCaptor.forClass(CreateCheckoutSessionRequest.class);
        verify(paymentsApi).sessions(captor.capture());
        assertNotNull(captor.getValue().getReference());
    }

    @Test
    void sessions_throwsWhenApiException() throws Exception {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class)))
                .thenThrow(new ApiException("API error", 401));
        when(request.getScheme()).thenReturn("http");

        assertThrows(ApiException.class, () -> checkoutResource.sessions("localhost:8080", request));
    }

    @Test
    void sessions_throwsWhenIOException() throws Exception {
        when(paymentsApi.sessions(any(CreateCheckoutSessionRequest.class)))
                .thenThrow(new IOException("IO error"));
        when(request.getScheme()).thenReturn("http");

        assertThrows(IOException.class, () -> checkoutResource.sessions("localhost:8080", request));
    }

    // --- GET /api/handleShopperRedirect tests ---

    private PaymentDetailsResponse buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum resultCode) {
        var response = new PaymentDetailsResponse();
        response.setResultCode(resultCode);
        return response;
    }

    @Test
    void redirect_withRedirectResult_authorised_redirectsToSuccess() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED));

        RedirectView view = checkoutResource.redirect(null, "someRedirectResult");

        assertTrue(view.getUrl().startsWith("/result/success"));
    }

    @Test
    void redirect_withRedirectResult_pending_redirectsToPending() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.PENDING));

        RedirectView view = checkoutResource.redirect(null, "someRedirectResult");

        assertTrue(view.getUrl().startsWith("/result/pending"));
    }

    @Test
    void redirect_withRedirectResult_received_redirectsToPending() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.RECEIVED));

        RedirectView view = checkoutResource.redirect(null, "someRedirectResult");

        assertTrue(view.getUrl().startsWith("/result/pending"));
    }

    @Test
    void redirect_withRedirectResult_refused_redirectsToFailed() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.REFUSED));

        RedirectView view = checkoutResource.redirect(null, "someRedirectResult");

        assertTrue(view.getUrl().startsWith("/result/failed"));
    }

    @Test
    void redirect_withRedirectResult_cancelled_redirectsToError() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.CANCELLED));

        RedirectView view = checkoutResource.redirect(null, "someRedirectResult");

        assertTrue(view.getUrl().startsWith("/result/error"));
    }

    @Test
    void redirect_withRedirectResult_error_redirectsToError() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.ERROR));

        RedirectView view = checkoutResource.redirect(null, "someRedirectResult");

        assertTrue(view.getUrl().startsWith("/result/error"));
    }

    @Test
    void redirect_withPayload_callsPaymentsDetails() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED));

        checkoutResource.redirect("somePayload", null);

        ArgumentCaptor<PaymentDetailsRequest> captor = ArgumentCaptor.forClass(PaymentDetailsRequest.class);
        verify(paymentsApi).paymentsDetails(captor.capture());
        assertEquals("somePayload", captor.getValue().getDetails().getPayload());
        assertNull(captor.getValue().getDetails().getRedirectResult());
    }

    @Test
    void redirect_withRedirectResult_callsPaymentsDetailsWithRedirectResult() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED));

        checkoutResource.redirect(null, "someResult");

        ArgumentCaptor<PaymentDetailsRequest> captor = ArgumentCaptor.forClass(PaymentDetailsRequest.class);
        verify(paymentsApi).paymentsDetails(captor.capture());
        assertEquals("someResult", captor.getValue().getDetails().getRedirectResult());
    }

    @Test
    void redirect_redirectResultTakesPrecedenceOverPayload() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED));

        checkoutResource.redirect("somePayload", "someRedirectResult");

        ArgumentCaptor<PaymentDetailsRequest> captor = ArgumentCaptor.forClass(PaymentDetailsRequest.class);
        verify(paymentsApi).paymentsDetails(captor.capture());
        assertEquals("someRedirectResult", captor.getValue().getDetails().getRedirectResult());
    }

    @Test
    void redirect_includesReasonQueryParam() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED));

        RedirectView view = checkoutResource.redirect(null, "someRedirectResult");

        assertTrue(view.getUrl().contains("?reason=Authorised"));
    }

    @Test
    void redirect_withEmptyRedirectResultAndEmptyPayload_callsPaymentsDetails() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED));

        checkoutResource.redirect("", "");

        ArgumentCaptor<PaymentDetailsRequest> captor = ArgumentCaptor.forClass(PaymentDetailsRequest.class);
        verify(paymentsApi).paymentsDetails(captor.capture());
        assertNull(captor.getValue().getDetails().getRedirectResult());
        assertNull(captor.getValue().getDetails().getPayload());
    }

    @Test
    void redirect_withNullBoth_callsPaymentsDetails() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenReturn(buildDetailsResponse(PaymentDetailsResponse.ResultCodeEnum.AUTHORISED));

        checkoutResource.redirect(null, null);

        verify(paymentsApi).paymentsDetails(any(PaymentDetailsRequest.class));
    }

    @Test
    void redirect_throwsWhenApiException() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenThrow(new ApiException("Details error", 500));

        assertThrows(ApiException.class, () -> checkoutResource.redirect(null, "result"));
    }

    @Test
    void redirect_throwsWhenIOException() throws Exception {
        when(paymentsApi.paymentsDetails(any(PaymentDetailsRequest.class)))
                .thenThrow(new IOException("IO error"));

        assertThrows(IOException.class, () -> checkoutResource.redirect(null, "result"));
    }
}
