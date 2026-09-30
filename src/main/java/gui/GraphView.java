package gui;

import datastructure.CustomLinkedList;
import graph.Edge;
import graph.Graph;
import graph.Vertex;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import model.EntityType;

import java.util.Map;

/**
 * Draws the transaction graph using positions from GraphLayout (fixed,
 * manual, no layout algorithm) and highlights the DFS-detected cycle
 * path, if any, using CycleHighlighter. Reads only from Graph (Phase 4)
 * and CycleDetector's already-computed path (Phase 5) -- traceability:
 *   Graph visualization -> Graph
 *   Cycle highlight      -> DFS Cycle Detector
 */
public class GraphView {

    private static final double USER_RADIUS = 4;
    private static final double NODE_RADIUS = 10;
    private static final Color USER_COLOR = Color.web("#4a90d9");
    private static final Color MERCHANT_COLOR = Color.web("#e07b39");
    private static final Color ACCOUNT_COLOR = Color.web("#4caf50");
    private static final Color EDGE_COLOR = Color.web("#999999");
    private static final Color CYCLE_COLOR = Color.web("#d9534f");

    private GraphView() { }

    /** cyclePath may be null (no cycle was detected). */
    public static VBox build(Graph graph, CustomLinkedList<Vertex> cyclePath) {
        Pane canvas = new Pane();
        canvas.setPadding(new Insets(10));

        Map<String, double[]> positions = GraphLayout.computePositions(graph);

        // Edges first, so node circles are drawn on top and cleanly
        // cover each edge's start/end point. Non-cycle edges first,
        // cycle edges last, so highlighted edges render above the rest.
        for (Vertex source : graph.getVertices()) {
            double[] from = positions.get(source.getId());
            for (Edge edge : source.getEdges()) {
                if (!CycleHighlighter.isCycleEdge(cyclePath, source.getId(), edge.getDestination().getId())) {
                    double[] to = positions.get(edge.getDestination().getId());
                    drawEdge(canvas, from[0], from[1], to[0], to[1], edge.getDestination().getType(), false);
                }
            }
        }
        for (Vertex source : graph.getVertices()) {
            double[] from = positions.get(source.getId());
            for (Edge edge : source.getEdges()) {
                if (CycleHighlighter.isCycleEdge(cyclePath, source.getId(), edge.getDestination().getId())) {
                    double[] to = positions.get(edge.getDestination().getId());
                    drawEdge(canvas, from[0], from[1], to[0], to[1], edge.getDestination().getType(), true);
                }
            }
        }

        for (Vertex v : graph.getVertices()) {
            double[] pos = positions.get(v.getId());
            boolean highlighted = CycleHighlighter.isCycleVertex(cyclePath, v.getId());
            drawNode(canvas, v, pos[0], pos[1], highlighted);
        }

        addLegend(canvas, cyclePath != null);

        double contentHeight = GraphLayout.computeContentHeight(graph);
        canvas.setPrefSize(GraphLayout.ACCOUNT_X + 160, contentHeight);

        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToWidth(false);

        Label banner = buildBanner(cyclePath);

        VBox container = new VBox(8, banner, scrollPane);
        container.setPadding(new Insets(4));
        return container;
    }

    private static Label buildBanner(CustomLinkedList<Vertex> cyclePath) {
        Label banner = new Label();
        if (cyclePath != null) {
            banner.setText("Cycle Detected: " + algorithm.CycleDetector.formatPath(cyclePath));
            banner.setStyle("-fx-text-fill: #d9534f; -fx-font-weight: bold; -fx-font-size: 13px;");
        } else {
            banner.setText("No cycle detected.");
            banner.setStyle("-fx-text-fill: #555555; -fx-font-size: 13px;");
        }
        return banner;
    }

    private static double radiusForType(EntityType type) {
        return type == EntityType.USER ? USER_RADIUS : NODE_RADIUS;
    }

    private static Color colorForType(EntityType type) {
        return switch (type) {
            case USER -> USER_COLOR;
            case MERCHANT -> MERCHANT_COLOR;
            case ACCOUNT -> ACCOUNT_COLOR;
        };
    }

    private static void drawNode(Pane canvas, Vertex v, double x, double y, boolean highlighted) {
        double radius = radiusForType(v.getType());
        Circle node = new Circle(x, y, radius, colorForType(v.getType()));
        if (highlighted) {
            node.setStroke(CYCLE_COLOR);
            node.setStrokeWidth(2.5);
        }
        Tooltip.install(node, new Tooltip(v.getId() + " (" + v.getType() + ")"));
        canvas.getChildren().add(node);

        boolean showLabel = highlighted || v.getType() != EntityType.USER;
        if (showLabel) {
            Text label = new Text(x + radius + 4, y + 4, v.getId());
            label.setStyle(highlighted
                    ? "-fx-font-size: 10px; -fx-font-weight: bold; -fx-fill: #d9534f;"
                    : "-fx-font-size: 10px;");
            canvas.getChildren().add(label);
        }
    }

    private static void drawEdge(Pane canvas, double x1, double y1, double x2, double y2,
                                 EntityType destinationType, boolean highlighted) {
        Color color = highlighted ? CYCLE_COLOR : EDGE_COLOR;
        double strokeWidth = highlighted ? 2.0 : 0.6;
        double opacity = highlighted ? 0.9 : 0.5;

        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(color);
        line.setStrokeWidth(strokeWidth);
        line.setOpacity(opacity);
        canvas.getChildren().add(line);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double arrowLength = highlighted ? 9 : 6;
        double pullBack = radiusForType(destinationType);
        double ex = x2 - Math.cos(angle) * pullBack;
        double ey = y2 - Math.sin(angle) * pullBack;

        Polygon arrowHead = new Polygon(
                ex, ey,
                ex - arrowLength * Math.cos(angle - Math.PI / 6), ey - arrowLength * Math.sin(angle - Math.PI / 6),
                ex - arrowLength * Math.cos(angle + Math.PI / 6), ey - arrowLength * Math.sin(angle + Math.PI / 6)
        );
        arrowHead.setFill(color);
        arrowHead.setOpacity(opacity + 0.1);
        canvas.getChildren().add(arrowHead);
    }

    private static void addLegend(Pane canvas, boolean showCycleSwatch) {
        addLegendDot(canvas, 10, 5, USER_COLOR, "User");
        addLegendDot(canvas, 90, 5, MERCHANT_COLOR, "Merchant");
        addLegendDot(canvas, 190, 5, ACCOUNT_COLOR, "Account");
        if (showCycleSwatch) {
            addLegendDot(canvas, 290, 5, CYCLE_COLOR, "Cycle");
        }
    }

    private static void addLegendDot(Pane canvas, double x, double y, Color color, String label) {
        Circle dot = new Circle(x, y, 5, color);
        Text text = new Text(x + 10, y + 4, label);
        text.setStyle("-fx-font-size: 10px;");
        canvas.getChildren().addAll(dot, text);
    }
}