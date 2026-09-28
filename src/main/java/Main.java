import algorithm.CycleDetector;
import algorithm.InsertionSorter;
import data.SyntheticDataGenerator;
import datastructure.CustomLinkedList;
import datastructure.CustomQueue;
import detection.AnomalyRecord;
import detection.MerchantAnalyzer;
import graph.Graph;
import model.Transaction;
import report.ReportGenerator;

/**
 * Pipeline: Load -> Queue -> Process -> Graph -> DFS Cycle Detection
 *           -> Anomaly Scoring -> Insertion Sort -> Report
 */
public class Main {
    public static void main(String[] args) {
        ReportGenerator report = new ReportGenerator();
        report.printBanner();

        System.out.println("STEP 1: Loading synthetic transactions (mixed scenario)...");
        CustomLinkedList<Transaction> transactions = SyntheticDataGenerator.mixed();
        System.out.println("Loaded " + transactions.size() + " transactions.\n");

        System.out.println("STEP 2: Enqueuing transactions into the processing queue...");
        CustomQueue<Transaction> queue = new CustomQueue<>();
        for (Transaction t : transactions) {
            queue.enqueue(t);
        }
        System.out.println("Queue size: " + queue.size() + "\n");

        System.out.println("STEP 3: Processing queue -> building transaction graph...");
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
        System.out.println("Processed: " + processed + " transactions, "
                + duplicatesSkipped + " duplicates skipped.");
        System.out.println("Graph built: " + graph.vertexCount() + " vertices.\n");

        System.out.println("STEP 4: Running DFS-based cycle detection...");
        CycleDetector cycleDetector = new CycleDetector(graph);
        boolean cycleFound = cycleDetector.hasCycle();
        report.printCycleResult(cycleFound, cycleDetector);

        System.out.println("STEP 5: Analyzing merchant transaction patterns...");
        MerchantAnalyzer analyzer = new MerchantAnalyzer();
        CustomLinkedList<AnomalyRecord> records = analyzer.analyze(graph);
        System.out.println("Analyzed " + records.size() + " merchants.\n");

        System.out.println("STEP 6: Ranking merchants by anomaly score (Insertion Sort)...");
        InsertionSorter sorter = new InsertionSorter();
        CustomLinkedList<AnomalyRecord> ranked = sorter.sortByScoreDescending(records);
        System.out.println("Ranking complete.\n");

        report.printMerchantAnalysis(ranked);
        report.printRankedResults(ranked);
        report.printDisclaimer();
    }
}