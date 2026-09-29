package gui;

import data.SyntheticDataGenerator;
import graph.Graph;
import org.junit.jupiter.api.Test;
import pipeline.AnalysisPipeline;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GraphLayoutTest {

    private Graph buildGraph(datastructure.CustomLinkedList<model.Transaction> dataset) {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        return pipeline.buildGraph(pipeline.enqueue(dataset)).getGraph();
    }

    @Test
    void computePositions_emptyGraph_returnsEmptyMap() {
        assertTrue(GraphLayout.computePositions(new Graph()).isEmpty());
    }

    @Test
    void computePositions_hasOneEntryPerVertex() {
        Graph g = buildGraph(SyntheticDataGenerator.hub());
        assertEquals(g.vertexCount(), GraphLayout.computePositions(g).size());
    }

    @Test
    void computePositions_userVertex_isInUserColumn() {
        Graph g = buildGraph(SyntheticDataGenerator.hub());
        double[] pos = GraphLayout.computePositions(g).get("U011");
        assertEquals(GraphLayout.USER_X, pos[0]);
    }

    @Test
    void computePositions_merchantVertex_isInMerchantColumn() {
        Graph g = buildGraph(SyntheticDataGenerator.hub());
        double[] pos = GraphLayout.computePositions(g).get("M005");
        assertEquals(GraphLayout.MERCHANT_X, pos[0]);
    }

    @Test
    void computePositions_accountVertex_isInAccountColumn() {
        Graph g = buildGraph(SyntheticDataGenerator.hub());
        double[] pos = GraphLayout.computePositions(g).get("A001");
        assertEquals(GraphLayout.ACCOUNT_X, pos[0]);
    }

    @Test
    void computePositions_consecutiveSameTypeVertices_areSpacedApart() {
        Graph g = buildGraph(SyntheticDataGenerator.realistic());
        Map<String, double[]> positions = GraphLayout.computePositions(g);
        double y1 = positions.get("U001")[1];
        double y2 = positions.get("U002")[1];
        assertEquals(GraphLayout.USER_SPACING, y2 - y1, 0.001);
    }

    @Test
    void computePositions_isDeterministicAcrossCalls() {
        Graph g = buildGraph(SyntheticDataGenerator.mixed());
        Map<String, double[]> first = GraphLayout.computePositions(g);
        Map<String, double[]> second = GraphLayout.computePositions(g);
        for (String id : first.keySet()) {
            assertArrayEquals(first.get(id), second.get(id));
        }
    }

    @Test
    void computeContentHeight_scalesWithVertexCount() {
        Graph small = buildGraph(SyntheticDataGenerator.normal());
        Graph large = buildGraph(SyntheticDataGenerator.realistic());
        assertTrue(GraphLayout.computeContentHeight(large) > GraphLayout.computeContentHeight(small));
    }

    @Test
    void computeContentHeight_emptyGraph_isJustMargins() {
        assertEquals(GraphLayout.TOP_MARGIN * 2, GraphLayout.computeContentHeight(new Graph()));
    }
}