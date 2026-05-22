package com.adyen.checkout.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    @BeforeEach
    void setUp() {
        Storage.getAllTokens().clear();
    }

    @Test
    void shopperReferenceConstant() {
        assertEquals("YOUR_UNIQUE_SHOPPER_ID_IOfW3k9G2PvYuJiol", Storage.SHOPPER_REFERENCE);
    }

    @Test
    void add_addsToken() {
        Storage.add("token1", "visa", "shopper1");
        assertEquals(1, Storage.getAllTokens().size());
    }

    @Test
    void add_multipleTokens() {
        Storage.add("token1", "visa", "shopper1");
        Storage.add("token2", "mastercard", "shopper1");
        assertEquals(2, Storage.getAllTokens().size());
    }

    @Test
    void add_duplicateTokenIgnored() {
        Storage.add("token1", "visa", "shopper1");
        Storage.add("token1", "visa", "shopper1");
        assertEquals(1, Storage.getAllTokens().size());
    }

    @Test
    void remove_removesMatchingToken() {
        Storage.add("token1", "visa", "shopper1");
        Storage.add("token2", "mastercard", "shopper1");
        Storage.remove("token1", "shopper1");
        assertEquals(1, Storage.getAllTokens().size());
    }

    @Test
    void remove_doesNothingWhenNotFound() {
        Storage.add("token1", "visa", "shopper1");
        Storage.remove("nonExistent", "shopper1");
        assertEquals(1, Storage.getAllTokens().size());
    }

    @Test
    void remove_requiresMatchingShopperReference() {
        Storage.add("token1", "visa", "shopper1");
        Storage.remove("token1", "differentShopper");
        assertEquals(1, Storage.getAllTokens().size());
    }

    @Test
    void getAllTokens_returnsEmptySetInitially() {
        assertNotNull(Storage.getAllTokens());
    }
}
