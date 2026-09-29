package detection;

import algorithm.CycleDetector;
import datastructure.CustomLinkedList;
import graph.Edge;
import graph.Graph;
import graph.Vertex;
import model.EntityType;

/**
 * Builds per-merchant anomaly records from a transaction graph, combining
 * raw transaction statistics with the result of DFS-based cycle detection.
 */
public class MerchantAnalyzer {

    /**
     * Analyzes the given graph: computes per-merchant statistics, marks merchants
     * involved in the first detected cycle, and scores every merchant record.
     * Only merchants that appear in at least one transaction receive a record.
     */
    public CustomLinkedList<AnomalyRecord> analyze(Graph graph) {
        CustomLinkedList<AnomalyRecord> records = new CustomLinkedList<>();

        // Single pass over every vertex and its outgoing edges: O(V + E).
        // Each edge is "outgoing" for its source and "incoming" for its destination.
        for (Vertex source : graph.getVertices()) {
            for (Edge edge : source.getEdges()) {
                Vertex destination = edge.getDestination();

                if (source.getType() == EntityType.MERCHANT) {
                    AnomalyRecord record = findOrCreate(records, source.getId());
                    record.recordOutgoing(destination.getId(), edge.getWeight());
                }
                if (destination.getType() == EntityType.MERCHANT) {
                    AnomalyRecord record = findOrCreate(records, destination.getId());
                    record.recordIncoming(source.getId(), edge.getWeight());
                }
            }
        }

        markCycleMembership(graph, records);

        for (AnomalyRecord record : records) {
            record.computeAnomalyScore();
        }

        return records;
    }

    /** Finds an existing record for this merchant, or creates and appends a new one. */
    private AnomalyRecord findOrCreate(CustomLinkedList<AnomalyRecord> records, String merchantId) {
        for (AnomalyRecord record : records) {
            if (record.getMerchantId().equals(merchantId)) {
                return record;
            }
        }
        AnomalyRecord record = new AnomalyRecord(merchantId);
        records.addLast(record);
        return record;
    }

    /**
     * Runs DFS-based cycle detection once and flags any merchant appearing in
     * the detected cycle. Limitation: only the first cycle found is checked,
     * consistent with CycleDetector's single-cycle design (Phase 5).
     */
    private void markCycleMembership(Graph graph, CustomLinkedList<AnomalyRecord> records) {
        CycleDetector detector = new CycleDetector(graph);
        if (!detector.hasCycle()) {
            return;
        }
        for (Vertex v : detector.getCyclePath()) {
            if (v.getType() == EntityType.MERCHANT) {
                AnomalyRecord record = findOrCreate(records, v.getId());
                record.setCycleDetected(true);
            }
        }
    }
}