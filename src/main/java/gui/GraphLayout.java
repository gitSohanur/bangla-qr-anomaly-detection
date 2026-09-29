package gui;

import graph.Graph;
import graph.Vertex;

import java.util.HashMap;
import java.util.Map;

/**
 * Computes fixed, deterministic screen coordinates for graph nodes:
 * Users in a left column, Merchants in a middle column, Accounts in a
 * right column, each spaced evenly top to bottom in graph-vertex order.
 * No layout algorithm -- every position is a direct formula, not
 * computed from graph structure.
 *
 * Contains no JavaFX imports, so it can be unit tested without a
 * running JavaFX toolkit (same separation as DashboardStats, Phase 9C).
 *
 * The HashMap returned is a presentation-only coordinate lookup -- it
 * holds screen pixel positions keyed by vertex ID, not project state.
 * This is the same category of exception already applied to CSV
 * parsing's local array (see TransactionLoader): a throwaway mechanism
 * needed to render the UI, not a chosen project data structure.
 */
public class GraphLayout {

    public static final double USER_X = 80;
    public static final double MERCHANT_X = 420;
    public static final double ACCOUNT_X = 760;
    public static final double TOP_MARGIN = 30;
    public static final double USER_SPACING = 14;
    public static final double MERCHANT_SPACING = 40;
    public static final double ACCOUNT_SPACING = 40;

    private GraphLayout() { }

    /** x, y position for every vertex in the graph, keyed by vertex ID. */
    public static Map<String, double[]> computePositions(Graph graph) {
        Map<String, double[]> positions = new HashMap<>();
        double userY = TOP_MARGIN;
        double merchantY = TOP_MARGIN;
        double accountY = TOP_MARGIN;

        for (Vertex v : graph.getVertices()) {
            double x;
            double y;
            switch (v.getType()) {
                case USER -> { x = USER_X; y = userY; userY += USER_SPACING; }
                case MERCHANT -> { x = MERCHANT_X; y = merchantY; merchantY += MERCHANT_SPACING; }
                case ACCOUNT -> { x = ACCOUNT_X; y = accountY; accountY += ACCOUNT_SPACING; }
                default -> throw new IllegalStateException("Unknown entity type: " + v.getType());
            }
            positions.put(v.getId(), new double[]{x, y});
        }
        return positions;
    }

    /** Total drawing height needed to fit every positioned node, with a bottom margin. */
    public static double computeContentHeight(Graph graph) {
        Map<String, double[]> positions = computePositions(graph);
        double maxY = TOP_MARGIN;
        for (double[] pos : positions.values()) {
            maxY = Math.max(maxY, pos[1]);
        }
        return maxY + TOP_MARGIN;
    }
}