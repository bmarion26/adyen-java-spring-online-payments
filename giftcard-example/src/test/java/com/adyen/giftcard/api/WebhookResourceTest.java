package com.adyen.giftcard.api;

import com.adyen.giftcard.ApplicationProperty;
import com.adyen.util.HMACValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void constructorWithNullHmacKey_throwsException() {
        ApplicationProperty prop = new ApplicationProperty();
        assertThrows(RuntimeException.class, () -> new WebhookResource(prop, new HMACValidator()));
    }

    @Test
    void webhooks_validHmac_authorisation_returns202() throws Exception {
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
                        "additionalData": {}
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(), eq("44782DEF547AAA06C910C43D1F7508C13573E96ADA3A520E13B7027BFA6B2F4E")))
                .thenReturn(true);

        var response = webhookResource.webhooks(json);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void webhooks_validHmac_orderOpened_returns202() throws Exception {
        String json = """
                {
                  "live": "false",
                  "notificationItems": [
                    {
                      "NotificationRequestItem": {
                        "eventCode": "ORDER_OPENED",
                        "merchantAccountCode": "TestMerchant",
                        "merchantReference": "ref123",
                        "pspReference": "psp123",
                        "amount": {"currency": "EUR", "value": 1000},
                        "success": "true",
                        "additionalData": {}
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
    void webhooks_validHmac_orderClosed_returns202() throws Exception {
        String json = """
                {
                  "live": "false",
                  "notificationItems": [
                    {
                      "NotificationRequestItem": {
                        "eventCode": "ORDER_CLOSED",
                        "merchantAccountCode": "TestMerchant",
                        "merchantReference": "ref123",
                        "pspReference": "psp123",
                        "amount": {"currency": "EUR", "value": 1000},
                        "success": "true",
                        "additionalData": {
                          "order-1-pspReference": "psp1",
                          "order-1-paymentAmount": "EUR 50.00",
                          "order-1-paymentMethod": "giftcard",
                          "order-2-pspReference": "psp2",
                          "order-2-paymentAmount": "EUR 60.00",
                          "order-2-paymentMethod": "scheme"
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
    void webhooks_validHmac_failedEvent_returns202() throws Exception {
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
                        "success": "false",
                        "reason": "Refused",
                        "additionalData": {}
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
                        "amount": {"currency": "EUR", "value": 1000},
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
    void webhooks_emptyPayload_throwsException() {
        String emptyJson = "{\"live\":\"false\",\"notificationItems\":[]}";
        assertThrows(Exception.class, () -> webhookResource.webhooks(emptyJson));
    }
}
