package pipeline;

import algorithm.CycleDetector;
import algorithm.InsertionSorter;
import datastructure.CustomLinkedList;
import datastructure.CustomQueue;
import detection.AnomalyRecord;
import detection.MerchantAnalyzer;
import graph.Graph;
import model.Transaction;

/**
 * Shared orchestration over the existing DSA engine. Both the CLI (Main)
 * and the JavaFX controller (Phase 9C onward) call these same methods, so
 * neither duplicates any DSA logic. Each method is exactly one pipeline
 * stage, so a caller can show progress between stages rather than getting
 * one opaque result at the end.
 */
public class AnalysisPipeline {

    /** Stage 2: enqueue every transaction, preserving arrival order. */
    public CustomQueue<Transaction> enqueue(CustomLinkedList<Transaction> transactions) {
        CustomQueue<Transaction> queue = new CustomQueue<>();
        for (Transaction t : transactions) {
            queue.enqueue(t);
        }
        return queue;
    }

    /** Stage 3: drain the queue into a transaction graph, in FIFO order. */
    public GraphBuildResult buildGraph(CustomQueue<Transaction> queue) {
        Graph graph = new Graph();
        int processed = 0;
        int duplicatesSkipped = 0;
        while (!queue.isEmpty()) {
            Transaction t = queue.dequeue();
            boolean added = graph.addEdge(t.getTransactionId(), t.getSourceId(),
                    t.getDestinationId(), t.getAmount());
            if (added) {
                processed++;
            } else {
                duplicatesSkipped++;
            }
        }
        return new GraphBuildResult(graph, processed, duplicatesSkipped);
    }

    /** Stage 4: run DFS-based cycle detection once. */
    public CycleDetector detectCycles(Graph graph) {
        CycleDetector detector = new CycleDetector(graph);
        detector.hasCycle();
        return detector;
    }

    /** Stage 5: build per-merchant anomaly records (MerchantAnalyzer runs its own cycle pass -- see Phase 9). */
    public CustomLinkedList<AnomalyRecord> analyze(Graph graph) {
        return new MerchantAnalyzer().analyze(graph);
    }

    /** Stage 6: rank merchant records by anomaly score using Insertion Sort. */
    public CustomLinkedList<AnomalyRecord> rank(CustomLinkedList<AnomalyRecord> records) {
        return new InsertionSorter().sortByScoreDescending(records);
    }
}