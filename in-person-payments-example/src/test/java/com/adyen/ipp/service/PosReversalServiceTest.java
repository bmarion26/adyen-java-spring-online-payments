package com.adyen.ipp.service;

import com.adyen.model.nexo.ReversalReasonType;
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

class PosReversalServiceTest {

    private PosReversalService posReversalService;
    private TerminalCloudApiService terminalCloudApiService;
    private TerminalCloudAPI terminalCloudAPI;

    @BeforeEach
    void setUp() throws Exception {
        posReversalService = new PosReversalService();

        terminalCloudApiService = mock(TerminalCloudApiService.class);
        terminalCloudAPI = mock(TerminalCloudAPI.class);
        when(terminalCloudApiService.getTerminalCloudApi()).thenReturn(terminalCloudAPI);

        Field field = PosReversalService.class.getDeclaredField("terminalCloudAPIService");
        field.setAccessible(true);
        field.set(posReversalService, terminalCloudApiService);
    }

    @Test
    void sendReversalRequest_callsTerminalApi() throws IOException, ApiException {
        TerminalAPIResponse mockResponse = new TerminalAPIResponse();
        when(terminalCloudAPI.sync(any())).thenReturn(mockResponse);

        TerminalAPIResponse response = posReversalService.sendReversalRequest(
                ReversalReasonType.MERCHANT_CANCEL, "saleTxId", "poiTxId", "V400m-123", "TestSale");

        assertNotNull(response);
        verify(terminalCloudAPI).sync(any());
    }

    @Test
    void sendReversalRequest_buildsCorrectRequest() throws IOException, ApiException {
        when(terminalCloudAPI.sync(any())).thenReturn(new TerminalAPIResponse());

        posReversalService.sendReversalRequest(
                ReversalReasonType.MERCHANT_CANCEL, "saleTxId", "poiTxId", "poi1", "sale1");

        verify(terminalCloudAPI).sync(argThat(request -> {
            var header = request.getSaleToPOIRequest().getMessageHeader();
            return "poi1".equals(header.getPOIID()) && "sale1".equals(header.getSaleID());
        }));
    }
}
