package gui;

import datastructure.CustomLinkedList;
import detection.AnomalyRecord;
import detection.AnomalyStatus;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.function.Function;

/**
 * Builds the merchant analysis table for "Show Suspicious Merchants".
 * Displays AnomalyRecord data (MerchantAnalyzer, Phase 6) in the exact
 * order InsertionSorter (Phase 7) produced. Column-header sorting is
 * disabled so the row order shown always matches the project's own
 * Insertion Sort result -- never JavaFX's own comparator-based sort.
 *
 * Traceability: table contents -> MerchantAnalyzer; row order -> Insertion Sort.
 */
public class MerchantTableView {

    private MerchantTableView() { }

    public static VBox build(CustomLinkedList<AnomalyRecord> ranked) {
        Label caption = new Label(
                "Merchant Analysis -- ranked by Insertion Sort (highest anomaly score first)");
        caption.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        TableView<AnomalyRecord> table = new TableView<>();
        table.setItems(toObservableList(ranked));
        table.getColumns().addAll(
                textColumn("Merchant ID", AnomalyRecord::getMerchantId),
                textColumn("Incoming Txns", r -> String.valueOf(r.getIncomingTransactionCount())),
                textColumn("Outgoing Txns", r -> String.valueOf(r.getOutgoingTransactionCount())),
                textColumn("Incoming Volume", r -> String.valueOf(r.getIncomingVolume())),
                textColumn("Outgoing Volume", r -> String.valueOf(r.getOutgoingVolume())),
                textColumn("Cycle Detected", r -> r.isCycleDetected() ? "YES" : "NO"),
                textColumn("Anomaly Score", r -> String.valueOf(r.getAnomalyScore())),
                statusColumn()
        );
        table.getColumns().forEach(c -> c.setSortable(false)); // preserve Insertion Sort's row order
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(AnomalyRecord record, boolean empty) {
                super.updateItem(record, empty);
                setStyle((!empty && record != null && record.isCycleDetected())
                        ? "-fx-background-color: #fdecea;" // matches GraphView's cycle-highlight color
                        : "");
            }
        });
        table.setPrefHeight(360);

        VBox container = new VBox(6, caption, table);
        container.setPadding(new Insets(4));
        return container;
    }

    private static TableColumn<AnomalyRecord, String> textColumn(
            String title, Function<AnomalyRecord, String> extractor) {
        TableColumn<AnomalyRecord, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(extractor.apply(data.getValue())));
        column.setPrefWidth(110);
        return column;
    }

    private static TableColumn<AnomalyRecord, String> statusColumn() {
        TableColumn<AnomalyRecord, String> column = new TableColumn<>("Status");
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus().toString()));
        column.setPrefWidth(130);
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    setStyle("-fx-text-fill: " + colorFor(status) + "; -fx-font-weight: bold;");
                }
            }
        });
        return column;
    }

    /** Pure, no JavaFX types in the signature -- unit tested directly (see MerchantTableViewColorTest). */
    static String colorFor(String statusLabel) {
        if (statusLabel.equals(AnomalyStatus.HIGH_ANOMALY.toString())) return "#d9534f";
        if (statusLabel.equals(AnomalyStatus.MEDIUM_ANOMALY.toString())) return "#e07b39";
        if (statusLabel.equals(AnomalyStatus.LOW_ANOMALY.toString())) return "#c9a227";
        return "#555555"; // NORMAL
    }

    private static ObservableList<AnomalyRecord> toObservableList(CustomLinkedList<AnomalyRecord> ranked) {
        ObservableList<AnomalyRecord> items = FXCollections.observableArrayList();
        for (AnomalyRecord r : ranked) {
            items.add(r);
        }
        return items;
    }
}