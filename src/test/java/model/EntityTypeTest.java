package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityTypeTest {

    @Test
    void fromId_userPrefix_returnsUser() {
        assertEquals(EntityType.USER, EntityType.fromId("U001"));
    }

    @Test
    void fromId_merchantPrefix_returnsMerchant() {
        assertEquals(EntityType.MERCHANT, EntityType.fromId("M001"));
    }

    @Test
    void fromId_accountPrefix_returnsAccount() {
        assertEquals(EntityType.ACCOUNT, EntityType.fromId("A001"));
    }

    @Test
    void fromId_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> EntityType.fromId(null));
    }

    // Cases: empty, prefix only, unknown prefix, lowercase prefix,
    // letter among digits, Bengali digits (\u09E6\u09E7)
    @ParameterizedTest
    @ValueSource(strings = {"", "U", "X001", "u001", "U12A", "U\u09E6\u09E7"})
    void fromId_invalidFormat_throws(String id) {
        assertThrows(IllegalArgumentException.class, () -> EntityType.fromId(id));
    }
}