package com.adyen.giftcard;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GiftCardIntegrationApplicationTests {

    @BeforeAll
    public static void onceExecutedBeforeAll() {
        System.setProperty("ADYEN_API_KEY", "testKey");
        System.setProperty("ADYEN_MERCHANT_ACCOUNT", "testAccount");
        System.setProperty("ADYEN_CLIENT_KEY", "testKey");
        System.setProperty("ADYEN_HMAC_KEY", "44782DEF547AAA06C910C43D1F7508C13573E96ADA3A520E13B7027BFA6B2F4E");
    }

    @AfterAll
    public static void onceExecutedAfterAll() {
        System.clearProperty("ADYEN_API_KEY");
        System.clearProperty("ADYEN_MERCHANT_ACCOUNT");
        System.clearProperty("ADYEN_CLIENT_KEY");
        System.clearProperty("ADYEN_HMAC_KEY");
    }

    @Test
    void contextLoads() {
    }
}
