import model.Transaction;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        System.out.println("Bangla-QR Anomaly Detection - Phase 1 demo");

        Transaction t1 = new Transaction("T001", "U001", "M001", 500, LocalDateTime.of(2026, 9, 1, 10, 15));
        Transaction t2 = new Transaction("T002", "U002", "M001", 800, LocalDateTime.of(2026, 9, 1, 10, 17));
        Transaction t3 = new Transaction("T003", "M001", "A001", 1200, LocalDateTime.of(2026, 9, 1, 10, 30));

        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t3);
        System.out.println("Source type of T003: " + t3.getSourceType());

        try {
            new Transaction("T004", "U001", "U001", 100, LocalDateTime.of(2026, 9, 1, 11, 0));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}