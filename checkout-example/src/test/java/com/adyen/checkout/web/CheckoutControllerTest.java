package com.adyen.checkout.web;

import com.adyen.checkout.ApplicationProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutControllerTest {

    private CheckoutController controller;
    private ApplicationProperty applicationProperty;
    private Model model;

    @BeforeEach
    void setUp() {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setClientKey("test_client_key");
        controller = new CheckoutController(applicationProperty);
        model = new ExtendedModelMap();
    }

    @Test
    void index_returnsIndexView() {
        assertEquals("index", controller.index());
    }

    @Test
    void preview_returnsPreviewViewAndSetsType() {
        String view = controller.preview("card", model);
        assertEquals("preview", view);
        assertEquals("card", model.getAttribute("type"));
    }

    @Test
    void preview_withDifferentType() {
        String view = controller.preview("ideal", model);
        assertEquals("preview", view);
        assertEquals("ideal", model.getAttribute("type"));
    }

    @Test
    void checkoutDropin_returnsDropinViewAndSetsClientKey() {
        String view = controller.checkoutDropin(model);
        assertEquals("checkout/dropin", view);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void checkoutCard_returnsCardViewAndSetsClientKey() {
        String view = controller.checkoutCard(model);
        assertEquals("checkout/card", view);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void checkoutGooglepay_returnsGooglepayViewAndSetsClientKey() {
        String view = controller.checkoutGooglepay(model);
        assertEquals("checkout/googlepay", view);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void checkoutIdeal_returnsIdealViewAndSetsClientKey() {
        String view = controller.checkoutiDeal(model);
        assertEquals("checkout/ideal", view);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void checkoutSepa_returnsSepaViewAndSetsClientKey() {
        String view = controller.checkoutSepa(model);
        assertEquals("checkout/sepa", view);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void checkoutKlarna_returnsKlarnaViewAndSetsClientKey() {
        String view = controller.checkoutKlarna(model);
        assertEquals("checkout/klarna", view);
        assertEquals("test_client_key", model.getAttribute("clientKey"));
    }

    @Test
    void result_returnsResultViewAndSetsType() {
        String view = controller.result("success", model);
        assertEquals("result", view);
        assertEquals("success", model.getAttribute("type"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"success", "pending", "failed", "error"})
    void result_withDifferentTypes(String type) {
        String view = controller.result(type, model);
        assertEquals("result", view);
        assertEquals(type, model.getAttribute("type"));
    }

    @Test
    void constructor_doesNotThrowWhenClientKeyNull() {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setClientKey(null);
        assertDoesNotThrow(() -> new CheckoutController(prop));
    }

    @Test
    void checkoutEndpoints_clientKeyNullSetsNullInModel() {
        ApplicationProperty prop = new ApplicationProperty();
        prop.setClientKey(null);
        CheckoutController ctrl = new CheckoutController(prop);
        Model m = new ExtendedModelMap();

        ctrl.checkoutDropin(m);
        assertNull(m.getAttribute("clientKey"));
    }
}
