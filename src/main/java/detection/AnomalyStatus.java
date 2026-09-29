package detection;

/** Educational anomaly categories — not legal, regulatory, or official banking classifications. */
public enum AnomalyStatus {
    NORMAL("NORMAL"),
    LOW_ANOMALY("LOW ANOMALY"),
    MEDIUM_ANOMALY("MEDIUM ANOMALY"),
    HIGH_ANOMALY("HIGH ANOMALY");

    private final String label;

    AnomalyStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

