package com.adyen.giving.web;

import com.adyen.giving.ApplicationProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutControllerTest {

    private CheckoutController controller;
    private ApplicationProperty applicationProperty;

    @BeforeEach
    void setUp() {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setClientKey("test_client_key");
        controller = new CheckoutController(applicationProperty);
    }

    @Test
    void constructorWithNullClientKey_doesNotThrow() {
        ApplicationProperty prop = new ApplicationProperty();
        assertDoesNotThrow(() -> new CheckoutController(prop));
    }

    @Test
    void index_returnsIndexView() {
        assertEquals("index", controller.index());
    }

    @Test
    void preview_returnsPreviewViewWithType() {
        Model model = new ConcurrentModel();
        String result = controller.preview("card", model);
        assertEquals("preview", result);
        assertEquals("card", model.getAttribute("type"));
    }

    @Test
    void checkout_returnsCheckoutViewWithAttributes() {
        Model model = new ConcurrentModel();
        String result = controller.checkout("card", model);
        assertEquals("checkout", result);
        assertEquals("card", model.getAttribute("type"));
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void result_returnsResultViewWithAttributes() {
        Model model = new ConcurrentModel();
        String result = controller.result("success", model);
        assertEquals("result", result);
        assertEquals("success", model.getAttribute("type"));
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void result_handlesErrorType() {
        Model model = new ConcurrentModel();
        String result = controller.result("error", model);
        assertEquals("result", result);
        assertEquals("error", model.getAttribute("type"));
    }

    @Test
    void result_handlesPendingType() {
        Model model = new ConcurrentModel();
        String result = controller.result("pending", model);
        assertEquals("result", result);
        assertEquals("pending", model.getAttribute("type"));
    }
}
