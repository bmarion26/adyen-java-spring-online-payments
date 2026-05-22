package com.adyen.checkout.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDetailsModelTest {

    @Test
    void constructorSetsAllFields() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        PaymentDetailsModel model = new PaymentDetailsModel(
                "ref1", "psp1", "origRef", 10000L, "EUR",
                dateTime, "AUTHORISATION", "reason", "visa", true);

        assertEquals("ref1", model.getMerchantReference());
        assertEquals("psp1", model.getPspReference());
        assertEquals("origRef", model.getOriginalReference());
        assertEquals(10000.0, model.getAmount());
        assertEquals("EUR", model.getCurrency());
        assertEquals(dateTime, model.getDateTime());
        assertEquals("AUTHORISATION", model.getEventCode());
        assertEquals("reason", model.getRefusalReason());
        assertEquals("visa", model.getPaymentMethodBrand());
        assertTrue(model.getSuccess());
    }

    @Test
    void setters() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        PaymentDetailsModel model = new PaymentDetailsModel(
                "ref1", "psp1", "origRef", 10000L, "EUR",
                dateTime, "AUTHORISATION", null, "visa", true);

        model.setMerchantReference("newRef");
        assertEquals("newRef", model.getMerchantReference());

        model.setPspReference("newPsp");
        assertEquals("newPsp", model.getPspReference());

        model.setOriginalReference("newOrig");
        assertEquals("newOrig", model.getOriginalReference());

        model.setAmount(20000L);
        assertEquals(20000.0, model.getAmount());

        model.setCurrency("USD");
        assertEquals("USD", model.getCurrency());

        LocalDateTime newDateTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        model.setDateTime(newDateTime);
        assertEquals(newDateTime, model.getDateTime());

        model.setEventCode("CAPTURE");
        assertEquals("CAPTURE", model.getEventCode());

        model.setRefusalReason("refused");
        assertEquals("refused", model.getRefusalReason());

        model.setPaymentMethodBrand("mastercard");
        assertEquals("mastercard", model.getPaymentMethodBrand());

        model.setSuccess(false);
        assertFalse(model.getSuccess());
    }
}
