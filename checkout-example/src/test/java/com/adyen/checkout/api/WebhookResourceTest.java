package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.util.HMACValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.security.SignatureException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WebhookResourceTest {

    private WebhookResource webhookResource;
    private ApplicationProperty applicationProperty;
    private HMACValidator hmacValidator;

    private static final String VALID_WEBHOOK_JSON = """
            {
              "live": "false",
              "notificationItems": [
                {
                  "NotificationRequestItem": {
                    "eventCode": "AUTHORISATION",
                    "merchantAccountCode": "TestMerchant",
                    "merchantReference": "ref-123",
                    "pspReference": "psp-456",
                    "amount": {
                      "currency": "EUR",
                      "value": 10000
                    },
                    "success": "true",
                    "additionalData": {
                      "alias": "test-alias",
                      "hmacSignature": "test-signature"
                    }
                  }
                }
              ]
            }
            """;

    @BeforeEach
    void setUp() {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setHmacKey("test-hmac-key");
        hmacValidator = mock(HMACValidator.class);
        webhookResource = new WebhookResource(applicationProperty, hmacValidator);
    }

    @Test
    void webhooks_validHmac_returnsAccepted() throws Exception {
        when(hmacValidator.validateHMAC(any(NotificationRequestItem.class), eq("test-hmac-key")))
                .thenReturn(true);

        var response = webhookResource.webhooks(VALID_WEBHOOK_JSON);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void webhooks_invalidHmac_throwsRuntimeException() throws Exception {
        when(hmacValidator.validateHMAC(any(NotificationRequestItem.class), eq("test-hmac-key")))
                .thenReturn(false);

        assertThrows(RuntimeException.class, () -> webhookResource.webhooks(VALID_WEBHOOK_JSON));
    }

    @Test
    void webhooks_hmacValidationThrowsSignatureException() throws Exception {
        when(hmacValidator.validateHMAC(any(NotificationRequestItem.class), eq("test-hmac-key")))
                .thenThrow(new SignatureException("Invalid key"));

        assertThrows(SignatureException.class, () -> webhookResource.webhooks(VALID_WEBHOOK_JSON));
    }

    @Test
    void webhooks_emptyNotificationItems_throwsException() {
        String emptyItemsJson = """
                {
                  "live": "false",
                  "notificationItems": []
                }
                """;

        assertThrows(Exception.class, () -> webhookResource.webhooks(emptyItemsJson));
    }

    @Test
    void webhooks_refusedPayment_validHmac_returnsAccepted() throws Exception {
        String refusedJson = """
                {
                  "live": "false",
                  "notificationItems": [
                    {
                      "NotificationRequestItem": {
                        "eventCode": "AUTHORISATION",
                        "merchantAccountCode": "TestMerchant",
                        "merchantReference": "ref-refused",
                        "pspReference": "psp-refused",
                        "amount": {
                          "currency": "EUR",
                          "value": 10000
                        },
                        "success": "false",
                        "reason": "Refused",
                        "additionalData": {
                          "alias": "test-alias",
                          "hmacSignature": "test-signature"
                        }
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(NotificationRequestItem.class), eq("test-hmac-key")))
                .thenReturn(true);

        var response = webhookResource.webhooks(refusedJson);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void webhooks_cancellationEvent_validHmac_returnsAccepted() throws Exception {
        String cancellationJson = """
                {
                  "live": "false",
                  "notificationItems": [
                    {
                      "NotificationRequestItem": {
                        "eventCode": "CANCELLATION",
                        "merchantAccountCode": "TestMerchant",
                        "merchantReference": "ref-cancel",
                        "pspReference": "psp-cancel",
                        "amount": {
                          "currency": "EUR",
                          "value": 10000
                        },
                        "success": "true",
                        "additionalData": {
                          "alias": "test-alias",
                          "hmacSignature": "test-signature"
                        }
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(NotificationRequestItem.class), eq("test-hmac-key")))
                .thenReturn(true);

        var response = webhookResource.webhooks(cancellationJson);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void webhooks_captureEvent_validHmac_returnsAccepted() throws Exception {
        String captureJson = """
                {
                  "live": "false",
                  "notificationItems": [
                    {
                      "NotificationRequestItem": {
                        "eventCode": "CAPTURE",
                        "merchantAccountCode": "TestMerchant",
                        "merchantReference": "ref-capture",
                        "pspReference": "psp-capture",
                        "amount": {
                          "currency": "EUR",
                          "value": 5000
                        },
                        "success": "true",
                        "additionalData": {
                          "alias": "test-alias",
                          "hmacSignature": "test-signature"
                        }
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(NotificationRequestItem.class), eq("test-hmac-key")))
                .thenReturn(true);

        var response = webhookResource.webhooks(captureJson);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void constructor_withNullHmacKey_doesNotThrow() {
        ApplicationProperty props = new ApplicationProperty();
        HMACValidator validator = new HMACValidator();

        assertDoesNotThrow(() -> new WebhookResource(props, validator));
    }

    @Test
    void webhooks_validatesHmacWithCorrectKey() throws Exception {
        when(hmacValidator.validateHMAC(any(NotificationRequestItem.class), eq("test-hmac-key")))
                .thenReturn(true);

        webhookResource.webhooks(VALID_WEBHOOK_JSON);

        verify(hmacValidator).validateHMAC(any(NotificationRequestItem.class), eq("test-hmac-key"));
    }

    @Test
    void webhooks_differentAmounts_validHmac_returnsAccepted() throws Exception {
        String smallAmountJson = """
                {
                  "live": "false",
                  "notificationItems": [
                    {
                      "NotificationRequestItem": {
                        "eventCode": "AUTHORISATION",
                        "merchantAccountCode": "TestMerchant",
                        "merchantReference": "ref-small",
                        "pspReference": "psp-small",
                        "amount": {
                          "currency": "USD",
                          "value": 100
                        },
                        "success": "true",
                        "additionalData": {
                          "alias": "test-alias",
                          "hmacSignature": "test-signature"
                        }
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(NotificationRequestItem.class), eq("test-hmac-key")))
                .thenReturn(true);

        var response = webhookResource.webhooks(smallAmountJson);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }
}
