package com.adyen.giving;

import com.adyen.util.HMACValidator;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void layoutDialectBeanCreated() {
        Config config = new Config();
        LayoutDialect dialect = config.layoutDialect();
        assertNotNull(dialect);
    }

    @Test
    void hmacValidatorBeanCreated() {
        Config config = new Config();
        HMACValidator validator = config.getHmacValidator();
        assertNotNull(validator);
    }
}
