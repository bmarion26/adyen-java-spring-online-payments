package com.adyen.checkout.web;

import com.adyen.checkout.ApplicationProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionControllerTest {

    private SubscriptionController controller;
    private ApplicationProperty applicationProperty;

    @BeforeEach
    void setUp() {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setClientKey("test_client_key");
        controller = new SubscriptionController(applicationProperty);
    }

    @Test
    void constructorWithNullClientKey_doesNotThrow() {
        ApplicationProperty prop = new ApplicationProperty();
        assertDoesNotThrow(() -> new SubscriptionController(prop));
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
    void subscription_returnsSubscriptionViewWithAttributes() {
        Model model = new ConcurrentModel();
        String result = controller.subscription("card", model);
        assertEquals("subscription", result);
        assertEquals("card", model.getAttribute("type"));
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void result_returnsResultViewWithType() {
        Model model = new ConcurrentModel();
        String result = controller.result("success", model);
        assertEquals("result", result);
        assertEquals("success", model.getAttribute("type"));
    }

    @Test
    void result_handlesErrorType() {
        Model model = new ConcurrentModel();
        String result = controller.result("error", model);
        assertEquals("result", result);
        assertEquals("error", model.getAttribute("type"));
    }

    @Test
    void redirect_returnsRedirectViewWithClientKey() {
        Model model = new ConcurrentModel();
        String result = controller.redirect(model);
        assertEquals("redirect", result);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }
}
