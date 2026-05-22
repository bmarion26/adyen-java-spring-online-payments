package com.adyen.paybylink.api;

import com.adyen.paybylink.ApplicationProperty;
import com.adyen.paybylink.service.PaymentLinkService;
import com.adyen.util.HMACValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WebhookControllerTest {

    private ApplicationProperty applicationProperty;
    private HMACValidator hmacValidator;
    private WebhookController webhookController;
    private PaymentLinkService paymentLinkService;

    @BeforeEach
    void setUp() throws Exception {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setHmacKey("44782DEF547AAA06C910C43D1F7508C13573E96ADA3A520E13B7027BFA6B2F4E");
        hmacValidator = mock(HMACValidator.class);
        webhookController = new WebhookController(applicationProperty, hmacValidator);

        paymentLinkService = mock(PaymentLinkService.class);
        Field plsField = WebhookController.class.getDeclaredField("paymentLinkService");
        plsField.setAccessible(true);
        plsField.set(webhookController, paymentLinkService);
    }

    @Test
    void constructorWithNullHmacKey_doesNotThrow() {
        ApplicationProperty prop = new ApplicationProperty();
        assertDoesNotThrow(() -> new WebhookController(prop, new HMACValidator()));
    }

    @Test
    void webhooks_validHmac_withPaymentLinkId_returns202() throws Exception {
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
                        "additionalData": {"paymentLinkId": "PL123456"}
                      }
                    }
                  ]
                }
                """;

        when(hmacValidator.validateHMAC(any(), any())).thenReturn(true);

        var response = webhookController.webhooks(json);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(paymentLinkService).updateLink("PL123456");
    }

    @Test
    void webhooks_validHmac_withoutPaymentLinkId_returns202() throws Exception {
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

        when(hmacValidator.validateHMAC(any(), any())).thenReturn(true);

        var response = webhookController.webhooks(json);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(paymentLinkService, never()).updateLink(any());
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

        assertThrows(RuntimeException.class, () -> webhookController.webhooks(json));
    }

    @Test
    void webhooks_emptyPayload_throwsException() {
        String emptyJson = "{\"live\":\"false\",\"notificationItems\":[]}";
        assertThrows(Exception.class, () -> webhookController.webhooks(emptyJson));
    }
}
