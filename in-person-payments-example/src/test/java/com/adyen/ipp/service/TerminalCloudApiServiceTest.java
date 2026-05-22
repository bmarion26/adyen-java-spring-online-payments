package com.adyen.ipp.service;

import com.adyen.ipp.ApplicationProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalCloudApiServiceTest {

    @Test
    void constructorWithNullApiKey_throwsRuntimeException() {
        ApplicationProperty prop = new ApplicationProperty();
        assertThrows(RuntimeException.class, () -> new TerminalCloudApiService(prop));
    }

    @Test
    void constructorWithApiKey_createsService() {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setApiKey("testApiKey");
        TerminalCloudApiService service = new TerminalCloudApiService(prop);
        assertNotNull(service.getTerminalCloudApi());
    }

    @Test
    void constructorWithCustomEndpoint_createsService() {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setApiKey("testApiKey");
        prop.setAdyenTerminalApiCloudEndpoint("https://custom-endpoint.example.com");
        TerminalCloudApiService service = new TerminalCloudApiService(prop);
        assertNotNull(service.getTerminalCloudApi());
    }
}
