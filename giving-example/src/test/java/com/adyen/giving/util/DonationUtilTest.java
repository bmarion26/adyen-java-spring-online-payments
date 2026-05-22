package com.adyen.giving.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;

class DonationUtilTest {

    @Test
    void setDonationTokenAndOriginalPspReference_setsAttributes() {
        MockHttpSession session = new MockHttpSession();
        DonationUtil.setDonationTokenAndOriginalPspReference(session, "token123", "psp456");

        assertEquals("token123", session.getAttribute("DonationToken"));
        assertEquals("psp456", session.getAttribute("PaymentOriginalPspReference"));
    }

    @Test
    void setDonationTokenAndOriginalPspReference_throwsWhenTokenNull() {
        MockHttpSession session = new MockHttpSession();
        assertThrows(NullPointerException.class, () ->
                DonationUtil.setDonationTokenAndOriginalPspReference(session, null, "psp456"));
    }

    @Test
    void getDonationToken_returnsToken() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("DonationToken", "myToken");
        assertEquals("myToken", DonationUtil.getDonationToken(session));
    }

    @Test
    void getDonationToken_throwsWhenMissing() {
        MockHttpSession session = new MockHttpSession();
        assertThrows(Exception.class, () -> DonationUtil.getDonationToken(session));
    }

    @Test
    void getPaymentOriginalPspReference_returnsReference() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("PaymentOriginalPspReference", "pspRef");
        assertEquals("pspRef", DonationUtil.getPaymentOriginalPspReference(session));
    }

    @Test
    void getPaymentOriginalPspReference_throwsWhenMissing() {
        MockHttpSession session = new MockHttpSession();
        assertThrows(Exception.class, () -> DonationUtil.getPaymentOriginalPspReference(session));
    }
}
