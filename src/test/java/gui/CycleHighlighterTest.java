package gui;

import algorithm.CycleDetector;
import data.SyntheticDataGenerator;
import datastructure.CustomLinkedList;
import graph.Graph;
import graph.Vertex;
import org.junit.jupiter.api.Test;
import pipeline.AnalysisPipeline;

import static org.junit.jupiter.api.Assertions.*;

class CycleHighlighterTest {

    private CustomLinkedList<Vertex> cyclePathFrom(datastructure.CustomLinkedList<model.Transaction> dataset) {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        Graph g = pipeline.buildGraph(pipeline.enqueue(dataset)).getGraph();
        CycleDetector detector = pipeline.detectCycles(g);
        return detector.getCyclePath();
    }

    @Test
    void isCycleVertex_nullPath_isFalse() {
        assertFalse(CycleHighlighter.isCycleVertex(null, "M001"));
    }

    @Test
    void isCycleVertex_vertexInPath_isTrue() {
        CustomLinkedList<Vertex> path = cyclePathFrom(SyntheticDataGenerator.cycle());
        assertTrue(CycleHighlighter.isCycleVertex(path, "M007"));
    }

    @Test
    void isCycleVertex_vertexNotInPath_isFalse() {
        CustomLinkedList<Vertex> path = cyclePathFrom(SyntheticDataGenerator.cycle());
        assertFalse(CycleHighlighter.isCycleVertex(path, "U021"));
    }

    @Test
    void isCycleEdge_nullPath_isFalse() {
        assertFalse(CycleHighlighter.isCycleEdge(null, "M001", "A001"));
    }

    @Test
    void isCycleEdge_consecutivePairInPath_isTrue() {
        CustomLinkedList<Vertex> path = cyclePathFrom(SyntheticDataGenerator.cycle());
        assertTrue(CycleHighlighter.isCycleEdge(path, "M007", "A003"));
    }

    @Test
    void isCycleEdge_reversedPair_isFalse() {
        CustomLinkedList<Vertex> path = cyclePathFrom(SyntheticDataGenerator.cycle());
        assertFalse(CycleHighlighter.isCycleEdge(path, "A003", "M007"));
    }

    @Test
    void isCycleEdge_nonAdjacentPair_isFalse() {
        CustomLinkedList<Vertex> path = cyclePathFrom(SyntheticDataGenerator.cycle());
        assertFalse(CycleHighlighter.isCycleEdge(path, "U021", "A003"));
    }
}