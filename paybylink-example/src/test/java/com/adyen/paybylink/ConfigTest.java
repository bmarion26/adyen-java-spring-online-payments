package com.adyen.paybylink;

import com.adyen.util.HMACValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void hmacValidatorBeanCreated() {
        Config config = new Config();
        HMACValidator validator = config.getHmacValidator();
        assertNotNull(validator);
    }
}
