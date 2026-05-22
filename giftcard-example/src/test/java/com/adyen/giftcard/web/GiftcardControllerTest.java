package com.adyen.giftcard.web;

import com.adyen.giftcard.ApplicationProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;

class GiftcardControllerTest {

    private GiftcardController controller;
    private ApplicationProperty applicationProperty;

    @BeforeEach
    void setUp() {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setClientKey("test_client_key");
        controller = new GiftcardController(applicationProperty);
    }

    @Test
    void constructorWithNullClientKey_doesNotThrow() {
        ApplicationProperty prop = new ApplicationProperty();
        assertDoesNotThrow(() -> new GiftcardController(prop));
    }

    @Test
    void index_returnsIndexView() {
        assertEquals("index", controller.index());
    }

    @Test
    void preview_returnsPreviewViewWithType() {
        Model model = new ConcurrentModel();
        String result = controller.preview("dropin", model);
        assertEquals("preview", result);
        assertEquals("dropin", model.getAttribute("type"));
    }

    @Test
    void checkout_returnsTypeViewWithAttributes() {
        Model model = new ConcurrentModel();
        String result = controller.checkout("dropin", model);
        assertEquals("dropin", result);
        assertEquals("dropin", model.getAttribute("type"));
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void checkout_giftcardType() {
        Model model = new ConcurrentModel();
        String result = controller.checkout("giftcard", model);
        assertEquals("giftcard", result);
        assertEquals("giftcard", model.getAttribute("type"));
    }

    @Test
    void result_returnsResultViewWithType() {
        Model model = new ConcurrentModel();
        String result = controller.result("success", model);
        assertEquals("result", result);
        assertEquals("success", model.getAttribute("type"));
    }

    @Test
    void result_handlesFailedType() {
        Model model = new ConcurrentModel();
        String result = controller.result("failed", model);
        assertEquals("result", result);
        assertEquals("failed", model.getAttribute("type"));
    }

    @Test
    void redirect_returnsRedirectViewWithClientKey() {
        Model model = new ConcurrentModel();
        String result = controller.redirect(model);
        assertEquals("redirect", result);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }
}
