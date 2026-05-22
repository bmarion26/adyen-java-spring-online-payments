package com.adyen.giftcard.api;

import com.adyen.giftcard.ApplicationProperty;
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

class GiftcardResourceTest {

    private GiftcardResource giftcardResource;
    private PaymentsApi paymentsApi;

    @BeforeEach
    void setUp() throws Exception {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setApiKey("testApiKey");
        prop.setMerchantAccount("TestMerchant");

        giftcardResource = new GiftcardResource(prop);

        paymentsApi = mock(PaymentsApi.class);
        Field field = GiftcardResource.class.getDeclaredField("paymentsApi");
        field.setAccessible(true);
        field.set(giftcardResource, paymentsApi);
    }

    @Test
    void constructor_withNullApiKey_throwsException() {
        ApplicationProperty prop = new ApplicationProperty();
        assertThrows(RuntimeException.class, () -> new GiftcardResource(prop));
    }

    @Test
    void sessions_returnsSessionResponse() throws IOException, ApiException {
        CreateCheckoutSessionResponse mockResponse = new CreateCheckoutSessionResponse();
        mockResponse.setId("test-session-id");
        when(paymentsApi.sessions(any())).thenReturn(mockResponse);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");

        ResponseEntity<CreateCheckoutSessionResponse> response = giftcardResource.sessions("localhost:8080", "dropin", request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("test-session-id", response.getBody().getId());
    }
}
