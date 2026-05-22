package com.adyen.ipp.service;

import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.TerminalCloudAPI;
import com.adyen.service.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PosTransactionStatusServiceTest {

    private PosTransactionStatusService posTransactionStatusService;
    private TerminalCloudApiService terminalCloudApiService;
    private TerminalCloudAPI terminalCloudAPI;

    @BeforeEach
    void setUp() throws Exception {
        posTransactionStatusService = new PosTransactionStatusService();

        terminalCloudApiService = mock(TerminalCloudApiService.class);
        terminalCloudAPI = mock(TerminalCloudAPI.class);
        when(terminalCloudApiService.getTerminalCloudApi()).thenReturn(terminalCloudAPI);

        Field field = PosTransactionStatusService.class.getDeclaredField("terminalCloudAPIService");
        field.setAccessible(true);
        field.set(posTransactionStatusService, terminalCloudApiService);
    }

    @Test
    void sendTransactionStatusRequest_callsTerminalApi() throws IOException, ApiException {
        TerminalAPIResponse mockResponse = new TerminalAPIResponse();
        when(terminalCloudAPI.sync(any())).thenReturn(mockResponse);

        TerminalAPIResponse response = posTransactionStatusService.sendTransactionStatusRequest(
                "service123", "V400m-123", "TestSale");

        assertNotNull(response);
        verify(terminalCloudAPI).sync(any());
    }

    @Test
    void sendTransactionStatusRequest_buildsCorrectRequest() throws IOException, ApiException {
        when(terminalCloudAPI.sync(any())).thenReturn(new TerminalAPIResponse());

        posTransactionStatusService.sendTransactionStatusRequest("svc1", "poi1", "sale1");

        verify(terminalCloudAPI).sync(argThat(request -> {
            var header = request.getSaleToPOIRequest().getMessageHeader();
            return "poi1".equals(header.getPOIID()) && "sale1".equals(header.getSaleID());
        }));
    }
}
