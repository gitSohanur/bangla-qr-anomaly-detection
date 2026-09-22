package graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VertexTest {

    @Test
    void newVertex_defaultsToUnvisitedWithNoParent() {
        Vertex v = new Vertex("U001");
        assertEquals(VisitState.UNVISITED, v.getVisitState());
        assertNull(v.getParent());
    }

    @Test
    void setVisitState_updatesState() {
        Vertex v = new Vertex("U001");
        v.setVisitState(VisitState.VISITING);
        assertEquals(VisitState.VISITING, v.getVisitState());
    }

    @Test
    void setParent_updatesParent() {
        Vertex a = new Vertex("U001");
        Vertex b = new Vertex("M001");
        b.setParent(a);
        assertEquals(a, b.getParent());
    }
}