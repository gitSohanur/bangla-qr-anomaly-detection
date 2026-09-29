package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point. Delegates all UI construction to DashboardController,
 * the only class that touches AnalysisPipeline / DashboardStats.
 */
public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        DashboardController controller = new DashboardController();
        BorderPane root = controller.buildRoot();

        Scene scene = new Scene(root, 960, 640);
        scene.getStylesheets().add(getClass().getResource("/gui/dashboard.css").toExternalForm());

        primaryStage.setTitle("Bangla-QR Transaction Anomaly Detection");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}