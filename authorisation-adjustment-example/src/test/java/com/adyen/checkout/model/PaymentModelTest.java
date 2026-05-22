package com.adyen.checkout.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PaymentModelTest {

    @Test
    void constructorSetsAllFields() {
        LocalDateTime booking = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime expiry = LocalDateTime.of(2024, 1, 29, 10, 0);
        var details = new ArrayList<PaymentDetailsModel>();

        PaymentModel model = new PaymentModel("ref1", "psp1", 10000L, "EUR",
                booking, expiry, "visa", details);

        assertEquals("ref1", model.getMerchantReference());
        assertEquals("psp1", model.getPspReference());
        assertEquals(10000L, model.getAmount());
        assertEquals("EUR", model.getCurrency());
        assertEquals(booking, model.getBookingDate());
        assertEquals(expiry, model.getExpiryDate());
        assertEquals("visa", model.getPaymentMethodBrand());
        assertSame(details, model.getPaymentDetailsModelList());
    }

    @Test
    void getDaysUntilExpiry_returns28() {
        LocalDateTime booking = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime expiry = LocalDateTime.of(2024, 1, 29, 10, 0);

        PaymentModel model = new PaymentModel("ref1", "psp1", 10000L, "EUR",
                booking, expiry, "visa", new ArrayList<>());

        assertEquals(28, model.getDaysUntilExpiry());
    }

    @Test
    void getFormattedExpiryDate_returnsCorrectFormat() {
        LocalDateTime expiry = LocalDateTime.of(2024, 3, 15, 14, 30, 45);

        PaymentModel model = new PaymentModel("ref1", "psp1", 10000L, "EUR",
                LocalDateTime.now(), expiry, "visa", new ArrayList<>());

        assertEquals("2024-03-15 14:30:45", model.getFormattedExpiryDate());
    }

    @Test
    void setters() {
        PaymentModel model = new PaymentModel("ref1", "psp1", 10000L, "EUR",
                LocalDateTime.now(), LocalDateTime.now(), "visa", new ArrayList<>());

        model.setMerchantReference("newRef");
        assertEquals("newRef", model.getMerchantReference());

        model.setPspReference("newPsp");
        assertEquals("newPsp", model.getPspReference());

        model.setAmount(20000L);
        assertEquals(20000L, model.getAmount());

        model.setCurrency("USD");
        assertEquals("USD", model.getCurrency());

        model.setPaymentMethodBrand("mastercard");
        assertEquals("mastercard", model.getPaymentMethodBrand());

        var newDetails = new ArrayList<PaymentDetailsModel>();
        model.setPaymentDetailsModelList(newDetails);
        assertSame(newDetails, model.getPaymentDetailsModelList());

        LocalDateTime newBooking = LocalDateTime.of(2025, 1, 1, 0, 0);
        model.setBookingDate(newBooking);
        assertEquals(newBooking, model.getBookingDate());

        LocalDateTime newExpiry = LocalDateTime.of(2025, 2, 1, 0, 0);
        model.setExpiryDate(newExpiry);
        assertEquals(newExpiry, model.getExpiryDate());
    }
}
