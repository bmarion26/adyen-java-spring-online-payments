package com.adyen.ipp.service;

import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.TerminalCloudAPI;
import com.adyen.service.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PosPaymentServiceTest {

    private PosPaymentService posPaymentService;
    private TerminalCloudApiService terminalCloudApiService;
    private TerminalCloudAPI terminalCloudAPI;

    @BeforeEach
    void setUp() throws Exception {
        posPaymentService = new PosPaymentService();

        terminalCloudApiService = mock(TerminalCloudApiService.class);
        terminalCloudAPI = mock(TerminalCloudAPI.class);
        when(terminalCloudApiService.getTerminalCloudApi()).thenReturn(terminalCloudAPI);

        Field field = PosPaymentService.class.getDeclaredField("terminalCloudAPIService");
        field.setAccessible(true);
        field.set(posPaymentService, terminalCloudApiService);
    }

    @Test
    void sendPaymentRequest_callsTerminalApi() throws IOException, ApiException {
        TerminalAPIResponse mockResponse = new TerminalAPIResponse();
        when(terminalCloudAPI.sync(any())).thenReturn(mockResponse);

        TerminalAPIResponse response = posPaymentService.sendPaymentRequest(
                "service123", "V400m-123", "TestSale", "EUR", BigDecimal.TEN);

        assertNotNull(response);
        verify(terminalCloudAPI).sync(any());
    }

    @Test
    void sendPaymentRequest_buildsCorrectRequest() throws IOException, ApiException {
        when(terminalCloudAPI.sync(any())).thenReturn(new TerminalAPIResponse());

        posPaymentService.sendPaymentRequest("svc1", "poi1", "sale1", "USD", BigDecimal.valueOf(42.99));

        verify(terminalCloudAPI).sync(argThat(request -> {
            var header = request.getSaleToPOIRequest().getMessageHeader();
            return "poi1".equals(header.getPOIID()) &&
                    "sale1".equals(header.getSaleID()) &&
                    "svc1".equals(header.getServiceID());
        }));
    }
}
