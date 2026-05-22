package com.adyen.checkout.configurations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationConfigurationTest {

    @Test
    void gettersAndSetters() {
        ApplicationConfiguration config = new ApplicationConfiguration();

        config.setServerPort(8080);
        assertEquals(8080, config.getServerPort());

        config.setAdyenApiKey("testApiKey");
        assertEquals("testApiKey", config.getAdyenApiKey());

        config.setAdyenMerchantAccount("testMerchant");
        assertEquals("testMerchant", config.getAdyenMerchantAccount());

        config.setAdyenClientKey("testClientKey");
        assertEquals("testClientKey", config.getAdyenClientKey());

        config.setAdyenHmacKey("testHmacKey");
        assertEquals("testHmacKey", config.getAdyenHmacKey());
    }

    @Test
    void defaultValues() {
        ApplicationConfiguration config = new ApplicationConfiguration();
        assertNull(config.getAdyenApiKey());
        assertNull(config.getAdyenMerchantAccount());
        assertNull(config.getAdyenClientKey());
        assertNull(config.getAdyenHmacKey());
    }
}
