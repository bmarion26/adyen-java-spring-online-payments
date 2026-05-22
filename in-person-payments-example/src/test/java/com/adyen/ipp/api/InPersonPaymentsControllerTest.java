package com.adyen.ipp.api;

import com.adyen.ipp.ApplicationProperty;
import com.adyen.ipp.model.*;
import com.adyen.ipp.request.CreatePaymentRequest;
import com.adyen.ipp.request.CreateReversalRequest;
import com.adyen.ipp.response.CreatePaymentResponse;
import com.adyen.ipp.response.CreateReversalResponse;
import com.adyen.ipp.service.*;
import com.adyen.model.nexo.*;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InPersonPaymentsControllerTest {

    private InPersonPaymentsController controller;
    private ApplicationProperty applicationProperty;
    private PosPaymentService posPaymentService;
    private PosReversalService posReversalService;
    private PosAbortService posAbortService;
    private PosTransactionStatusService posTransactionStatusService;
    private TableService tableService;

    @BeforeEach
    void setUp() throws Exception {
        applicationProperty = new ApplicationProperty();
        applicationProperty.setPoiId("V400m-123456789");
        applicationProperty.setSaleId("TestSale");
        applicationProperty.setApiKey("testKey");

        controller = new InPersonPaymentsController(applicationProperty);

        posPaymentService = mock(PosPaymentService.class);
        posReversalService = mock(PosReversalService.class);
        posAbortService = mock(PosAbortService.class);
        posTransactionStatusService = mock(PosTransactionStatusService.class);
        tableService = new TableService();

        setField("posPaymentService", posPaymentService);
        setField("posReversalService", posReversalService);
        setField("posAbortService", posAbortService);
        setField("posTransactionStatusService", posTransactionStatusService);
        setField("tableService", tableService);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = InPersonPaymentsController.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private TerminalAPIResponse createSuccessPaymentResponse() throws Exception {
        var xmlFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar timestamp = xmlFactory.newXMLGregorianCalendar(new GregorianCalendar());

        var poiTransId = new TransactionIdentification();
        poiTransId.setTransactionID("poi-tx-id");
        poiTransId.setTimeStamp(timestamp);

        var saleTransId = new TransactionIdentification();
        saleTransId.setTransactionID("sale-tx-id");
        saleTransId.setTimeStamp(timestamp);

        var poiData = new POIData();
        poiData.setPOITransactionID(poiTransId);

        var saleData = new SaleData();
        saleData.setSaleTransactionID(saleTransId);

        var responseObj = new Response();
        responseObj.setResult(ResultType.SUCCESS);

        var paymentResponse = new PaymentResponse();
        paymentResponse.setResponse(responseObj);
        paymentResponse.setPOIData(poiData);
        paymentResponse.setSaleData(saleData);

        var saleToPOIResponse = new SaleToPOIResponse();
        saleToPOIResponse.setPaymentResponse(paymentResponse);

        var terminalResponse = new TerminalAPIResponse();
        terminalResponse.setSaleToPOIResponse(saleToPOIResponse);
        return terminalResponse;
    }

    private TerminalAPIResponse createFailurePaymentResponse() throws Exception {
        var xmlFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar timestamp = xmlFactory.newXMLGregorianCalendar(new GregorianCalendar());

        var poiTransId = new TransactionIdentification();
        poiTransId.setTransactionID("poi-tx-id");
        poiTransId.setTimeStamp(timestamp);

        var saleTransId = new TransactionIdentification();
        saleTransId.setTransactionID("sale-tx-id");
        saleTransId.setTimeStamp(timestamp);

        var poiData = new POIData();
        poiData.setPOITransactionID(poiTransId);

        var saleData = new SaleData();
        saleData.setSaleTransactionID(saleTransId);

        var responseObj = new Response();
        responseObj.setResult(ResultType.FAILURE);
        responseObj.setErrorCondition(ErrorConditionType.REFUSAL);

        var paymentResponse = new PaymentResponse();
        paymentResponse.setResponse(responseObj);
        paymentResponse.setPOIData(poiData);
        paymentResponse.setSaleData(saleData);

        var saleToPOIResponse = new SaleToPOIResponse();
        saleToPOIResponse.setPaymentResponse(paymentResponse);

        var terminalResponse = new TerminalAPIResponse();
        terminalResponse.setSaleToPOIResponse(saleToPOIResponse);
        return terminalResponse;
    }

    @Test
    void createPayment_tableNotFound_returns404() throws IOException, ApiException {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setTableName("NonexistentTable");
        request.setAmount(BigDecimal.TEN);
        request.setCurrency("EUR");

        ResponseEntity<CreatePaymentResponse> response = controller.createPayment(request);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("failure", response.getBody().getResult());
        assertTrue(response.getBody().getRefusalReason().contains("not found"));
    }

    @Test
    void createPayment_success_returns200() throws Exception {
        when(posPaymentService.sendPaymentRequest(any(), any(), any(), any(), any()))
                .thenReturn(createSuccessPaymentResponse());

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setTableName("Table 1");
        request.setAmount(BigDecimal.TEN);
        request.setCurrency("EUR");

        ResponseEntity<CreatePaymentResponse> response = controller.createPayment(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().getResult());
    }

    @Test
    void createPayment_failure_returns200WithFailure() throws Exception {
        when(posPaymentService.sendPaymentRequest(any(), any(), any(), any(), any()))
                .thenReturn(createFailurePaymentResponse());

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setTableName("Table 1");
        request.setAmount(BigDecimal.TEN);
        request.setCurrency("EUR");

        ResponseEntity<CreatePaymentResponse> response = controller.createPayment(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("failure", response.getBody().getResult());
    }

    @Test
    void createPayment_nullResponse_returnsBadRequest() throws Exception {
        when(posPaymentService.sendPaymentRequest(any(), any(), any(), any(), any()))
                .thenReturn(null);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setTableName("Table 1");
        request.setAmount(BigDecimal.TEN);
        request.setCurrency("EUR");

        ResponseEntity<CreatePaymentResponse> response = controller.createPayment(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("failure", response.getBody().getResult());
    }

    @Test
    void createPayment_ioException_throwsException() throws Exception {
        when(posPaymentService.sendPaymentRequest(any(), any(), any(), any(), any()))
                .thenThrow(new IOException("Connection error"));

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setTableName("Table 1");
        request.setAmount(BigDecimal.TEN);
        request.setCurrency("EUR");

        assertThrows(IOException.class, () -> controller.createPayment(request));
    }

    @Test
    void createReversal_tableNotFound_returns404() throws IOException, ApiException {
        CreateReversalRequest request = new CreateReversalRequest();
        request.setTableName("NonexistentTable");

        ResponseEntity<CreateReversalResponse> response = controller.createReversal(request);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("failure", response.getBody().getResult());
    }

    @Test
    void createReversal_success_returns200() throws Exception {
        // First do a successful payment so the table has transaction details
        when(posPaymentService.sendPaymentRequest(any(), any(), any(), any(), any()))
                .thenReturn(createSuccessPaymentResponse());

        CreatePaymentRequest payReq = new CreatePaymentRequest();
        payReq.setTableName("Table 2");
        payReq.setAmount(BigDecimal.TEN);
        payReq.setCurrency("EUR");
        controller.createPayment(payReq);

        // Now set up reversal response
        var responseObj = new Response();
        responseObj.setResult(ResultType.SUCCESS);

        var reversalResponse = new ReversalResponse();
        reversalResponse.setResponse(responseObj);

        var saleToPOIResponse = new SaleToPOIResponse();
        saleToPOIResponse.setReversalResponse(reversalResponse);

        var terminalResponse = new TerminalAPIResponse();
        terminalResponse.setSaleToPOIResponse(saleToPOIResponse);

        when(posReversalService.sendReversalRequest(any(), any(), any(), any(), any()))
                .thenReturn(terminalResponse);

        CreateReversalRequest revReq = new CreateReversalRequest();
        revReq.setTableName("Table 2");

        ResponseEntity<CreateReversalResponse> response = controller.createReversal(revReq);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().getResult());
    }

    @Test
    void createReversal_failure_returns200WithFailure() throws Exception {
        // First do a payment
        when(posPaymentService.sendPaymentRequest(any(), any(), any(), any(), any()))
                .thenReturn(createSuccessPaymentResponse());

        CreatePaymentRequest payReq = new CreatePaymentRequest();
        payReq.setTableName("Table 3");
        payReq.setAmount(BigDecimal.TEN);
        payReq.setCurrency("EUR");
        controller.createPayment(payReq);

        // Set up failure reversal
        var responseObj = new Response();
        responseObj.setResult(ResultType.FAILURE);
        responseObj.setAdditionalResponse("Refund%20Failed");

        var reversalResponse = new ReversalResponse();
        reversalResponse.setResponse(responseObj);

        var saleToPOIResponse = new SaleToPOIResponse();
        saleToPOIResponse.setReversalResponse(reversalResponse);

        var terminalResponse = new TerminalAPIResponse();
        terminalResponse.setSaleToPOIResponse(saleToPOIResponse);

        when(posReversalService.sendReversalRequest(any(), any(), any(), any(), any()))
                .thenReturn(terminalResponse);

        CreateReversalRequest revReq = new CreateReversalRequest();
        revReq.setTableName("Table 3");

        ResponseEntity<CreateReversalResponse> response = controller.createReversal(revReq);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("failure", response.getBody().getResult());
    }

    @Test
    void createReversal_nullResponse_returnsBadRequest() throws Exception {
        // First do a payment
        when(posPaymentService.sendPaymentRequest(any(), any(), any(), any(), any()))
                .thenReturn(createSuccessPaymentResponse());

        CreatePaymentRequest payReq = new CreatePaymentRequest();
        payReq.setTableName("Table 4");
        payReq.setAmount(BigDecimal.TEN);
        payReq.setCurrency("EUR");
        controller.createPayment(payReq);

        when(posReversalService.sendReversalRequest(any(), any(), any(), any(), any()))
                .thenReturn(null);

        CreateReversalRequest revReq = new CreateReversalRequest();
        revReq.setTableName("Table 4");

        ResponseEntity<CreateReversalResponse> response = controller.createReversal(revReq);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void abort_tableNotFound_returnsNotFound() throws IOException, ApiException {
        ResponseEntity response = controller.abort("NonexistentTable");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void abort_tableFoundWithServiceId_returns200() throws Exception {
        // First do a payment to set serviceId
        when(posPaymentService.sendPaymentRequest(any(), any(), any(), any(), any()))
                .thenReturn(createSuccessPaymentResponse());

        CreatePaymentRequest payReq = new CreatePaymentRequest();
        payReq.setTableName("Table 1");
        payReq.setAmount(BigDecimal.TEN);
        payReq.setCurrency("EUR");
        controller.createPayment(payReq);

        var abortResponse = new TerminalAPIResponse();
        when(posAbortService.sendAbortRequest(any(), any(), any()))
                .thenReturn(abortResponse);

        ResponseEntity response = controller.abort("Table 1");

        assertEquals(200, response.getStatusCode().value());
    }
}
