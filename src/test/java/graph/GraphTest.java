package graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void newGraph_isEmpty() {
        Graph g = new Graph();
        assertEquals(0, g.vertexCount());
    }

    @Test
    void addVertex_singleVertex_isStored() {
        Graph g = new Graph();
        g.addVertex("U001");
        assertEquals(1, g.vertexCount());
        assertNotNull(g.getVertex("U001"));
    }

    @Test
    void addVertex_calledTwice_doesNotDuplicate() {
        Graph g = new Graph();
        g.addVertex("U001");
        g.addVertex("U001");
        assertEquals(1, g.vertexCount());
    }

    @Test
    void getVertex_nonExisting_returnsNull() {
        Graph g = new Graph();
        assertNull(g.getVertex("U999"));
    }

    @Test
    void addEdge_autoCreatesBothVertices() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 500);
        assertEquals(2, g.vertexCount());
    }

    @Test
    void addEdge_multipleVertices_buildsCorrectStructure() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 500);
        g.addEdge("T002", "U002", "M001", 800);
        g.addEdge("T003", "M001", "A001", 1200);
        assertEquals(4, g.vertexCount()); // U001, U002, M001, A001
        assertEquals(1, g.getVertex("U001").getEdges().size());
        assertEquals(1, g.getVertex("U002").getEdges().size());
        assertEquals(1, g.getVertex("M001").getEdges().size());
    }

    @Test
    void addEdge_parallelEdgesBetweenSamePair_bothStored() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 500);
        g.addEdge("T002", "U001", "M001", 800);
        assertEquals(2, g.getVertex("U001").getEdges().size());
    }

    @Test
    void addEdge_duplicateTransactionId_rejected() {
        Graph g = new Graph();
        assertTrue(g.addEdge("T001", "U001", "M001", 500));
        assertFalse(g.addEdge("T001", "U001", "M001", 500));
        assertEquals(1, g.getVertex("U001").getEdges().size());
    }

    @Test
    void addEdge_selfLoop_throws() {
        Graph g = new Graph();
        assertThrows(IllegalArgumentException.class,
                () -> g.addEdge("T001", "M001", "M001", 500));
    }

    @Test
    void addEdge_zeroWeight_throws() {
        Graph g = new Graph();
        assertThrows(IllegalArgumentException.class,
                () -> g.addEdge("T001", "U001", "M001", 0));
    }

    @Test
    void addEdge_negativeWeight_throws() {
        Graph g = new Graph();
        assertThrows(IllegalArgumentException.class,
                () -> g.addEdge("T001", "U001", "M001", -100));
    }

    @Test
    void removeEdge_existingEdge_removesIt() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 500);
        assertTrue(g.removeEdge("U001", "M001"));
        assertEquals(0, g.getVertex("U001").getEdges().size());
    }

    @Test
    void removeEdge_nonExistingEdge_returnsFalse() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 500);
        assertFalse(g.removeEdge("U001", "A001")); // no such edge
    }

    @Test
    void removeEdge_nonExistingSourceVertex_returnsFalse() {
        Graph g = new Graph();
        assertFalse(g.removeEdge("U999", "M001"));
    }

    @Test
    void removeEdge_parallelEdges_removesOnlyOne() {
        Graph g = new Graph();
        g.addEdge("T001", "U001", "M001", 500);
        g.addEdge("T002", "U001", "M001", 800);
        g.removeEdge("U001", "M001");
        assertEquals(1, g.getVertex("U001").getEdges().size());
    }

    @Test
    void cycleShapedGraph_buildsCorrectly() {
        // M01 -> A01 -> M07 -> M01
        Graph g = new Graph();
        g.addEdge("T001", "M001", "A001", 1000);
        g.addEdge("T002", "A001", "M007", 1000);
        g.addEdge("T003", "M007", "M001", 1000);
        assertEquals(3, g.vertexCount());
        assertEquals("M001", g.getVertex("M007").getEdges().peekFirst().getDestination().getId());
    }

    @Test
    void vertexEquality_isById() {
        Vertex a = new Vertex("U001");
        Vertex b = new Vertex("U001");
        assertEquals(a, b);
    }

    @Test
    void displayGraph_doesNotThrowOnEmptyOrPopulatedGraph() {
        Graph g = new Graph();
        assertDoesNotThrow(g::displayGraph); // empty graph
        g.addEdge("T001", "U001", "M001", 500);
        assertDoesNotThrow(g::displayGraph); // populated graph
    }

}
    