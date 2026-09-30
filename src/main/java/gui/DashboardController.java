package gui;

import algorithm.CycleDetector;
import data.SyntheticDataGenerator;
import datastructure.CustomLinkedList;
import detection.AnomalyRecord;
import graph.Graph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Transaction;
import pipeline.AnalysisPipeline;
import pipeline.GraphBuildResult;
import graph.Vertex;


/**
 * Builds and wires the dashboard. Contains no DSA logic -- every figure
 * shown comes from AnalysisPipeline (Phase 9B) or DashboardStats (pure
 * helpers, unit tested separately). Traceability:
 *   Transactions Processed -> GraphBuildResult.getProcessedCount()
 *   Merchants               -> ranked.size() (MerchantAnalyzer + Insertion Sort)
 *   Users                   -> DashboardStats.countUsers(graph)
 *   Cycle Detected           -> CycleDetector.getCyclePath() (YES/NO -- see
 *                              Phase 9C note on the single-cycle limitation)
 *   High-Anomaly Merchants  -> DashboardStats.countHighAnomaly(ranked)
 */
public class DashboardController {

    private final AnalysisPipeline pipeline = new AnalysisPipeline();

    private CustomLinkedList<Transaction> loadedTransactions;
    private Graph graph;
    private CycleDetector cycleDetector;
    private CustomLinkedList<AnomalyRecord> rankedRecords;

    private SummaryCard transactionsCard;
    private SummaryCard merchantsCard;
    private SummaryCard usersCard;
    private SummaryCard cycleCard;
    private SummaryCard highAnomalyCard;

    private Button loadButton;
    private Button runButton;
    private Button resetButton;
    private Button showGraphButton;
    private Button showMerchantsButton;

    private StackPane contentArea;
    private Label statusBar;

    public BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setTop(buildTop());
        root.setCenter(buildContentArea());
        root.setBottom(buildStatusBar());
        resetState(); // also serves as initial-state setup
        return root;
    }

    private VBox buildTop() {
        VBox top = new VBox(12, buildHeader(), buildSummaryCards(), buildControls());
        top.setPadding(new Insets(16));
        return top;
    }

    private VBox buildHeader() {
        Label title = new Label("Bangla QR Transaction Anomaly Detection");
        title.getStyleClass().add("title");
        Label subtitle = new Label("DSA-Based Synthetic Transaction Analysis");
        subtitle.getStyleClass().add("subtitle");
        VBox header = new VBox(4, title, subtitle);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private HBox buildSummaryCards() {
        transactionsCard = new SummaryCard("Transactions Processed");
        merchantsCard = new SummaryCard("Merchants");
        usersCard = new SummaryCard("Users");
        cycleCard = new SummaryCard("Cycle Detected");
        highAnomalyCard = new SummaryCard("High-Anomaly Merchants");
        return new HBox(12, transactionsCard.getNode(), merchantsCard.getNode(),
                usersCard.getNode(), cycleCard.getNode(), highAnomalyCard.getNode());
    }

    private HBox buildControls() {
        loadButton = new Button("Load Demo Dataset");
        runButton = new Button("Run Analysis");
        resetButton = new Button("Reset");
        showGraphButton = new Button("Show Graph");
        showMerchantsButton = new Button("Show Suspicious Merchants");

        loadButton.setOnAction(e -> onLoadDataset());
        runButton.setOnAction(e -> onRunAnalysis());
        resetButton.setOnAction(e -> resetState());
        showGraphButton.setOnAction(e -> onShowGraph());
        showMerchantsButton.setOnAction(e -> onShowSuspiciousMerchants());

        return new HBox(8, loadButton, runButton, resetButton, showGraphButton, showMerchantsButton);
    }

    private StackPane buildContentArea() {
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(16));
        return contentArea;
    }

    private Label buildStatusBar() {
        statusBar = new Label();
        statusBar.setPadding(new Insets(6, 16, 6, 16));
        statusBar.getStyleClass().add("status-bar");
        return statusBar;
    }

    private void onLoadDataset() {
        loadedTransactions = SyntheticDataGenerator.realistic();
        graph = null;
        cycleDetector = null;
        rankedRecords = null;

        transactionsCard.setValue(String.valueOf(loadedTransactions.size()));
        merchantsCard.setValue("--");
        usersCard.setValue("--");
        cycleCard.setValue("--");
        highAnomalyCard.setValue("--");

        runButton.setDisable(false);
        showGraphButton.setDisable(true);
        showMerchantsButton.setDisable(true);
        contentArea.getChildren().setAll(new Label(
                "Loaded " + loadedTransactions.size() + " transactions. Click Run Analysis to continue."));
        statusBar.setText("Loaded " + loadedTransactions.size() + " transactions (realistic dataset).");
    }

    private void onRunAnalysis() {
        if (loadedTransactions == null) {
            statusBar.setText("Load a dataset first.");
            return;
        }
        GraphBuildResult buildResult = pipeline.buildGraph(pipeline.enqueue(loadedTransactions));
        graph = buildResult.getGraph();
        cycleDetector = pipeline.detectCycles(graph);
        rankedRecords = pipeline.rank(pipeline.analyze(graph));

        transactionsCard.setValue(String.valueOf(buildResult.getProcessedCount()));
        merchantsCard.setValue(String.valueOf(rankedRecords.size()));
        usersCard.setValue(String.valueOf(DashboardStats.countUsers(graph)));
        cycleCard.setValue(cycleDetector.getCyclePath() != null ? "YES" : "NO");
        highAnomalyCard.setValue(String.valueOf(DashboardStats.countHighAnomaly(rankedRecords)));

        showGraphButton.setDisable(false);
        showMerchantsButton.setDisable(false);
        contentArea.getChildren().setAll(new Label(
                "Analysis complete. Use Show Graph or Show Suspicious Merchants below."));

        String duplicateNote = buildResult.getDuplicatesSkipped() > 0
                ? " (" + buildResult.getDuplicatesSkipped() + " duplicate transaction(s) skipped)"
                : "";
        statusBar.setText("Analysis complete: " + buildResult.getProcessedCount()
                + " transactions processed" + duplicateNote + ".");
    }

    private void onShowGraph() {
        CustomLinkedList<Vertex> cyclePath = cycleDetector != null ? cycleDetector.getCyclePath() : null;
        contentArea.getChildren().setAll(GraphView.build(graph, cyclePath));
    }

    private void onShowSuspiciousMerchants() {
        TextArea area = new TextArea(DashboardStats.formatRankedList(rankedRecords));
        area.setEditable(false);
        area.setStyle("-fx-font-family: 'Consolas', monospace;");
        contentArea.getChildren().setAll(area);
    }

    private void resetState() {
        loadedTransactions = null;
        graph = null;
        cycleDetector = null;
        rankedRecords = null;

        transactionsCard.setValue("--");
        merchantsCard.setValue("--");
        usersCard.setValue("--");
        cycleCard.setValue("--");
        highAnomalyCard.setValue("--");

        runButton.setDisable(true);
        showGraphButton.setDisable(true);
        showMerchantsButton.setDisable(true);

        contentArea.getChildren().setAll(new Label("Load the demo dataset to begin."));
        statusBar.setText("Ready.");
    }

    /** A small bordered box showing one summary figure and its caption. */
    private static class SummaryCard {
        private final VBox node;
        private final Label valueLabel;

        SummaryCard(String caption) {
            valueLabel = new Label("--");
            valueLabel.getStyleClass().add("card-value");
            Label captionLabel = new Label(caption);
            captionLabel.getStyleClass().add("card-caption");
            node = new VBox(4, valueLabel, captionLabel);
            node.getStyleClass().add("card");
            node.setAlignment(Pos.CENTER);
            node.setPadding(new Insets(12));
            node.setPrefWidth(150);
        }

        VBox getNode() { return node; }
        void setValue(String value) { valueLabel.setText(value); }
    }

}