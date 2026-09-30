package pipeline;

import data.SyntheticDataGenerator;
import datastructure.CustomLinkedList;
import detection.AnomalyRecord;
import graph.Graph;
import gui.DashboardStats;
import model.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Confirms the CLI (Main.java) and the GUI (DashboardController) compute
 * identical figures from the same dataset, since both delegate to
 * AnalysisPipeline (Phase 9B). This test does not run Main or the GUI
 * directly -- it reproduces each one's exact call sequence and checks
 * the results match, which is what both classes actually execute.
 */
class CliGuiConsistencyTest {

    @Test
    void bothEntryPoints_useTheSameDataset() {
        // Main.java and DashboardController.onLoadDataset() must call the
        // same factory method. If either one is ever changed to load a
        // different dataset, this assertion is the only thing that would
        // catch the mismatch automatically.
        CustomLinkedList<Transaction> dataset = SyntheticDataGenerator.realistic();
        assertEquals(239, dataset.size());
    }

    @Test
    void processedAndDuplicateCounts_matchAcrossBothPaths() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        GraphBuildResult result = pipeline.buildGraph(
                pipeline.enqueue(SyntheticDataGenerator.realistic()));
        // Same numbers Main prints in STEP 3, and the same numbers
        // DashboardController.onRunAnalysis() feeds into the Transactions
        // Processed summary card.
        assertEquals(238, result.getProcessedCount());
        assertEquals(1, result.getDuplicatesSkipped());
    }

    @Test
    void merchantCountAndTopRanked_matchAcrossBothPaths() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        Graph graph = pipeline.buildGraph(pipeline.enqueue(SyntheticDataGenerator.realistic())).getGraph();
        CustomLinkedList<AnomalyRecord> ranked = pipeline.rank(pipeline.analyze(graph));

        // Same count shown in the CLI's "Analyzed N merchants" line and
        // the GUI's Merchants summary card.
        assertEquals(15, ranked.size());
        // Same top row the CLI prints first under RANKED MERCHANTS and
        // the GUI's MerchantTableView shows in its first row. The hub
        // merchants (score 7) outrank the cycle pair (score 6) -- see
        // Phase 9A's hand-computed table.
        String topId = ranked.peekFirst().getMerchantId();
        assertTrue(topId.equals("M008") || topId.equals("M009"));
        assertEquals(7, ranked.peekFirst().getAnomalyScore());
    }

    @Test
    void userCountAndHighAnomalyCount_matchDashboardCards() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        Graph graph = pipeline.buildGraph(pipeline.enqueue(SyntheticDataGenerator.realistic())).getGraph();
        CustomLinkedList<AnomalyRecord> ranked = pipeline.rank(pipeline.analyze(graph));

        // These two figures only appear in the GUI (Users and
        // High-Anomaly Merchants cards) -- pinning them here means any
        // future change to DashboardStats or the dataset that shifts
        // these numbers is caught immediately.
        assertEquals(107, DashboardStats.countUsers(graph));
        assertEquals(0, DashboardStats.countHighAnomaly(ranked)); // no HIGH_ANOMALY merchant in this dataset
    }

    @Test
    void cycleDetectionResult_matchesAcrossBothPaths() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        Graph graph = pipeline.buildGraph(pipeline.enqueue(SyntheticDataGenerator.realistic())).getGraph();
        var detector = pipeline.detectCycles(graph);

        // Same YES/NO the GUI's Cycle Detected card shows, and the same
        // path the CLI prints under "Cycle detected:".
        assertNotNull(detector.getCyclePath());
        assertEquals("M010 -> A003 -> M011 -> M010",
                algorithm.CycleDetector.formatPath(detector.getCyclePath()));
    }
}