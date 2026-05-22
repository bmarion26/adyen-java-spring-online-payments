package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.util.HMACValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WebhookControllerTest {

    private ApplicationProperty applicationProperty;
    private HMACValidator hmacValidator;
    private WebhookController webhookController;

    @BeforeEach
    void setUp() {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setHmacKey("44782DEF547AAA06C910C43D1F7508C13573E96ADA3A520E13B7027BFA6B2F4E");
        hmacValidator = mock(HMACValidator.class);
        webhookController = new WebhookController(applicationProperty, hmacValidator);
    }

    @Test
    void constructorWithNullHmacKey_doesNotThrow() {
        ApplicationProperty prop = new ApplicationProperty();
        assertDoesNotThrow(() -> new WebhookController(prop, new HMACValidator()));
    }

    @Test
    void webhooks_validNotification_withValidHmac_returns202() throws Exception {
        String json = """
                {
                  "live": "false",
                  "notificationItems": [
                    {
                      "NotificationRequestItem": {
                        "eventCode": "AUTHORISATION",
                        "merchantAccountCode": "TestMerchant",
                        "merchantReference": "ref123",
                        "pspReference": "psp123",
                        "amount": {"currency": "EUR", "value": 1000},
                        "success": "true",
                        "additionalData": {"alias": "H123456789012345"}
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(), eq("44782DEF547AAA06C910C43D1F7508C13573E96ADA3A520E13B7027BFA6B2F4E")))
                .thenReturn(true);

        var response = webhookController.webhooks(json);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void webhooks_validNotification_withInvalidHmac_throwsRuntimeException() throws Exception {
        String json = """
                {
                  "live": "false",
                  "notificationItems": [
                    {
                      "NotificationRequestItem": {
                        "eventCode": "AUTHORISATION",
                        "merchantAccountCode": "TestMerchant",
                        "merchantReference": "ref123",
                        "pspReference": "psp123",
                        "amount": {"currency": "EUR", "value": 1000},
                        "success": "true"
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(), any())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> webhookController.webhooks(json));
    }

    @Test
    void webhooks_emptyPayload_returnsBadRequest() throws Exception {
        String emptyJson = "{\"live\":\"false\",\"notificationItems\":[]}";
        var response = webhookController.webhooks(emptyJson);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
