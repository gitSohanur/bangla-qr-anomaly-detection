package model;

import java.time.LocalDateTime;

/** One synthetic QR payment: money moving from a source entity to a destination entity. */
public final class Transaction {
    private final String transactionId;
    private final String sourceId;
    private final String destinationId;
    private final EntityType sourceType;
    private final EntityType destinationType;
    private final long amount;              // whole taka
    private final LocalDateTime timestamp;

    public Transaction(String transactionId, String sourceId, String destinationId,
                       long amount, LocalDateTime timestamp) {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID must not be empty");
        }
        this.sourceType = EntityType.fromId(sourceId);            // validates too
        this.destinationType = EntityType.fromId(destinationId);  // validates too
        if (sourceId.equals(destinationId)) {
            throw new IllegalArgumentException(
                    "Source and destination must be different: " + sourceId);
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive: " + amount);
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp must not be null");
        }
        this.transactionId = transactionId;
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getTransactionId() { return transactionId; }
    public String getSourceId() { return sourceId; }
    public String getDestinationId() { return destinationId; }
    public EntityType getSourceType() { return sourceType; }
    public EntityType getDestinationType() { return destinationType; }
    public long getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction other = (Transaction) o;
        return transactionId.equals(other.transactionId);
    }

    @Override
    public int hashCode() {
        return transactionId.hashCode();
    }

    @Override
    public String toString() {
        return transactionId + ": " + sourceId + " -> " + destinationId
                + " | " + amount + " | " + timestamp;
    }
} 