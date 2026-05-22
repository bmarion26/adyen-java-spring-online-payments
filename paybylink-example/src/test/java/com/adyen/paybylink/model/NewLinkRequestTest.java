package com.adyen.paybylink.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewLinkRequestTest {

    @Test
    void defaultConstructor() {
        NewLinkRequest request = new NewLinkRequest();
        assertNull(request.getAmount());
        assertNull(request.getReference());
    }

    @Test
    void parameterizedConstructor() {
        NewLinkRequest request = new NewLinkRequest(100L, "ref123");
        assertEquals(100L, request.getAmount());
        assertEquals("ref123", request.getReference());
    }

    @Test
    void gettersAndSetters() {
        NewLinkRequest request = new NewLinkRequest();

        request.setAmount(500L);
        assertEquals(500L, request.getAmount());

        request.setReference("myRef");
        assertEquals("myRef", request.getReference());
    }
}
