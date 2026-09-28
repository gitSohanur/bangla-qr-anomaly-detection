package data;

import algorithm.CycleDetector;
import algorithm.InsertionSorter;
import datastructure.CustomLinkedList;
import detection.AnomalyRecord;
import detection.AnomalyStatus;
import detection.MerchantAnalyzer;
import graph.Graph;
import model.Transaction;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class RealisticDatasetTest {

    private Graph buildGraph(CustomLinkedList<Transaction> dataset, int[] acceptedCounter) {
        Graph g = new Graph();
        int accepted = 0;
        for (Transaction t : dataset) {
            if (g.addEdge(t.getTransactionId(), t.getSourceId(), t.getDestinationId(), t.getAmount())) {
                accepted++;
            }
        }
        acceptedCounter[0] = accepted;
        return g;
    }

    private AnomalyRecord findRecord(CustomLinkedList<AnomalyRecord> records, String merchantId) {
        for (AnomalyRecord r : records) {
            if (r.getMerchantId().equals(merchantId)) {
                return r;
            }
        }
        return null;
    }

    @Test
    void realistic_hasExpectedTransactionCount() {
        assertEquals(239, SyntheticDataGenerator.realistic().size());
    }

    @Test
    void realistic_isDeterministic() {
        CustomLinkedList<Transaction> first = SyntheticDataGenerator.realistic();
        CustomLinkedList<Transaction> second = SyntheticDataGenerator.realistic();
        assertEquals(first.size(), second.size());
        Iterator<Transaction> it1 = first.iterator();
        Iterator<Transaction> it2 = second.iterator();
        while (it1.hasNext()) {
            assertEquals(it1.next().toString(), it2.next().toString());
        }
    }

    @Test
    void realistic_duplicateTransactionId_isRejectedExactlyOnce() {
        int[] accepted = new int[1];
        buildGraph(SyntheticDataGenerator.realistic(), accepted);
        assertEquals(238, accepted[0]); // 239 records, 1 duplicate rejected
    }

    @Test
    void realistic_allFifteenMerchantsProduceRecords() {
        int[] accepted = new int[1];
        Graph g = buildGraph(SyntheticDataGenerator.realistic(), accepted);
        assertEquals(15, new MerchantAnalyzer().analyze(g).size());
    }

    @Test
    void realistic_safeNormalMerchant_isNormal() {
        int[] accepted = new int[1];
        Graph g = buildGraph(SyntheticDataGenerator.realistic(), accepted);
        AnomalyRecord m001 = findRecord(new MerchantAnalyzer().analyze(g), "M001");
        assertNotNull(m001);
        assertEquals(AnomalyStatus.NORMAL, m001.getStatus());
        assertFalse(m001.isCycleDetected());
    }

    @Test
    void realistic_boundaryMerchant_hitsExactlyLowAnomaly() {
        int[] accepted = new int[1];
        Graph g = buildGraph(SyntheticDataGenerator.realistic(), accepted);
        AnomalyRecord m007 = findRecord(new MerchantAnalyzer().analyze(g), "M007");
        assertNotNull(m007);
        assertEquals(5, m007.getIncomingTransactionCount());
        assertEquals(AnomalyStatus.LOW_ANOMALY, m007.getStatus());
    }

    @Test
    void realistic_hubMerchants_areMediumAnomalyAndNotCyclic() {
        int[] accepted = new int[1];
        Graph g = buildGraph(SyntheticDataGenerator.realistic(), accepted);
        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(g);
        for (String id : new String[]{"M008", "M009"}) {
            AnomalyRecord r = findRecord(records, id);
            assertNotNull(r);
            assertEquals(AnomalyStatus.MEDIUM_ANOMALY, r.getStatus());
            assertFalse(r.isCycleDetected());
        }
    }

    @Test
    void realistic_cycleDetector_findsOnePlantedCycle() {
        int[] accepted = new int[1];
        Graph g = buildGraph(SyntheticDataGenerator.realistic(), accepted);
        CycleDetector detector = new CycleDetector(g);
        assertTrue(detector.hasCycle());
        assertEquals(3, detector.getCycleLength());
    }

    @Test
    void realistic_firstCycleMerchants_areFlaggedAndMedium() {
        int[] accepted = new int[1];
        Graph g = buildGraph(SyntheticDataGenerator.realistic(), accepted);
        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(g);
        for (String id : new String[]{"M010", "M011"}) {
            AnomalyRecord r = findRecord(records, id);
            assertNotNull(r);
            assertTrue(r.isCycleDetected());
            assertEquals(AnomalyStatus.MEDIUM_ANOMALY, r.getStatus());
        }
    }

    @Test
    void realistic_otherCycleMerchants_areNotFlaggedThisRun() {
        // Documents the deliberate single-cycle limitation (Phases 5-6):
        // these ARE structurally cyclic, but CycleDetector already stopped
        // after finding the first cycle, so they are not flagged here.
        int[] accepted = new int[1];
        Graph g = buildGraph(SyntheticDataGenerator.realistic(), accepted);
        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(g);
        for (String id : new String[]{"M012", "M013", "M014", "M015"}) {
            AnomalyRecord r = findRecord(records, id);
            assertNotNull(r);
            assertFalse(r.isCycleDetected());
            assertEquals(AnomalyStatus.NORMAL, r.getStatus());
        }
    }

    @Test
    void realistic_insertionSort_producesNonIncreasingOrder() {
        int[] accepted = new int[1];
        Graph g = buildGraph(SyntheticDataGenerator.realistic(), accepted);
        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(g);
        CustomLinkedList<AnomalyRecord> ranked = new InsertionSorter().sortByScoreDescending(records);

        assertEquals(records.size(), ranked.size());
        int previousScore = Integer.MAX_VALUE;
        for (AnomalyRecord r : ranked) {
            assertTrue(r.getAnomalyScore() <= previousScore);
            previousScore = r.getAnomalyScore();
        }
    }

    @Test
    void realistic_topRankedMerchant_isAFlaggedOrHubMerchant() {
        int[] accepted = new int[1];
        Graph g = buildGraph(SyntheticDataGenerator.realistic(), accepted);
        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(g);
        CustomLinkedList<AnomalyRecord> ranked = new InsertionSorter().sortByScoreDescending(records);
        String topId = ranked.peekFirst().getMerchantId();
        assertTrue(topId.equals("M010") || topId.equals("M011")
                || topId.equals("M008") || topId.equals("M009"));
    }
}

