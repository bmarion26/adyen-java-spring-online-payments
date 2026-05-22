package com.adyen.ipp.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateReversalRequestTest {

    @Test
    void gettersAndSetters() {
        CreateReversalRequest request = new CreateReversalRequest();

        request.setTableName("Table 1");
        assertEquals("Table 1", request.getTableName());
    }

    @Test
    void defaultValues() {
        CreateReversalRequest request = new CreateReversalRequest();
        assertNull(request.getTableName());
    }
}
