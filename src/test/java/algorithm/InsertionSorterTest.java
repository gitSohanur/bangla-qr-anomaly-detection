package algorithm;

import datastructure.CustomLinkedList;
import detection.AnomalyRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsertionSorterTest {

    /**
     * Builds an AnomalyRecord whose computed score matches a known value,
     * using AnomalyRecord's real scoring rules (already verified in
     * AnomalyRecordTest) — not a shortcut or a fake score.
     */
    private AnomalyRecord recordWithScore(String merchantId, int targetScore) {
        AnomalyRecord r = new AnomalyRecord(merchantId);
        switch (targetScore) {
            case 0 -> { }
            case 2 -> r.recordOutgoing("A001", 100);           // concentrated recipient (+2)
            case 3 -> { for (int i = 0; i < 5; i++) r.recordIncoming("U00" + i, 100); } // +3
            case 4 -> r.setCycleDetected(true);                 // +4
            case 7 -> {
                for (int i = 0; i < 5; i++) r.recordIncoming("U00" + i, 100); // +3
                r.setCycleDetected(true);                                     // +4
            }
            default -> throw new IllegalArgumentException("Unsupported test score: " + targetScore);
        }
        r.computeAnomalyScore();
        return r;
    }

    private String idsOf(CustomLinkedList<AnomalyRecord> list) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (AnomalyRecord r : list) {
            if (!first) sb.append(", ");
            sb.append(r.getMerchantId());
            first = false;
        }
        return sb.append("]").toString();
    }

    @Test
    void emptyList_sortsToEmptyList() {
        CustomLinkedList<AnomalyRecord> records = new CustomLinkedList<>();
        assertTrue(new InsertionSorter().sortByScoreDescending(records).isEmpty());
    }

    @Test
    void oneElement_sortsToSameSingleElement() {
        CustomLinkedList<AnomalyRecord> records = new CustomLinkedList<>();
        records.addLast(recordWithScore("M001", 3));
        CustomLinkedList<AnomalyRecord> sorted = new InsertionSorter().sortByScoreDescending(records);
        assertEquals(1, sorted.size());
        assertEquals("M001", sorted.peekFirst().getMerchantId());
    }

    @Test
    void alreadySortedDescending_staysInSameOrder() {
        CustomLinkedList<AnomalyRecord> records = new CustomLinkedList<>();
        records.addLast(recordWithScore("M001", 7));
        records.addLast(recordWithScore("M002", 4));
        records.addLast(recordWithScore("M003", 2));
        assertEquals("[M001, M002, M003]",
                idsOf(new InsertionSorter().sortByScoreDescending(records)));
    }

    @Test
    void reverseSorted_ascendingInput_getsFullyReversed() {
        CustomLinkedList<AnomalyRecord> records = new CustomLinkedList<>();
        records.addLast(recordWithScore("M001", 0));
        records.addLast(recordWithScore("M002", 2));
        records.addLast(recordWithScore("M003", 3));
        records.addLast(recordWithScore("M004", 4));
        records.addLast(recordWithScore("M005", 7));
        assertEquals("[M005, M004, M003, M002, M001]",
                idsOf(new InsertionSorter().sortByScoreDescending(records)));
    }

    @Test
    void unsortedMix_sortsCorrectly() {
        CustomLinkedList<AnomalyRecord> records = new CustomLinkedList<>();
        records.addLast(recordWithScore("M001", 3));
        records.addLast(recordWithScore("M002", 7));
        records.addLast(recordWithScore("M003", 0));
        records.addLast(recordWithScore("M004", 4));
        assertEquals("[M002, M004, M001, M003]",
                idsOf(new InsertionSorter().sortByScoreDescending(records)));
    }

    @Test
    void equalScores_preserveOriginalRelativeOrder() {
        // M001 and M002 both score 2. M001 appears first in the input,
        // so it must still appear before M002 in the sorted output.
        CustomLinkedList<AnomalyRecord> records = new CustomLinkedList<>();
        records.addLast(recordWithScore("M001", 2));
        records.addLast(recordWithScore("M002", 2));
        records.addLast(recordWithScore("M003", 7));
        assertEquals("[M003, M001, M002]",
                idsOf(new InsertionSorter().sortByScoreDescending(records)));
    }

    @Test
    void originalListIsNotMutated() {
        CustomLinkedList<AnomalyRecord> records = new CustomLinkedList<>();
        records.addLast(recordWithScore("M001", 0));
        records.addLast(recordWithScore("M002", 7));
        new InsertionSorter().sortByScoreDescending(records);
        assertEquals("[M001, M002]", idsOf(records)); // unchanged
    }
}