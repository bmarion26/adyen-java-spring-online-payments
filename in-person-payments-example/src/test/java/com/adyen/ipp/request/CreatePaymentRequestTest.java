package com.adyen.ipp.request;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CreatePaymentRequestTest {

    @Test
    void gettersAndSetters() {
        CreatePaymentRequest request = new CreatePaymentRequest();

        request.setTableName("Table 1");
        assertEquals("Table 1", request.getTableName());

        request.setAmount(BigDecimal.valueOf(22.22));
        assertEquals(BigDecimal.valueOf(22.22), request.getAmount());

        request.setCurrency("EUR");
        assertEquals("EUR", request.getCurrency());
    }

    @Test
    void defaultValues() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        assertNull(request.getTableName());
        assertNull(request.getAmount());
        assertNull(request.getCurrency());
    }
}
