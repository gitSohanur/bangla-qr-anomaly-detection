package report;

import algorithm.CycleDetector;
import datastructure.CustomLinkedList;
import detection.AnomalyRecord;
import detection.MerchantAnalyzer;
import graph.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ReportGeneratorTest {

    @Test
    void printBanner_doesNotThrow() {
        assertDoesNotThrow(() -> new ReportGenerator().printBanner());
    }

    @Test
    void printCycleResult_noCycle_doesNotThrow() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 500);
        CycleDetector detector = new CycleDetector(g);
        boolean found = detector.hasCycle();
        assertDoesNotThrow(() -> new ReportGenerator().printCycleResult(found, detector));
    }

    @Test
    void printCycleResult_withCycle_doesNotThrow() {
        Graph g = new Graph();
        g.addEdge("T001", "M001", "A001", 1000);
        g.addEdge("T002", "A001", "M007", 1000);
        g.addEdge("T003", "M007", "M001", 1000);
        CycleDetector detector = new CycleDetector(g);
        boolean found = detector.hasCycle();
        assertDoesNotThrow(() -> new ReportGenerator().printCycleResult(found, detector));
    }

    @Test
    void printMerchantAnalysis_emptyList_doesNotThrow() {
        assertDoesNotThrow(() -> new ReportGenerator().printMerchantAnalysis(new CustomLinkedList<>()));
    }

    @Test
    void printMerchantAnalysis_populatedList_doesNotThrow() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 500);
        CustomLinkedList<AnomalyRecord> records = new MerchantAnalyzer().analyze(g);
        assertDoesNotThrow(() -> new ReportGenerator().printMerchantAnalysis(records));
    }

    @Test
    void printRankedResults_emptyList_doesNotThrow() {
        assertDoesNotThrow(() -> new ReportGenerator().printRankedResults(new CustomLinkedList<>()));
    }

    @Test
    void printDisclaimer_doesNotThrow() {
        assertDoesNotThrow(() -> new ReportGenerator().printDisclaimer());
    }
}