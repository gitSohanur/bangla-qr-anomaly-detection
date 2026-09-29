package gui;

import data.SyntheticDataGenerator;
import datastructure.CustomLinkedList;
import detection.AnomalyRecord;
import graph.Graph;
import org.junit.jupiter.api.Test;
import pipeline.AnalysisPipeline;

import static org.junit.jupiter.api.Assertions.*;

class DashboardStatsTest {

    @Test
    void countUsers_hubDataset_matchesKnownUserCount() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        Graph graph = pipeline.buildGraph(pipeline.enqueue(SyntheticDataGenerator.hub())).getGraph();
        assertEquals(6, DashboardStats.countUsers(graph)); // U011-U016
    }

    @Test
    void countUsers_emptyGraph_isZero() {
        assertEquals(0, DashboardStats.countUsers(new Graph()));
    }

    @Test
    void countHighAnomaly_cycleDataset_countsOnlyHighStatus() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        Graph graph = pipeline.buildGraph(pipeline.enqueue(SyntheticDataGenerator.cycle())).getGraph();
        CustomLinkedList<AnomalyRecord> records = pipeline.analyze(graph);
        assertEquals(1, DashboardStats.countHighAnomaly(records)); // M007 only
    }

    @Test
    void countHighAnomaly_normalDataset_isZero() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        Graph graph = pipeline.buildGraph(pipeline.enqueue(SyntheticDataGenerator.normal())).getGraph();
        CustomLinkedList<AnomalyRecord> records = pipeline.analyze(graph);
        assertEquals(0, DashboardStats.countHighAnomaly(records));
    }

    @Test
    void formatRankedList_emptyList_showsPlaceholderMessage() {
        assertEquals("(no merchants to display)", DashboardStats.formatRankedList(new CustomLinkedList<>()));
    }

    @Test
    void formatRankedList_hubDataset_containsExpectedLine() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        Graph graph = pipeline.buildGraph(pipeline.enqueue(SyntheticDataGenerator.hub())).getGraph();
        CustomLinkedList<AnomalyRecord> ranked = pipeline.rank(pipeline.analyze(graph));
        assertTrue(DashboardStats.formatRankedList(ranked)
                .contains("1. M005   Score: 7   Status: MEDIUM ANOMALY"));
    }

    @Test
    void formatRankedList_cycleDataset_marksCycleMerchants() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        Graph graph = pipeline.buildGraph(pipeline.enqueue(SyntheticDataGenerator.cycle())).getGraph();
        CustomLinkedList<AnomalyRecord> ranked = pipeline.rank(pipeline.analyze(graph));
        assertTrue(DashboardStats.formatRankedList(ranked).contains("[CYCLE]"));
    }
}