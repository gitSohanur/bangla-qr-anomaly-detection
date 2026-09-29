package detection;

import datastructure.CustomLinkedList;
import graph.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MerchantAnalyzerTest {

    @Test
    void emptyGraph_producesNoRecords() {
        Graph g = new Graph();
        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(g);
        assertEquals(0, records.size());
    }

    @Test
    void merchantAddedButNeverTransacts_producesNoRecord() {
        Graph g = new Graph();
        g.addVertex("M001"); // vertex exists, but no edges touch it
        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(g);
        assertEquals(0, records.size());
    }

    @Test
    void merchantWithOnlyIncoming_hasZeroOutgoingStats() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 500);
        AnomalyRecord r = new MerchantAnalyzer().analyze(g).peekFirst();
        assertEquals("M001", r.getMerchantId());
        assertEquals(1, r.getIncomingTransactionCount());
        assertEquals(0, r.getOutgoingTransactionCount());
    }

    @Test
    void merchantWithOnlyOutgoing_hasZeroIncomingStats() {
        Graph g = new Graph();
        g.addEdge("T001", "M001", "A001", 500);
        AnomalyRecord r = new MerchantAnalyzer().analyze(g).peekFirst();
        assertEquals(0, r.getIncomingTransactionCount());
        assertEquals(1, r.getOutgoingTransactionCount());
    }

    @Test
    void multipleMerchants_getSeparateRecords() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 500);
        g.addEdge("T002", "U002", "M002", 700);
        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(g);
        assertEquals(2, records.size());
    }

    @Test
    void nonMerchantVertices_neverProduceRecords() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "A001", 500); // no merchant involved at all
        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(g);
        assertEquals(0, records.size());
    }

    @Test
    void cycleInvolvingMerchants_flagsOnlyThoseMerchants() {
        // M001 -> A001 -> M007 -> M001
        Graph g = new Graph();
        g.addEdge("T001", "M001", "A001", 1000);
        g.addEdge("T002", "A001", "M007", 1000);
        g.addEdge("T003", "M007", "M001", 1000);

        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(g);
        boolean m001Flagged = false, m007Flagged = false;
        for (AnomalyRecord r : records) {
            if (r.getMerchantId().equals("M001")) m001Flagged = r.isCycleDetected();
            if (r.getMerchantId().equals("M007")) m007Flagged = r.isCycleDetected();
        }
        assertTrue(m001Flagged);
        assertTrue(m007Flagged);
    }

    @Test
    void graphWithNoCycle_noRecordHasCycleFlag() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 500);
        g.addEdge("T002", "M001", "A001", 500);
        for (AnomalyRecord r : new MerchantAnalyzer().analyze(g)) {
            assertFalse(r.isCycleDetected());
        }
    }

    @Test
    void suspiciousHubScenario_producesElevatedScore() {
        // Scenario B from the master prompt: 6 users -> M005 -> A001
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M005", 1000);
        g.addEdge("T002", "U002", "M005", 1200);
        g.addEdge("T003", "U003", "M005", 900);
        g.addEdge("T004", "U004", "M005", 1100);
        g.addEdge("T005", "U005", "M005", 1000);
        g.addEdge("T006", "U006", "M005", 800);
        g.addEdge("T007", "M005", "A001", 5800);

        AnomalyRecord r = new MerchantAnalyzer().analyze(g).peekFirst();
        assertEquals("M005", r.getMerchantId());
        assertEquals(6, r.getIncomingTransactionCount());
        assertEquals(6, r.getUniqueSenderCount()); // 6 distinct senders, so NOT sender-concentrated
        assertEquals(1, r.getUniqueRecipientCount()); // single cash-out account
        assertTrue(r.getAnomalyScore() >= 6); // at least MEDIUM
    }

    @Test
    void normalMerchantScenario_producesLowScore() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 300);
        g.addEdge("T002", "U002", "M001", 250);
        AnomalyRecord r = new MerchantAnalyzer().analyze(g).peekFirst();
        assertEquals(AnomalyStatus.NORMAL, r.getStatus());
    }
}