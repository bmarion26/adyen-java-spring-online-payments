package com.adyen.checkout.web;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.util.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;

class AdminControllerTest {

    @Test
    void constructorWithNullApiKey_throwsException() {
        ApplicationProperty prop = new ApplicationProperty();
        assertThrows(RuntimeException.class, () -> new AdminController(prop));
    }

    @Test
    void constructorWithApiKey_doesNotThrow() {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setApiKey("testApiKey");
        prop.setMerchantAccount("testMerchant");
        assertDoesNotThrow(() -> new AdminController(prop));
    }
}
