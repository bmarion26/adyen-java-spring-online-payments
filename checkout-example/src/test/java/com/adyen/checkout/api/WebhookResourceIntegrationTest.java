package com.adyen.checkout.api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebhookResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setUpEnvironment() {
        System.setProperty("ADYEN_API_KEY", "testKey");
        System.setProperty("ADYEN_MERCHANT_ACCOUNT", "testAccount");
        System.setProperty("ADYEN_CLIENT_KEY", "testKey");
        System.setProperty("ADYEN_HMAC_KEY", "44782DEF547AAA06C910C43D1F7508C13573E96ADA3A520E13B7027BFA6B2F4E");
    }

    @Test
    void webhookEndpoint_invalidHmac_throwsRuntimeException() {
        String json = """
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
                          "hmacSignature": "invalid-signature"
                        }
                      }
                    }
                  ]
                }
                """;

        Exception ex = assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/webhooks/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)));
        assertTrue(ex.getCause() instanceof RuntimeException);
    }

    @Test
    void webhookEndpoint_emptyPayload_throwsException() {
        String json = """
                {
                  "live": "false",
                  "notificationItems": []
                }
                """;

        Exception ex = assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/webhooks/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)));
        assertTrue(ex.getCause().getMessage().contains("empty payload"));
    }

    @Test
    void webhookEndpoint_malformedJson_throwsException() {
        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/webhooks/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-valid-json")));
    }

    @Test
    void webhookEndpoint_getNotAllowed() throws Exception {
        mockMvc.perform(get("/api/webhooks/notifications"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void webhookEndpoint_emptyBody_returns4xx() throws Exception {
        mockMvc.perform(post("/api/webhooks/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void sessionsEndpoint_withInvalidApiKey_throwsApiException() {
        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/sessions")
                        .header("Host", "localhost:8080")));
    }

    @Test
    void handleShopperRedirect_withoutParams_throwsException() {
        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/handleShopperRedirect")));
    }

    @Test
    void viewEndpoints_returnOk() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/preview").param("type", "card"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/result/success"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/checkout/dropin"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/checkout/card"))
                .andExpect(status().isOk());
    }

    @Test
    void nonExistentEndpoint_returns404() throws Exception {
        mockMvc.perform(get("/api/nonexistent"))
                .andExpect(status().isNotFound());
    }
}
