package com.adyen.ipp.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatePaymentResponseTest {

    @Test
    void gettersAndSetters() {
        CreatePaymentResponse response = new CreatePaymentResponse();

        response.setResult("success");
        assertEquals("success", response.getResult());

        response.setRefusalReason("reason");
        assertEquals("reason", response.getRefusalReason());
    }

    @Test
    void fluentApi() {
        CreatePaymentResponse response = new CreatePaymentResponse()
                .result("success")
                .refusalReason("none");

        assertEquals("success", response.getResult());
        assertEquals("none", response.getRefusalReason());
    }

    @Test
    void fluentApi_returnsSameInstance() {
        CreatePaymentResponse response = new CreatePaymentResponse();
        assertSame(response, response.result("success"));
        assertSame(response, response.refusalReason("none"));
    }
}
