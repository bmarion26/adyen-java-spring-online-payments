package com.adyen.ipp.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateReversalResponseTest {

    @Test
    void gettersAndSetters() {
        CreateReversalResponse response = new CreateReversalResponse();

        response.setResult("success");
        assertEquals("success", response.getResult());

        response.setRefusalReason("reason");
        assertEquals("reason", response.getRefusalReason());
    }

    @Test
    void fluentApi() {
        CreateReversalResponse response = new CreateReversalResponse()
                .result("success")
                .refusalReason("none");

        assertEquals("success", response.getResult());
        assertEquals("none", response.getRefusalReason());
    }

    @Test
    void fluentApi_returnsSameInstance() {
        CreateReversalResponse response = new CreateReversalResponse();
        assertSame(response, response.result("success"));
        assertSame(response, response.refusalReason("none"));
    }
}
