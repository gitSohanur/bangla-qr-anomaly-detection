package data;

import algorithm.InsertionSorter;
import datastructure.CustomLinkedList;
import detection.AnomalyRecord;
import detection.AnomalyStatus;
import detection.MerchantAnalyzer;
import graph.Graph;
import model.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioResultsTest {

    /** Runs a dataset through Graph -> MerchantAnalyzer -> InsertionSorter. */
    private CustomLinkedList<AnomalyRecord> rank(CustomLinkedList<Transaction> dataset) {
        Graph graph = new Graph();
        for (Transaction t : dataset) {
            graph.addEdge(t.getTransactionId(), t.getSourceId(), t.getDestinationId(), t.getAmount());
        }
        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(graph);
        return new InsertionSorter().sortByScoreDescending(records);
    }

    private String summary(CustomLinkedList<AnomalyRecord> ranked) {
        StringBuilder sb = new StringBuilder();
        for (AnomalyRecord r : ranked) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(r.getMerchantId()).append('=').append(r.getAnomalyScore());
        }
        return sb.toString();
    }

    @Test
    void normalScenario_allMerchantsAreNormal() {
        CustomLinkedList<AnomalyRecord> ranked = rank(SyntheticDataGenerator.normal());
        assertEquals("M003=2, M001=0, M002=0", summary(ranked));
        for (AnomalyRecord r : ranked) {
            assertEquals(AnomalyStatus.NORMAL, r.getStatus());
        }
    }

    @Test
    void hubScenario_m005IsMediumAnomaly() {
        CustomLinkedList<AnomalyRecord> ranked = rank(SyntheticDataGenerator.hub());
        assertEquals("M005=7", summary(ranked));
        assertEquals(AnomalyStatus.MEDIUM_ANOMALY, ranked.peekFirst().getStatus());
    }

    @Test
    void cycleScenario_m007High_m008Medium() {
        CustomLinkedList<AnomalyRecord> ranked = rank(SyntheticDataGenerator.cycle());
        assertEquals("M007=11, M008=6", summary(ranked));
        assertEquals(AnomalyStatus.HIGH_ANOMALY, ranked.peekFirst().getStatus());
        for (AnomalyRecord r : ranked) {
            assertTrue(r.isCycleDetected()); // both merchants sit on the ring
        }
    }

    @Test
    void mixedScenario_fullRanking() {
        CustomLinkedList<AnomalyRecord> ranked = rank(SyntheticDataGenerator.mixed());
        assertEquals("M007=11, M005=7, M008=6, M003=2, M001=0, M002=0", summary(ranked));
    }
}