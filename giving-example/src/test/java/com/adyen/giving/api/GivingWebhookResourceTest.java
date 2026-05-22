package com.adyen.giving.api;

import com.adyen.giving.ApplicationProperty;
import com.adyen.util.HMACValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GivingWebhookResourceTest {

    private ApplicationProperty applicationProperty;
    private HMACValidator hmacValidator;
    private GivingWebhookResource givingWebhookResource;

    @BeforeEach
    void setUp() {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setHmacKey("44782DEF547AAA06C910C43D1F7508C13573E96ADA3A520E13B7027BFA6B2F4E");
        hmacValidator = new HMACValidator();
        givingWebhookResource = new GivingWebhookResource(applicationProperty, hmacValidator);
    }

    @Test
    void constructorWithNullHmacKey_doesNotThrow() {
        ApplicationProperty prop = new ApplicationProperty();
        assertDoesNotThrow(() -> new GivingWebhookResource(prop, new HMACValidator()));
    }

    @Test
    void webhooks_validNotification_returns202() throws IOException {
        String json = """
                {
                  "live": "false",
                  "notificationItems": [
                    {
                      "NotificationRequestItem": {
                        "eventCode": "DONATION",
                        "merchantAccountCode": "TestMerchant",
                        "pspReference": "psp123",
                        "amount": {"currency": "EUR", "value": 500},
                        "success": "true"
                      }
                    }
                  ]
                }
                """;

        var response = givingWebhookResource.webhooks(json);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void webhooks_emptyPayload_returns202() throws IOException {
        String emptyJson = "{\"live\":\"false\",\"notificationItems\":[]}";
        var response = givingWebhookResource.webhooks(emptyJson);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void webhooks_failedDonation_returns202() throws IOException {
        String json = """
                {
                  "live": "false",
                  "notificationItems": [
                    {
                      "NotificationRequestItem": {
                        "eventCode": "DONATION",
                        "merchantAccountCode": "TestMerchant",
                        "pspReference": "psp456",
                        "amount": {"currency": "EUR", "value": 500},
                        "success": "false"
                      }
                    }
                  ]
                }
                """;

        var response = givingWebhookResource.webhooks(json);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }
}
