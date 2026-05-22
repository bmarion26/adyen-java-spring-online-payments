package com.adyen.ipp.web;

import com.adyen.ipp.ApplicationProperty;
import com.adyen.ipp.model.*;
import com.adyen.ipp.service.PosTransactionStatusService;
import com.adyen.ipp.service.TableService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InPersonPaymentsWebControllerTest {

    private InPersonPaymentsWebController controller;
    private ApplicationProperty applicationProperty;
    private TableService tableService;
    private PosTransactionStatusService posTransactionStatusService;

    @BeforeEach
    void setUp() throws Exception {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setPoiId("V400m-123456789");
        applicationProperty.setSaleId("TestSale");
        controller = new InPersonPaymentsWebController(applicationProperty);

        tableService = new TableService();
        posTransactionStatusService = mock(PosTransactionStatusService.class);

        Field tsField = InPersonPaymentsWebController.class.getDeclaredField("tableService");
        tsField.setAccessible(true);
        tsField.set(controller, tableService);

        Field ptsField = InPersonPaymentsWebController.class.getDeclaredField("posTransactionStatusService");
        ptsField.setAccessible(true);
        ptsField.set(controller, posTransactionStatusService);
    }

    @Test
    void index_returnsIndexView() {
        assertEquals("index", controller.index());
    }

    @Test
    void cashregister_returnsCashregisterViewWithAttributes() {
        Model model = new ConcurrentModel();
        String result = controller.cashregister(model);
        assertEquals("cashregister", result);
        assertEquals("V400m-123456789", model.getAttribute("poiId"));
        assertEquals("TestSale", model.getAttribute("saleId"));
        assertNotNull(model.getAttribute("tables"));
    }

    @Test
    void result_withTypeAndRefusalReason() {
        Model model = new ConcurrentModel();
        String result = controller.result("failure", "Refused", model);
        assertEquals("result", result);
        assertEquals("failure", model.getAttribute("type"));
        assertEquals("Refused", model.getAttribute("refusalReason"));
    }

    @Test
    void result_withTypeOnly() {
        Model model = new ConcurrentModel();
        String result = controller.result("success", model);
        assertEquals("result", result);
        assertEquals("success", model.getAttribute("type"));
    }

    @Test
    void transactionstatus_tableNotFound_returnsErrorMessage() throws Exception {
        Model model = new ConcurrentModel();
        String result = controller.transactionstatus("NonexistentTable", model);
        assertEquals("transactionstatus", result);
        assertEquals("table not found", model.getAttribute("errorMessage"));
    }

    @Test
    void transactionstatus_tableFoundButNoServiceId_returnsErrorMessage() throws Exception {
        Model model = new ConcurrentModel();
        String result = controller.transactionstatus("Table 1", model);
        assertEquals("transactionstatus", result);
        assertEquals("table not found", model.getAttribute("errorMessage"));
    }
}
