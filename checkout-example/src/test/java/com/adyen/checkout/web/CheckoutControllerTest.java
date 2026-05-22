package com.adyen.checkout.web;

import com.adyen.checkout.ApplicationProperty;
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
        String result = controller.preview("dropin", model);
        assertEquals("preview", result);
        assertEquals("dropin", model.getAttribute("type"));
    }

    @Test
    void checkoutDropin_returnsDropinViewWithClientKey() {
        Model model = new ConcurrentModel();
        String result = controller.checkoutDropin(model);
        assertEquals("checkout/dropin", result);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void checkoutCard_returnsCardViewWithClientKey() {
        Model model = new ConcurrentModel();
        String result = controller.checkoutCard(model);
        assertEquals("checkout/card", result);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void checkoutGooglepay_returnsGooglepayViewWithClientKey() {
        Model model = new ConcurrentModel();
        String result = controller.checkoutGooglepay(model);
        assertEquals("checkout/googlepay", result);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void checkoutiDeal_returnsIdealViewWithClientKey() {
        Model model = new ConcurrentModel();
        String result = controller.checkoutiDeal(model);
        assertEquals("checkout/ideal", result);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void checkoutSepa_returnsSepaViewWithClientKey() {
        Model model = new ConcurrentModel();
        String result = controller.checkoutSepa(model);
        assertEquals("checkout/sepa", result);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void checkoutKlarna_returnsKlarnaViewWithClientKey() {
        Model model = new ConcurrentModel();
        String result = controller.checkoutKlarna(model);
        assertEquals("checkout/klarna", result);
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
    void result_handlesFailedType() {
        Model model = new ConcurrentModel();
        String result = controller.result("failed", model);
        assertEquals("result", result);
        assertEquals("failed", model.getAttribute("type"));
    }

    @Test
    void result_handlesPendingType() {
        Model model = new ConcurrentModel();
        String result = controller.result("pending", model);
        assertEquals("result", result);
        assertEquals("pending", model.getAttribute("type"));
    }

    @Test
    void result_handlesErrorType() {
        Model model = new ConcurrentModel();
        String result = controller.result("error", model);
        assertEquals("result", result);
        assertEquals("error", model.getAttribute("type"));
    }
}
