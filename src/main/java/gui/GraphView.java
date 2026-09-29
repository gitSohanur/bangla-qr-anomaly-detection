package gui;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.util.Map;

/**
 * Draws the transaction graph using positions from GraphLayout (fixed,
 * manual, no layout algorithm). Reads only from Graph (Phase 4) --
 * traceability: Graph visualization -> Graph.
 */
public class GraphView {

    private static final double USER_RADIUS = 4;
    private static final double NODE_RADIUS = 10;
    private static final Color USER_COLOR = Color.web("#4a90d9");
    private static final Color MERCHANT_COLOR = Color.web("#e07b39");
    private static final Color ACCOUNT_COLOR = Color.web("#4caf50");
    private static final Color EDGE_COLOR = Color.web("#999999");

    private GraphView() { }

    public static ScrollPane build(Graph graph) {
        Pane canvas = new Pane();
        canvas.setPadding(new Insets(10));

        Map<String, double[]> positions = GraphLayout.computePositions(graph);

        for (Vertex v : graph.getVertices()) {
            double[] pos = positions.get(v.getId());
            drawNode(canvas, v, pos[0], pos[1]);
        }
        for (Vertex source : graph.getVertices()) {
            double[] from = positions.get(source.getId());
            for (Edge edge : source.getEdges()) {
                double[] to = positions.get(edge.getDestination().getId());
                drawEdge(canvas, from[0], from[1], to[0], to[1]);
            }
        }
        addLegend(canvas);

        double contentHeight = GraphLayout.computeContentHeight(graph);
        canvas.setPrefSize(GraphLayout.ACCOUNT_X + 160, contentHeight);

        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToWidth(false);
        return scrollPane;
    }

    private static void drawNode(Pane canvas, Vertex v, double x, double y) {
        double radius;
        Color color;
        boolean alwaysLabel;
        switch (v.getType()) {
            case USER -> { radius = USER_RADIUS; color = USER_COLOR; alwaysLabel = false; }
            case MERCHANT -> { radius = NODE_RADIUS; color = MERCHANT_COLOR; alwaysLabel = true; }
            case ACCOUNT -> { radius = NODE_RADIUS; color = ACCOUNT_COLOR; alwaysLabel = true; }
            default -> throw new IllegalStateException("Unknown entity type: " + v.getType());
        }

        Circle node = new Circle(x, y, radius, color);
        Tooltip.install(node, new Tooltip(v.getId() + " (" + v.getType() + ")"));
        canvas.getChildren().add(node);

        if (alwaysLabel) {
            Text label = new Text(x + radius + 4, y + 4, v.getId());
            label.setStyle("-fx-font-size: 10px;");
            canvas.getChildren().add(label);
        }
    }

    private static void drawEdge(Pane canvas, double x1, double y1, double x2, double y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(EDGE_COLOR);
        line.setStrokeWidth(0.6);
        line.setOpacity(0.5);
        canvas.getChildren().add(line);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double arrowLength = 6;
        double ex = x2 - Math.cos(angle) * NODE_RADIUS; // stop just short of the destination node
        double ey = y2 - Math.sin(angle) * NODE_RADIUS;

        Polygon arrowHead = new Polygon(
                ex, ey,
                ex - arrowLength * Math.cos(angle - Math.PI / 6), ey - arrowLength * Math.sin(angle - Math.PI / 6),
                ex - arrowLength * Math.cos(angle + Math.PI / 6), ey - arrowLength * Math.sin(angle + Math.PI / 6)
        );
        arrowHead.setFill(EDGE_COLOR);
        arrowHead.setOpacity(0.6);
        canvas.getChildren().add(arrowHead);
    }

    private static void addLegend(Pane canvas) {
        addLegendItem(canvas, 10, 5, USER_COLOR, "User");
        addLegendItem(canvas, 90, 5, MERCHANT_COLOR, "Merchant");
        addLegendItem(canvas, 190, 5, ACCOUNT_COLOR, "Account");
    }

    private static void addLegendItem(Pane canvas, double x, double y, Color color, String label) {
        Circle dot = new Circle(x, y, 5, color);
        Text text = new Text(x + 10, y + 4, label);
        text.setStyle("-fx-font-size: 10px;");
        canvas.getChildren().addAll(dot, text);
    }
}