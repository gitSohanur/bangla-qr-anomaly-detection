import algorithm.CycleDetector;
import data.SyntheticDataGenerator;
import datastructure.CustomLinkedList;
import datastructure.CustomQueue;
import detection.AnomalyRecord;
import graph.Graph;
import model.Transaction;
import pipeline.AnalysisPipeline;
import pipeline.GraphBuildResult;
import report.ReportGenerator;

/**
 * Pipeline: Load -> Queue -> Process -> Graph -> DFS Cycle Detection
 *           -> Anomaly Scoring -> Insertion Sort -> Report
 * Orchestration delegates to AnalysisPipeline, shared with the JavaFX GUI.
 */
public class Main {
    public static void main(String[] args) {
        ReportGenerator report = new ReportGenerator();
        AnalysisPipeline pipeline = new AnalysisPipeline();
        report.printBanner();

        System.out.println("STEP 1: Loading synthetic transactions (realistic dataset)...");
        CustomLinkedList<Transaction> transactions = SyntheticDataGenerator.realistic();
        System.out.println("Loaded " + transactions.size() + " transactions.\n");

        System.out.println("STEP 2: Enqueuing transactions into the processing queue...");
        CustomQueue<Transaction> queue = pipeline.enqueue(transactions);
        System.out.println("Queue size: " + queue.size() + "\n");

        System.out.println("STEP 3: Processing queue -> building transaction graph...");
        GraphBuildResult buildResult = pipeline.buildGraph(queue);
        Graph graph = buildResult.getGraph();
        System.out.println("Processed: " + buildResult.getProcessedCount() + " transactions, "
                + buildResult.getDuplicatesSkipped() + " duplicates skipped.");
        System.out.println("Graph built: " + graph.vertexCount() + " vertices.\n");

        System.out.println("STEP 4: Running DFS-based cycle detection...");
        CycleDetector cycleDetector = pipeline.detectCycles(graph);
        boolean cycleFound = cycleDetector.getCyclePath() != null;
        report.printCycleResult(cycleFound, cycleDetector);

        System.out.println("STEP 5: Analyzing merchant transaction patterns...");
        CustomLinkedList<AnomalyRecord> records = pipeline.analyze(graph);
        System.out.println("Analyzed " + records.size() + " merchants.\n");

        System.out.println("STEP 6: Ranking merchants by anomaly score (Insertion Sort)...");
        CustomLinkedList<AnomalyRecord> ranked = pipeline.rank(records);
        System.out.println("Ranking complete.\n");

        report.printMerchantAnalysis(ranked);
        report.printRankedResults(ranked);
        report.printDisclaimer();
    }
}