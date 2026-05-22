package com.adyen.paybylink;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationPropertyTest {

    @Test
    void gettersAndSetters() {
        ApplicationProperty prop = new ApplicationProperty();

        prop.setServerPort(8080);
        assertEquals(8080, prop.getServerPort());

        prop.setApiKey("testApiKey");
        assertEquals("testApiKey", prop.getApiKey());

        prop.setMerchantAccount("testMerchant");
        assertEquals("testMerchant", prop.getMerchantAccount());

        prop.setHmacKey("testHmacKey");
        assertEquals("testHmacKey", prop.getHmacKey());
    }

    @Test
    void defaultValues() {
        ApplicationProperty prop = new ApplicationProperty();
        assertNull(prop.getApiKey());
        assertNull(prop.getMerchantAccount());
        assertNull(prop.getHmacKey());
    }
}
