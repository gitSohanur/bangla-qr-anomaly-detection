package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
    private static final LocalDateTime TIME = LocalDateTime.of(2026, 9, 1, 10, 15);

    @Test
    void constructor_validInput_storesAllFields() {
        Transaction t = new Transaction("T001", "U001", "M001", 500, TIME);
        assertEquals("T001", t.getTransactionId());
        assertEquals("U001", t.getSourceId());
        assertEquals("M001", t.getDestinationId());
        assertEquals(500, t.getAmount());
        assertEquals(TIME, t.getTimestamp());
    }

    @Test
    void types_areDerivedFromIds() {
        Transaction t = new Transaction("T003", "M001", "A001", 1200, TIME);
        assertEquals(EntityType.MERCHANT, t.getSourceType());
        assertEquals(EntityType.ACCOUNT, t.getDestinationType());
    }

    @Test
    void constructor_nullTransactionId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(null, "U001", "M001", 500, TIME));
    }

    @Test
    void constructor_blankTransactionId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction("   ", "U001", "M001", 500, TIME));
    }

    @Test
    void constructor_invalidSourceId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction("T001", "X001", "M001", 500, TIME));
    }

    @Test
    void constructor_invalidDestinationId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction("T001", "U001", null, 500, TIME));
    }

    @Test
    void constructor_sameSourceAndDestination_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction("T001", "M001", "M001", 500, TIME));
    }

    @Test
    void constructor_zeroAmount_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction("T001", "U001", "M001", 0, TIME));
    }

    @Test
    void constructor_negativeAmount_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction("T001", "U001", "M001", -50, TIME));
    }

    @Test
    void constructor_nullTimestamp_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction("T001", "U001", "M001", 500, null));
    }

    @Test
    void equals_sameId_areEqualEvenIfOtherFieldsDiffer() {
        Transaction a = new Transaction("T001", "U001", "M001", 500, TIME);
        Transaction b = new Transaction("T001", "U002", "M002", 900, TIME.plusHours(1));
        assertEquals(a, b);
    }

    @Test
    void equals_differentId_areNotEqual() {
        Transaction a = new Transaction("T001", "U001", "M001", 500, TIME);
        Transaction b = new Transaction("T002", "U001", "M001", 500, TIME);
        assertNotEquals(a, b);
    }

    @Test
    void toString_hasExpectedFormat() {
        Transaction t = new Transaction("T001", "U001", "M001", 500, TIME);
        assertEquals("T001: U001 -> M001 | 500 | 2026-09-01T10:15", t.toString());
    }
}