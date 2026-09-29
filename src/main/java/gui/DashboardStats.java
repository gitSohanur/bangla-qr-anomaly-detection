package gui;

import datastructure.CustomLinkedList;
import detection.AnomalyRecord;
import detection.AnomalyStatus;
import graph.Graph;
import graph.Vertex;
import model.EntityType;

/**
 * Pure helper functions for dashboard figures and text formatting.
 * Deliberately separate from DashboardController (JavaFX wiring) so these
 * can be unit tested without starting the JavaFX runtime.
 */
public class DashboardStats {

    private DashboardStats() { }

    public static int countUsers(Graph graph) {
        int count = 0;
        for (Vertex v : graph.getVertices()) {
            if (v.getType() == EntityType.USER) {
                count++;
            }
        }
        return count;
    }

    public static int countHighAnomaly(CustomLinkedList<AnomalyRecord> records) {
        int count = 0;
        for (AnomalyRecord r : records) {
            if (r.getStatus() == AnomalyStatus.HIGH_ANOMALY) {
                count++;
            }
        }
        return count;
    }

    /** One readable line per ranked merchant, matching the CLI's RANKED MERCHANTS shape. */
    public static String formatRankedList(CustomLinkedList<AnomalyRecord> ranked) {
        if (ranked.isEmpty()) {
            return "(no merchants to display)";
        }
        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (AnomalyRecord r : ranked) {
            sb.append(rank).append(". ")
                    .append(r.getMerchantId())
                    .append("   Score: ").append(r.getAnomalyScore())
                    .append("   Status: ").append(r.getStatus())
                    .append(r.isCycleDetected() ? "   [CYCLE]" : "")
                    .append('\n');
            rank++;
        }
        return sb.toString();
    }
}