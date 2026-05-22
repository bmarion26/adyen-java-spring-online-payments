package com.adyen.checkout.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReversalPaymentRequestTest {

    @Test
    void gettersAndSetters() {
        ReversalPaymentRequest request = new ReversalPaymentRequest();

        request.setReference("ref123");
        assertEquals("ref123", request.getReference());
    }

    @Test
    void publicFieldAccess() {
        ReversalPaymentRequest request = new ReversalPaymentRequest();
        request.reference = "directRef";
        assertEquals("directRef", request.reference);
    }
}
