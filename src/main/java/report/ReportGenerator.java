package report;

import algorithm.CycleDetector;
import datastructure.CustomLinkedList;
import detection.AnomalyRecord;
import graph.Vertex;

/** Formats and prints the console report for each stage of the pipeline. */
public class ReportGenerator {
    private static final String DIVIDER = "----------------------------------------";
    private static final String DOUBLE_DIVIDER = "========================================";

    public void printBanner() {
        System.out.println(DOUBLE_DIVIDER);
        System.out.println("BANGLA QR ANOMALY DETECTION");
        System.out.println(DOUBLE_DIVIDER);
        System.out.println();
    }

    public void printCycleResult(boolean cycleFound, CycleDetector detector) {
        if (cycleFound) {
            System.out.println("Cycle detected:");
            System.out.println("  " + formatCyclePath(detector.getCyclePath()));
            System.out.println("Cycle length: " + detector.getCycleLength());
        } else {
            System.out.println("No cycle detected.");
        }
        System.out.println();
    }

    private String formatCyclePath(CustomLinkedList<Vertex> path) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Vertex v : path) {
            if (!first) sb.append(" -> ");
            sb.append(v.getId());
            first = false;
        }
        return sb.toString();
    }

    public void printMerchantAnalysis(CustomLinkedList<AnomalyRecord> records) {
        System.out.println("Merchant Analysis");
        System.out.println(DIVIDER);
        for (AnomalyRecord r : records) {
            System.out.println();
            System.out.println(r.getMerchantId());
            System.out.println("Incoming Transactions : " + r.getIncomingTransactionCount());
            System.out.println("Outgoing Transactions : " + r.getOutgoingTransactionCount());
            System.out.println("Incoming Volume       : " + r.getIncomingVolume());
            System.out.println("Outgoing Volume       : " + r.getOutgoingVolume());
            System.out.println("Cycle Detected        : " + (r.isCycleDetected() ? "YES" : "NO"));
            System.out.println("Anomaly Score         : " + r.getAnomalyScore());
            System.out.println("Status                : " + r.getStatus());
        }
        System.out.println();
        System.out.println(DIVIDER);
        System.out.println();
    }

    public void printRankedResults(CustomLinkedList<AnomalyRecord> ranked) {
        System.out.println("RANKED MERCHANTS");
        System.out.println(DIVIDER);
        System.out.println();
        int rank = 1;
        for (AnomalyRecord r : ranked) {
            System.out.println(rank + ". " + r.getMerchantId() + "   Score: " + r.getAnomalyScore());
            rank++;
        }
        System.out.println();
    }

    public void printDisclaimer() {
        System.out.println(DIVIDER);
        System.out.println("This is an educational simulation using synthetic data.");
        System.out.println("It is NOT a production banking fraud-detection system and");
        System.out.println("does NOT prove fraud, criminal activity, or guilt.");
        System.out.println("High-scoring merchants require further review only.");
        System.out.println(DIVIDER);
    }
}