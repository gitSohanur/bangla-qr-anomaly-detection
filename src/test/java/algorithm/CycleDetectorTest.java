package algorithm;

import graph.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CycleDetectorTest {

    @Test
    void emptyGraph_hasNoCycle() {
        Graph g = new Graph();
        CycleDetector d = new CycleDetector(g);
        assertFalse(d.hasCycle());
        assertNull(d.getCyclePath());
    }

    @Test
    void singleVertexNoEdges_hasNoCycle() {
        Graph g = new Graph();
        g.addVertex("U001");
        assertFalse(new CycleDetector(g).hasCycle());
    }

    @Test
    void linearChain_hasNoCycle() {
        // A -> B -> C
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 100);
        g.addEdge("T002", "M001", "A001", 100);
        assertFalse(new CycleDetector(g).hasCycle());
    }

    @Test
    void simpleCycle_isDetected() {
        // M01 -> A01 -> M07 -> M01
        Graph g = new Graph();
        g.addEdge("T001", "M001", "A001", 1000);
        g.addEdge("T002", "A001", "M007", 1000);
        g.addEdge("T003", "M007", "M001", 1000);

        CycleDetector d = new CycleDetector(g);
        assertTrue(d.hasCycle());
        assertEquals(3, d.getCycleLength());
    }

    @Test
    void simpleCycle_pathIsInCorrectOrder() {
        Graph g = new Graph();
        g.addEdge("T001", "M001", "A001", 1000);
        g.addEdge("T002", "A001", "M007", 1000);
        g.addEdge("T003", "M007", "M001", 1000);

        CycleDetector d = new CycleDetector(g);
        d.hasCycle();
        assertEquals("[M001(MERCHANT) -> A001(ACCOUNT) -> M007(MERCHANT) -> M001(MERCHANT)]",
                d.getCyclePath().toString());
    }

    @Test
    void branchingGraphWithCycleInOneBranch_isDetected() {
        // Hub: U001,U002,U003 -> M005 (no cycle here)
        // Separate cyclic branch: M001 -> A001 -> M001... wait no self-loop allowed,
        // use a 2-node cycle instead: M001 -> A001 -> M001 is disallowed too if it
        // revisits directly? No, A001 -> M001 is fine (different pair each direction).
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M005", 100);
        g.addEdge("T002", "U002", "M005", 100);
        g.addEdge("T003", "U003", "M005", 100);
        g.addEdge("T004", "M001", "A001", 500);
        g.addEdge("T005", "A001", "M001", 500); // closes a 2-vertex cycle

        assertTrue(new CycleDetector(g).hasCycle());
    }

    @Test
    void disconnectedComponents_noneWithCycle_hasNoCycle() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 100); // component 1
        g.addEdge("T002", "U002", "M002", 100); // component 2
        assertFalse(new CycleDetector(g).hasCycle());
    }

    @Test
    void twoIndependentCycles_atLeastOneIsDetected() {
        Graph g = new Graph();
        g.addEdge("T001", "M001", "A001", 100);
        g.addEdge("T002", "A001", "M001", 100); // cycle 1
        g.addEdge("T003", "M002", "A002", 200);
        g.addEdge("T004", "A002", "M002", 200); // cycle 2

        CycleDetector d = new CycleDetector(g);
        assertTrue(d.hasCycle());
        assertNotNull(d.getCyclePath());
        assertTrue(d.getCycleLength() >= 2);
    }

    @Test
    void hasCycle_calledTwice_stillDetectsCycleBothTimes() {
        // Regression test for forgetting to reset traversal state.
        Graph g = new Graph();
        g.addEdge("T001", "M001", "A001", 1000);
        g.addEdge("T002", "A001", "M007", 1000);
        g.addEdge("T003", "M007", "M001", 1000);

        CycleDetector d = new CycleDetector(g);
        assertTrue(d.hasCycle());
        assertTrue(d.hasCycle()); // must still be true, not silently false
    }

    @Test
    void hasCycle_falseBeforeCycleAdded_trueAfter() {
        Graph g = new Graph();
        g.addEdge("T001", "M001", "A001", 1000);
        g.addEdge("T002", "A001", "M007", 1000);
        CycleDetector d = new CycleDetector(g);
        assertFalse(d.hasCycle());

        g.addEdge("T003", "M007", "M001", 1000); // now closes the cycle
        assertTrue(d.hasCycle());
    }
}