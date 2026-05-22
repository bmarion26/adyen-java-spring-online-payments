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

class PosAbortServiceTest {

    private PosAbortService posAbortService;
    private TerminalCloudApiService terminalCloudApiService;
    private TerminalCloudAPI terminalCloudAPI;

    @BeforeEach
    void setUp() throws Exception {
        posAbortService = new PosAbortService();

        terminalCloudApiService = mock(TerminalCloudApiService.class);
        terminalCloudAPI = mock(TerminalCloudAPI.class);
        when(terminalCloudApiService.getTerminalCloudApi()).thenReturn(terminalCloudAPI);

        Field field = PosAbortService.class.getDeclaredField("terminalCloudAPIService");
        field.setAccessible(true);
        field.set(posAbortService, terminalCloudApiService);
    }

    @Test
    void sendAbortRequest_callsTerminalApi() throws IOException, ApiException {
        TerminalAPIResponse mockResponse = new TerminalAPIResponse();
        when(terminalCloudAPI.sync(any())).thenReturn(mockResponse);

        TerminalAPIResponse response = posAbortService.sendAbortRequest("service123", "V400m-123", "TestSale");

        assertNotNull(response);
        verify(terminalCloudAPI).sync(any());
    }

    @Test
    void sendAbortRequest_buildsCorrectRequest() throws IOException, ApiException {
        when(terminalCloudAPI.sync(any())).thenReturn(new TerminalAPIResponse());

        posAbortService.sendAbortRequest("svc1", "poi1", "sale1");

        verify(terminalCloudAPI).sync(argThat(request -> {
            var header = request.getSaleToPOIRequest().getMessageHeader();
            return "poi1".equals(header.getPOIID()) &&
                    "sale1".equals(header.getSaleID());
        }));
    }
}
