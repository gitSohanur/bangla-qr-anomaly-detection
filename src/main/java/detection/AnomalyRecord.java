package detection;

import datastructure.CustomLinkedList;

/**
 * Per-merchant transaction statistics and deterministic anomaly score.
 * This is an educational rule-based mechanism — not a machine-learning
 * model, not a banking risk model, and not proof of fraud.
 */
public class AnomalyRecord {

    // Thresholds and weights below are educational parameters, chosen for
    // this project, not officially validated banking thresholds.
    private static final int HIGH_INCOMING_COUNT_THRESHOLD = 5;
    private static final long HIGH_INCOMING_VOLUME_THRESHOLD = 5000;
    private static final int CONCENTRATION_MIN_TRANSACTIONS = 3;

    private static final int SCORE_HIGH_INCOMING_COUNT = 3;
    private static final int SCORE_HIGH_INCOMING_VOLUME = 2;
    private static final int SCORE_CONCENTRATED_SENDERS = 2;
    private static final int SCORE_CONCENTRATED_RECIPIENT = 2;
    private static final int SCORE_CYCLE_DETECTED = 4;

    private static final int LOW_ANOMALY_MIN_SCORE = 3;
    private static final int MEDIUM_ANOMALY_MIN_SCORE = 6;
    private static final int HIGH_ANOMALY_MIN_SCORE = 9;

    private final String merchantId;
    private int incomingTransactionCount;
    private int outgoingTransactionCount;
    private long incomingVolume;
    private long outgoingVolume;
    private final CustomLinkedList<String> uniqueSenderIds;
    private final CustomLinkedList<String> uniqueRecipientIds;
    private boolean cycleDetected;
    private int anomalyScore;
    private AnomalyStatus status;

    public AnomalyRecord(String merchantId) {
        this.merchantId = merchantId;
        this.uniqueSenderIds = new CustomLinkedList<>();
        this.uniqueRecipientIds = new CustomLinkedList<>();
        this.status = AnomalyStatus.NORMAL;
    }

    /** Records one incoming transaction (this merchant is the destination). */
    public void recordIncoming(String senderId, long amount) {
        incomingTransactionCount++;
        incomingVolume += amount;
        if (!uniqueSenderIds.contains(senderId)) {
            uniqueSenderIds.addLast(senderId);
        }
    }

    /** Records one outgoing transaction (this merchant is the source). */
    public void recordOutgoing(String recipientId, long amount) {
        outgoingTransactionCount++;
        outgoingVolume += amount;
        if (!uniqueRecipientIds.contains(recipientId)) {
            uniqueRecipientIds.addLast(recipientId);
        }
    }

    public void setCycleDetected(boolean cycleDetected) {
        this.cycleDetected = cycleDetected;
    }

    /** Computes the deterministic anomaly score and status from this record's current statistics. */
    public void computeAnomalyScore() {
        int score = 0;

        if (incomingTransactionCount >= HIGH_INCOMING_COUNT_THRESHOLD) {
            score += SCORE_HIGH_INCOMING_COUNT;
        }
        if (incomingVolume >= HIGH_INCOMING_VOLUME_THRESHOLD) {
            score += SCORE_HIGH_INCOMING_VOLUME;
        }
        if (incomingTransactionCount >= CONCENTRATION_MIN_TRANSACTIONS && uniqueSenderIds.size() == 1) {
            score += SCORE_CONCENTRATED_SENDERS;
        }
        if (outgoingTransactionCount >= 1 && uniqueRecipientIds.size() == 1) {
            score += SCORE_CONCENTRATED_RECIPIENT;
        }
        if (cycleDetected) {
            score += SCORE_CYCLE_DETECTED;
        }

        this.anomalyScore = score;
        this.status = deriveStatus(score);
    }

    private AnomalyStatus deriveStatus(int score) {
        if (score >= HIGH_ANOMALY_MIN_SCORE) return AnomalyStatus.HIGH_ANOMALY;
        if (score >= MEDIUM_ANOMALY_MIN_SCORE) return AnomalyStatus.MEDIUM_ANOMALY;
        if (score >= LOW_ANOMALY_MIN_SCORE) return AnomalyStatus.LOW_ANOMALY;
        return AnomalyStatus.NORMAL;
    }

    public String getMerchantId() { return merchantId; }
    public int getIncomingTransactionCount() { return incomingTransactionCount; }
    public int getOutgoingTransactionCount() { return outgoingTransactionCount; }
    public long getIncomingVolume() { return incomingVolume; }
    public long getOutgoingVolume() { return outgoingVolume; }
    public int getUniqueSenderCount() { return uniqueSenderIds.size(); }
    public int getUniqueRecipientCount() { return uniqueRecipientIds.size(); }
    public boolean isCycleDetected() { return cycleDetected; }
    public int getAnomalyScore() { return anomalyScore; }
    public AnomalyStatus getStatus() { return status; }

    @Override
    public String toString() {
        return merchantId
                + " | In: " + incomingTransactionCount + " (" + incomingVolume + ")"
                + " | Out: " + outgoingTransactionCount + " (" + outgoingVolume + ")"
                + " | UniqueSenders: " + uniqueSenderIds.size()
                + " | UniqueRecipients: " + uniqueRecipientIds.size()
                + " | Cycle: " + (cycleDetected ? "YES" : "NO")
                + " | Score: " + anomalyScore
                + " | Status: " + status;
    }
}