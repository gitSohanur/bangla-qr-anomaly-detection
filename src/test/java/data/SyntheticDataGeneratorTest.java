package data;

import datastructure.CustomLinkedList;
import graph.Graph;
import model.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class SyntheticDataGeneratorTest {

    private String dump(CustomLinkedList<Transaction> list) {
        StringBuilder sb = new StringBuilder();
        for (Transaction t : list) {
            sb.append(t).append('\n');
        }
        return sb.toString();
    }

    @Test
    void normal_hasEightTransactions() {
        assertEquals(8, SyntheticDataGenerator.normal().size());
    }

    @Test
    void hub_hasSevenTransactions() {
        assertEquals(7, SyntheticDataGenerator.hub().size());
    }

    @Test
    void cycle_hasEightTransactions() {
        assertEquals(8, SyntheticDataGenerator.cycle().size());
    }

    @Test
    void mixed_hasTwentyThreeTransactions() {
        assertEquals(23, SyntheticDataGenerator.mixed().size());
    }

    @Test
    void normal_firstTransactionIsAsDocumented() {
        Transaction t = SyntheticDataGenerator.normal().peekFirst();
        assertEquals("T001: U001 -> M001 | 300 | 2026-09-01T09:00", t.toString());
    }

    @Test
    void transactionIds_areSequentialInArrivalOrder() {
        int expected = 1;
        for (Transaction t : SyntheticDataGenerator.mixed()) {
            assertEquals(String.format(Locale.ROOT, "T%03d", expected), t.getTransactionId());
            expected++;
        }
        assertEquals(24, expected); // 23 transactions were checked
    }

    @Test
    void timestamps_increaseStrictlyInArrivalOrder() {
        LocalDateTime previous = null;
        for (Transaction t : SyntheticDataGenerator.mixed()) {
            if (previous != null) {
                assertTrue(t.getTimestamp().isAfter(previous));
            }
            previous = t.getTimestamp();
        }
    }

    @Test
    void generation_isDeterministic() {
        assertEquals(dump(SyntheticDataGenerator.mixed()), dump(SyntheticDataGenerator.mixed()));
    }

    @Test
    void mixed_hasNoDuplicateTransactionIds() {
        // Graph.addEdge returns false for a repeated transaction ID (Phase 4).
        Graph g = new Graph();
        for (Transaction t : SyntheticDataGenerator.mixed()) {
            assertTrue(g.addEdge(t.getTransactionId(), t.getSourceId(),
                    t.getDestinationId(), t.getAmount()));
        }
    }

    @Test
    void eachCall_returnsAnIndependentList() {
        CustomLinkedList<Transaction> first = SyntheticDataGenerator.normal();
        first.removeFirst();
        assertEquals(8, SyntheticDataGenerator.normal().size());
    }
}