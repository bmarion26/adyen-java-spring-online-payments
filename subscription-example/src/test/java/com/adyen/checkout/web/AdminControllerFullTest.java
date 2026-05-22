package com.adyen.checkout.web;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.util.Storage;
import com.adyen.model.checkout.PaymentResponse;
import com.adyen.service.RecurringApi;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminControllerFullTest {

    private AdminController adminController;
    private PaymentsApi paymentsApi;
    private RecurringApi recurringApi;
    private ApplicationProperty applicationProperty;

    @BeforeEach
    void setUp() throws Exception {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setApiKey("testApiKey");
        applicationProperty.setMerchantAccount("TestMerchant");

        adminController = new AdminController(applicationProperty);

        paymentsApi = mock(PaymentsApi.class);
        recurringApi = mock(RecurringApi.class);

        Field paymentsField = AdminController.class.getDeclaredField("paymentsApi");
        paymentsField.setAccessible(true);
        paymentsField.set(adminController, paymentsApi);

        Field recurringField = AdminController.class.getDeclaredField("recurring");
        recurringField.setAccessible(true);
        recurringField.set(adminController, recurringApi);
    }

    @Test
    void index_addsTokensToModel() {
        Model model = new ConcurrentModel();
        String result = adminController.index(model);
        assertEquals("admin/index", result);
        assertNotNull(model.getAttribute("tokens"));
    }

    @Test
    void payment_authorised_returnsSuccess() throws Exception {
        PaymentResponse mockResponse = new PaymentResponse();
        mockResponse.setResultCode(PaymentResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsApi.payments(any())).thenReturn(mockResponse);

        Model model = new ConcurrentModel();
        String result = adminController.payment("ref123", model);

        assertEquals("admin/makepayment", result);
        assertEquals("success", model.getAttribute("result"));
        assertEquals("ref123", model.getAttribute("recurringDetailReference"));
    }

    @Test
    void payment_refused_returnsError() throws Exception {
        PaymentResponse mockResponse = new PaymentResponse();
        mockResponse.setResultCode(PaymentResponse.ResultCodeEnum.REFUSED);
        when(paymentsApi.payments(any())).thenReturn(mockResponse);

        Model model = new ConcurrentModel();
        String result = adminController.payment("ref123", model);

        assertEquals("admin/makepayment", result);
        assertEquals("error", model.getAttribute("result"));
    }

    @Test
    void payment_apiException_returnsError() throws Exception {
        when(paymentsApi.payments(any())).thenThrow(new ApiException("API error", 401));

        Model model = new ConcurrentModel();
        String result = adminController.payment("ref123", model);

        assertEquals("admin/makepayment", result);
        assertEquals("error", model.getAttribute("result"));
    }

    @Test
    void payment_unexpectedException_returnsError() throws Exception {
        when(paymentsApi.payments(any())).thenThrow(new RuntimeException("Unexpected"));

        Model model = new ConcurrentModel();
        String result = adminController.payment("ref123", model);

        assertEquals("admin/makepayment", result);
        assertEquals("error", model.getAttribute("result"));
    }

    @Test
    void disable_success_returnsSuccess() throws Exception {
        when(recurringApi.disable(any())).thenReturn(null);

        // Add a token to storage first
        Storage.add("ref456", "card", Storage.SHOPPER_REFERENCE);

        Model model = new ConcurrentModel();
        String result = adminController.disable("ref456", model);

        assertEquals("admin/disable", result);
        assertEquals("success", model.getAttribute("result"));
        assertEquals("ref456", model.getAttribute("recurringDetailReference"));
    }

    @Test
    void disable_exception_returnsError() throws Exception {
        when(recurringApi.disable(any())).thenThrow(new RuntimeException("Failed"));

        Model model = new ConcurrentModel();
        String result = adminController.disable("ref789", model);

        assertEquals("admin/disable", result);
        assertEquals("error", model.getAttribute("result"));
    }
}
