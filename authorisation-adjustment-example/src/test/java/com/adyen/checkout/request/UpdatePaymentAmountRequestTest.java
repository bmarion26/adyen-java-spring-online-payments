package com.adyen.checkout.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdatePaymentAmountRequestTest {

    @Test
    void gettersAndSetters() {
        UpdatePaymentAmountRequest request = new UpdatePaymentAmountRequest();

        request.setReference("ref123");
        assertEquals("ref123", request.getReference());

        request.setAmount(25000L);
        assertEquals(25000L, request.getAmount());
    }

    @Test
    void publicFieldAccess() {
        UpdatePaymentAmountRequest request = new UpdatePaymentAmountRequest();
        request.reference = "directRef";
        request.amount = 15000L;

        assertEquals("directRef", request.reference);
        assertEquals(15000L, request.amount);
    }
}
