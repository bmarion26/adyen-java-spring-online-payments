package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.checkout.CreateCheckoutSessionResponse;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubscriptionResourceTest {

    private SubscriptionResource subscriptionResource;
    private PaymentsApi paymentsApi;

    @BeforeEach
    void setUp() throws Exception {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setApiKey("testApiKey");
        prop.setMerchantAccount("TestMerchant");

        subscriptionResource = new SubscriptionResource(prop);

        paymentsApi = mock(PaymentsApi.class);
        Field field = SubscriptionResource.class.getDeclaredField("paymentsApi");
        field.setAccessible(true);
        field.set(subscriptionResource, paymentsApi);
    }

    @Test
    void constructor_withNullApiKey_throwsException() {
        ApplicationProperty prop = new ApplicationProperty();
        assertThrows(RuntimeException.class, () -> new SubscriptionResource(prop));
    }

    @Test
    void sessions_returnsSessionResponse() throws IOException, ApiException {
        CreateCheckoutSessionResponse mockResponse = new CreateCheckoutSessionResponse();
        mockResponse.setId("test-session-id");
        when(paymentsApi.sessions(any())).thenReturn(mockResponse);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");

        ResponseEntity<CreateCheckoutSessionResponse> response = subscriptionResource.sessions("localhost:8080", request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("test-session-id", response.getBody().getId());
        verify(paymentsApi).sessions(any());
    }
}
