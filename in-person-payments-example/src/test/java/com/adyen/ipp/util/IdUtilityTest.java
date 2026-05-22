package com.adyen.ipp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdUtilityTest {

    @Test
    void getRandomAlphanumericId_returnsCorrectLength() {
        assertEquals(10, IdUtility.getRandomAlphanumericId(10).length());
        assertEquals(1, IdUtility.getRandomAlphanumericId(1).length());
        assertEquals(50, IdUtility.getRandomAlphanumericId(50).length());
    }

    @Test
    void getRandomAlphanumericId_returnsOnlyAlphanumericCharacters() {
        String id = IdUtility.getRandomAlphanumericId(100);
        assertTrue(id.matches("[A-Za-z0-9]+"));
    }

    @Test
    void getRandomAlphanumericId_returnsUniqueValues() {
        String id1 = IdUtility.getRandomAlphanumericId(20);
        String id2 = IdUtility.getRandomAlphanumericId(20);
        assertNotEquals(id1, id2);
    }

    @Test
    void getRandomAlphanumericId_zeroLength_returnsEmpty() {
        assertEquals(0, IdUtility.getRandomAlphanumericId(0).length());
    }
}
