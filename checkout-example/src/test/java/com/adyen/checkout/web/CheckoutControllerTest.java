package com.adyen.checkout.web;

import com.adyen.checkout.ApplicationProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutControllerTest {

    private ApplicationProperty applicationProperty;
    private CheckoutController checkoutController;
    private Model model;

    @BeforeEach
    void setUp() {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setClientKey("test_client_key");
        checkoutController = new CheckoutController(applicationProperty);
        model = mock(Model.class);
    }

    @Test
    void constructor_nullClientKey_doesNotThrow() {
        ApplicationProperty prop = new ApplicationProperty();
        assertDoesNotThrow(() -> new CheckoutController(prop));
    }

    @Test
    void index_returnsIndexView() {
        String viewName = checkoutController.index();
        assertEquals("index", viewName);
    }

    @Test
    void preview_returnsPreviewView_andSetsType() {
        String viewName = checkoutController.preview("card", model);
        assertEquals("preview", viewName);
        verify(model).addAttribute("type", "card");
    }

    @Test
    void preview_withDifferentType_setsCorrectType() {
        String viewName = checkoutController.preview("ideal", model);
        assertEquals("preview", viewName);
        verify(model).addAttribute("type", "ideal");
    }

    @Test
    void checkoutDropin_returnsDropinView_andSetsClientKey() {
        String viewName = checkoutController.checkoutDropin(model);
        assertEquals("checkout/dropin", viewName);
        verify(model).addAttribute("clientKey", "test_client_key");
    }

    @Test
    void checkoutCard_returnsCardView_andSetsClientKey() {
        String viewName = checkoutController.checkoutCard(model);
        assertEquals("checkout/card", viewName);
        verify(model).addAttribute("clientKey", "test_client_key");
    }

    @Test
    void checkoutGooglepay_returnsGooglepayView_andSetsClientKey() {
        String viewName = checkoutController.checkoutGooglepay(model);
        assertEquals("checkout/googlepay", viewName);
        verify(model).addAttribute("clientKey", "test_client_key");
    }

    @Test
    void checkoutiDeal_returnsIdealView_andSetsClientKey() {
        String viewName = checkoutController.checkoutiDeal(model);
        assertEquals("checkout/ideal", viewName);
        verify(model).addAttribute("clientKey", "test_client_key");
    }

    @Test
    void checkoutSepa_returnsSepaView_andSetsClientKey() {
        String viewName = checkoutController.checkoutSepa(model);
        assertEquals("checkout/sepa", viewName);
        verify(model).addAttribute("clientKey", "test_client_key");
    }

    @Test
    void checkoutKlarna_returnsKlarnaView_andSetsClientKey() {
        String viewName = checkoutController.checkoutKlarna(model);
        assertEquals("checkout/klarna", viewName);
        verify(model).addAttribute("clientKey", "test_client_key");
    }

    @Test
    void result_returnsResultView_andSetsType() {
        String viewName = checkoutController.result("success", model);
        assertEquals("result", viewName);
        verify(model).addAttribute("type", "success");
    }

    @Test
    void result_withPendingType() {
        String viewName = checkoutController.result("pending", model);
        assertEquals("result", viewName);
        verify(model).addAttribute("type", "pending");
    }

    @Test
    void result_withFailedType() {
        String viewName = checkoutController.result("failed", model);
        assertEquals("result", viewName);
        verify(model).addAttribute("type", "failed");
    }

    @Test
    void result_withErrorType() {
        String viewName = checkoutController.result("error", model);
        assertEquals("result", viewName);
        verify(model).addAttribute("type", "error");
    }
}
