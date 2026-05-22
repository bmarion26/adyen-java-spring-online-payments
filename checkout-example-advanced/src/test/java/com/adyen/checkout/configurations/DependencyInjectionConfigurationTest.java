package com.adyen.checkout.configurations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DependencyInjectionConfigurationTest {

    @Test
    void constructorWithNullApiKey_doesNotThrow() {
        ApplicationConfiguration config = new ApplicationConfiguration();
        assertDoesNotThrow(() -> new DependencyInjectionConfiguration(config));
    }

    @Test
    void constructorWithValidConfig_doesNotThrow() {
        ApplicationConfiguration config = new ApplicationConfiguration();
        config.setAdyenApiKey("testKey");
        config.setAdyenMerchantAccount("testMerchant");
        config.setAdyenClientKey("testClientKey");
        assertDoesNotThrow(() -> new DependencyInjectionConfiguration(config));
    }

    @Test
    void hmacValidator_returnsNonNull() {
        ApplicationConfiguration config = new ApplicationConfiguration();
        config.setAdyenApiKey("testKey");
        DependencyInjectionConfiguration diConfig = new DependencyInjectionConfiguration(config);
        assertNotNull(diConfig.hmacValidator());
    }

    @Test
    void layoutDialect_returnsNonNull() {
        ApplicationConfiguration config = new ApplicationConfiguration();
        config.setAdyenApiKey("testKey");
        DependencyInjectionConfiguration diConfig = new DependencyInjectionConfiguration(config);
        assertNotNull(diConfig.layoutDialect());
    }

    @Test
    void client_returnsNonNull() {
        ApplicationConfiguration config = new ApplicationConfiguration();
        config.setAdyenApiKey("testKey");
        DependencyInjectionConfiguration diConfig = new DependencyInjectionConfiguration(config);
        assertNotNull(diConfig.client());
    }

    @Test
    void paymentsApi_returnsNonNull() {
        ApplicationConfiguration config = new ApplicationConfiguration();
        config.setAdyenApiKey("testKey");
        DependencyInjectionConfiguration diConfig = new DependencyInjectionConfiguration(config);
        assertNotNull(diConfig.paymentsApi());
    }
}
