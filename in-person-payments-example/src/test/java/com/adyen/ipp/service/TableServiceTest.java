package com.adyen.ipp.service;

import com.adyen.ipp.model.PaymentStatus;
import com.adyen.ipp.model.Table;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TableServiceTest {

    @Test
    void constructor_createsFourTables() {
        TableService service = new TableService();
        ArrayList<Table> tables = service.getTables();
        assertEquals(4, tables.size());
    }

    @Test
    void tables_haveCorrectNames() {
        TableService service = new TableService();
        ArrayList<Table> tables = service.getTables();

        assertEquals("Table 1", tables.get(0).getTableName());
        assertEquals("Table 2", tables.get(1).getTableName());
        assertEquals("Table 3", tables.get(2).getTableName());
        assertEquals("Table 4", tables.get(3).getTableName());
    }

    @Test
    void tables_haveCorrectAmounts() {
        TableService service = new TableService();
        ArrayList<Table> tables = service.getTables();

        assertEquals(0, BigDecimal.valueOf(22.22).compareTo(tables.get(0).getAmount()));
        assertEquals(0, BigDecimal.valueOf(44.44).compareTo(tables.get(1).getAmount()));
        assertEquals(0, BigDecimal.valueOf(66.66).compareTo(tables.get(2).getAmount()));
        assertEquals(0, BigDecimal.valueOf(88.88).compareTo(tables.get(3).getAmount()));
    }

    @Test
    void tables_haveEurCurrency() {
        TableService service = new TableService();
        for (Table table : service.getTables()) {
            assertEquals("EUR", table.getCurrency());
        }
    }

    @Test
    void tables_areNotPaidByDefault() {
        TableService service = new TableService();
        for (Table table : service.getTables()) {
            assertEquals(PaymentStatus.NotPaid, table.getPaymentStatus());
        }
    }

    @Test
    void tables_havePaymentStatusDetails() {
        TableService service = new TableService();
        for (Table table : service.getTables()) {
            assertNotNull(table.getPaymentStatusDetails());
        }
    }
}
