package algorithm;

import datastructure.CustomLinkedList;
import detection.AnomalyRecord;

/**
 * Ranks merchant anomaly records by descending anomaly score using a
 * manually implemented Insertion Sort (no Collections.sort/Arrays.sort).
 */
public class InsertionSorter {

    /**
     * Returns a new list containing the same AnomalyRecord references as
     * `records`, ordered by anomaly score from highest to lowest. Equal
     * scores keep their original relative order (a stable sort). The
     * input list is left unchanged.
     */
    public CustomLinkedList<AnomalyRecord> sortByScoreDescending(CustomLinkedList<AnomalyRecord> records) {
        CustomLinkedList<AnomalyRecord> sorted = new CustomLinkedList<>();
        for (AnomalyRecord record : records) {
            insertInSortedPosition(sorted, record);
        }
        return sorted;
    }

    /**
     * Finds where `record` belongs in the already-sorted `sorted` list —
     * the first position whose current occupant has a strictly lower
     * score — and inserts it there. This is the "compare and shift" step
     * of Insertion Sort, adapted to a singly linked list.
     */
    private void insertInSortedPosition(CustomLinkedList<AnomalyRecord> sorted, AnomalyRecord record) {
        int index = 0;
        for (AnomalyRecord existing : sorted) {
            if (existing.getAnomalyScore() < record.getAnomalyScore()) {
                break;
            }
            index++;
        }
        sorted.insertAt(index, record);
    }
}