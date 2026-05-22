package com.adyen.checkout.util;

import com.adyen.checkout.model.PaymentDetailsModel;
import com.adyen.checkout.model.PaymentModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    @BeforeEach
    void setUp() {
        Storage.getAll().clear();
    }

    @Test
    void put_addsPayment() {
        PaymentModel payment = createPayment("ref1", "psp1", 10000L);
        Storage.put(payment);
        assertEquals(1, Storage.getAll().size());
        assertEquals("ref1", Storage.getAll().get(0).getMerchantReference());
    }

    @Test
    void getAll_returnsAllPayments() {
        Storage.put(createPayment("ref1", "psp1", 10000L));
        Storage.put(createPayment("ref2", "psp2", 20000L));
        assertEquals(2, Storage.getAll().size());
    }

    @Test
    void findByMerchantReference_findsExisting() {
        Storage.put(createPayment("ref1", "psp1", 10000L));
        PaymentModel found = Storage.findByMerchantReference("ref1");
        assertNotNull(found);
        assertEquals("psp1", found.getPspReference());
    }

    @Test
    void findByMerchantReference_returnsNullForMissing() {
        assertNull(Storage.findByMerchantReference("nonExistent"));
    }

    @Test
    void addPaymentToHistory_addsDetails() {
        PaymentModel payment = createPayment("ref1", "psp1", 10000L);
        Storage.put(payment);

        PaymentDetailsModel details = new PaymentDetailsModel(
                "ref1", "psp2", "psp1", 10000L, "EUR",
                LocalDateTime.now(), "CAPTURE", null, "visa", true);
        Storage.addPaymentToHistory(details);

        assertEquals(1, payment.getPaymentDetailsModelList().size());
    }

    @Test
    void addPaymentToHistory_throwsWhenMerchantReferenceNull() {
        PaymentDetailsModel details = new PaymentDetailsModel(
                null, "psp2", "psp1", 10000L, "EUR",
                LocalDateTime.now(), "CAPTURE", null, "visa", true);
        assertThrows(IllegalArgumentException.class, () -> Storage.addPaymentToHistory(details));
    }

    @Test
    void addPaymentToHistory_doesNothingWhenPaymentNotFound() {
        PaymentDetailsModel details = new PaymentDetailsModel(
                "nonExistent", "psp2", "psp1", 10000L, "EUR",
                LocalDateTime.now(), "CAPTURE", null, "visa", true);
        assertDoesNotThrow(() -> Storage.addPaymentToHistory(details));
    }

    @Test
    void updatePayment_updatesAmountAndExpiry() {
        PaymentModel payment = createPayment("ref1", "psp1", 10000L);
        Storage.put(payment);

        LocalDateTime newExpiry = LocalDateTime.now().plusDays(56);
        Storage.updatePayment("ref1", 20000L, newExpiry);

        assertEquals(20000L, payment.getAmount());
        assertEquals(newExpiry, payment.getExpiryDate());
    }

    @Test
    void updatePayment_doesNothingWhenNotFound() {
        LocalDateTime expiry = LocalDateTime.now().plusDays(28);
        assertDoesNotThrow(() -> Storage.updatePayment("nonExistent", 10000L, expiry));
    }

    private PaymentModel createPayment(String merchantRef, String pspRef, long amount) {
        return new PaymentModel(merchantRef, pspRef, amount, "EUR",
                LocalDateTime.now(), LocalDateTime.now().plusDays(28), "visa", new ArrayList<>());
    }
}
