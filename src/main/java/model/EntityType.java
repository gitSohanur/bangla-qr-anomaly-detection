package model;

/** The three kinds of entity that can appear in a transaction. */
public enum EntityType {
    USER,
    MERCHANT,
    ACCOUNT;

    /**
     * Works out the entity type from a synthetic ID such as U001, M001 or A001.
     *
     * @throws IllegalArgumentException if the ID is null, shorter than 2 characters,
     *         has non-digit characters after the prefix, or has an unknown prefix
     */
    public static EntityType fromId(String id) {
        if (id == null || id.length() < 2) {
            throw new IllegalArgumentException("Invalid entity ID: " + id);
        }
        // ASCII digits only. Character.isDigit() would also accept Bengali digits.
        for (int i = 1; i < id.length(); i++) {
            char c = id.charAt(i);
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException(
                        "Invalid entity ID (digits expected after prefix): " + id);
            }
        }
        return switch (id.charAt(0)) {
            case 'U' -> USER;
            case 'M' -> MERCHANT;
            case 'A' -> ACCOUNT;
            default -> throw new IllegalArgumentException(
                    "Unknown ID prefix (expected U, M or A): " + id);
        };
    }
}