package com.adyen.ipp;

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

        prop.setHmacKey("testHmacKey");
        assertEquals("testHmacKey", prop.getHmacKey());

        prop.setPoiId("testPoiId");
        assertEquals("testPoiId", prop.getPoiId());

        prop.setSaleId("testSaleId");
        assertEquals("testSaleId", prop.getSaleId());

        prop.setAdyenTerminalApiCloudEndpoint("https://terminal.adyen.com");
        assertEquals("https://terminal.adyen.com", prop.getAdyenTerminalApiCloudEndpoint());
    }

    @Test
    void defaultValues() {
        ApplicationProperty prop = new ApplicationProperty();
        assertNull(prop.getApiKey());
        assertNull(prop.getHmacKey());
        assertNull(prop.getPoiId());
        assertNull(prop.getAdyenTerminalApiCloudEndpoint());
    }
}
