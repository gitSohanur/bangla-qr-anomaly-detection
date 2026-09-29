package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Minimal JavaFX entry point for Phase 9B -- confirms the JavaFX runtime
 * is correctly configured. The dashboard (summary cards, graph view,
 * cycle highlighting, merchant table) is built in Phases 9C-9F, and will
 * call AnalysisPipeline for every piece of data it shows -- never
 * duplicating DSA logic here.
 */
public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label placeholder = new Label(
                "Bangla QR Transaction Anomaly Detection\n"
                        + "JavaFX setup confirmed (Phase 9B).\n"
                        + "Dashboard arrives in Phase 9C.");
        placeholder.setStyle("-fx-font-size: 16px; -fx-text-alignment: center;");

        StackPane root = new StackPane(placeholder);
        root.setAlignment(Pos.CENTER);

        primaryStage.setTitle("Bangla QR Transaction Anomaly Detection");
        primaryStage.setScene(new Scene(root, 640, 400));
        primaryStage.show();
    }
}