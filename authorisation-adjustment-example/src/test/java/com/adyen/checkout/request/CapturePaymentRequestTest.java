package com.adyen.checkout.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CapturePaymentRequestTest {

    @Test
    void gettersAndSetters() {
        CapturePaymentRequest request = new CapturePaymentRequest();

        request.setReference("ref123");
        assertEquals("ref123", request.getReference());

        request.setAmount(10000L);
        assertEquals(10000L, request.getAmount());
    }

    @Test
    void publicFieldAccess() {
        CapturePaymentRequest request = new CapturePaymentRequest();
        request.reference = "directRef";
        request.amount = 5000L;

        assertEquals("directRef", request.reference);
        assertEquals(5000L, request.amount);
    }
}
