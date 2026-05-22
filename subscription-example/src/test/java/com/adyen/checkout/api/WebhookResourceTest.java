package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.util.HMACValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebhookResourceTest {

    private ApplicationProperty applicationProperty;
    private HMACValidator hmacValidator;
    private WebhookResource webhookResource;

    @BeforeEach
    void setUp() {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setHmacKey("44782DEF547AAA06C910C43D1F7508C13573E96ADA3A520E13B7027BFA6B2F4E");
        hmacValidator = mock(HMACValidator.class);
        webhookResource = new WebhookResource(applicationProperty, hmacValidator);
    }

    @Test
    void constructorWithNullHmacKey_doesNotThrow() {
        ApplicationProperty prop = new ApplicationProperty();
        assertDoesNotThrow(() -> new WebhookResource(prop, new HMACValidator()));
    }

    @Test
    void webhooks_validAuthorisationNotification_returns202() throws Exception {
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
                        "amount": {"currency": "EUR", "value": 0},
                        "success": "true"
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(), any())).thenReturn(true);

        var response = webhookResource.webhooks(json);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void webhooks_invalidHmac_throwsRuntimeException() throws Exception {
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
                        "amount": {"currency": "EUR", "value": 0},
                        "success": "true"
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(), any())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> webhookResource.webhooks(json));
    }

    @Test
    void webhooks_recurringContractEvent_storesToken() throws Exception {
        String json = """
                {
                  "live": "false",
                  "notificationItems": [
                    {
                      "NotificationRequestItem": {
                        "eventCode": "RECURRING_CONTRACT",
                        "merchantAccountCode": "TestMerchant",
                        "merchantReference": "ref123",
                        "pspReference": "psp123",
                        "paymentMethod": "visa",
                        "amount": {"currency": "EUR", "value": 0},
                        "success": "true",
                        "additionalData": {
                          "recurring.shopperReference": "shopper1",
                          "recurring.recurringDetailReference": "token123"
                        }
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(), any())).thenReturn(true);

        var response = webhookResource.webhooks(json);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void webhooks_failedEvent_returns202() throws Exception {
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
                        "amount": {"currency": "EUR", "value": 0},
                        "success": "false",
                        "reason": "Refused"
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(), any())).thenReturn(true);

        var response = webhookResource.webhooks(json);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }
}
