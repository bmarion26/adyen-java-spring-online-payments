package com.adyen.ipp.model;

import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class PaymentStatusDetailsTest {

    @Test
    void gettersAndSetters() throws DatatypeConfigurationException {
        PaymentStatusDetails details = new PaymentStatusDetails();

        details.setPspReference("psp123");
        assertEquals("psp123", details.getPspReference());

        details.setRefusalReason("refused");
        assertEquals("refused", details.getRefusalReason());

        details.setPoiTransactionId("poi123");
        assertEquals("poi123", details.getPoiTransactionId());

        details.setSaleTransactionId("sale123");
        assertEquals("sale123", details.getSaleTransactionId());

        details.setServiceId("service123");
        assertEquals("service123", details.getServiceId());

        XMLGregorianCalendar cal = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(new GregorianCalendar());

        details.setPoiTransactionTimeStamp(cal);
        assertEquals(cal, details.getPoiTransactionTimeStamp());

        details.setSaleTransactionTimeStamp(cal);
        assertEquals(cal, details.getSaleTransactionTimeStamp());
    }

    @Test
    void defaultValues_areNull() {
        PaymentStatusDetails details = new PaymentStatusDetails();
        assertNull(details.getPspReference());
        assertNull(details.getRefusalReason());
        assertNull(details.getPoiTransactionId());
        assertNull(details.getPoiTransactionTimeStamp());
        assertNull(details.getSaleTransactionId());
        assertNull(details.getSaleTransactionTimeStamp());
        assertNull(details.getServiceId());
    }
}
