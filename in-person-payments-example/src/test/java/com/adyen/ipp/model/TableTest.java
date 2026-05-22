package com.adyen.ipp.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    @Test
    void gettersAndSetters() {
        Table table = new Table();

        table.setTableName("Table 1");
        assertEquals("Table 1", table.getTableName());

        table.setAmount(BigDecimal.valueOf(50.00));
        assertEquals(BigDecimal.valueOf(50.00), table.getAmount());

        table.setCurrency("EUR");
        assertEquals("EUR", table.getCurrency());

        table.setPaymentStatus(PaymentStatus.Paid);
        assertEquals(PaymentStatus.Paid, table.getPaymentStatus());

        PaymentStatusDetails details = new PaymentStatusDetails();
        table.setPaymentStatusDetails(details);
        assertSame(details, table.getPaymentStatusDetails());
    }

    @Test
    void defaultPaymentStatus_isNotPaid() {
        Table table = new Table();
        assertEquals(PaymentStatus.NotPaid, table.getPaymentStatus());
    }

    @Test
    void allPaymentStatuses() {
        Table table = new Table();

        for (PaymentStatus status : PaymentStatus.values()) {
            table.setPaymentStatus(status);
            assertEquals(status, table.getPaymentStatus());
        }
    }
}
