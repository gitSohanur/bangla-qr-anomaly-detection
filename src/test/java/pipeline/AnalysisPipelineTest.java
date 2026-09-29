package pipeline;

import algorithm.CycleDetector;
import data.SyntheticDataGenerator;
import datastructure.CustomLinkedList;
import datastructure.CustomQueue;
import detection.AnomalyRecord;
import model.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisPipelineTest {

    @Test
    void enqueue_preservesArrivalOrderAndSize() {
        CustomLinkedList<Transaction> data = SyntheticDataGenerator.normal();
        CustomQueue<Transaction> queue = new AnalysisPipeline().enqueue(data);
        assertEquals(data.size(), queue.size());
        assertEquals(data.peekFirst().getTransactionId(), queue.peek().getTransactionId());
    }

    @Test
    void buildGraph_processesAllTransactions_noDuplicatesInCleanDataset() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        CustomQueue<Transaction> queue = pipeline.enqueue(SyntheticDataGenerator.normal());
        GraphBuildResult result = pipeline.buildGraph(queue);
        assertEquals(8, result.getProcessedCount());
        assertEquals(0, result.getDuplicatesSkipped());
        assertTrue(queue.isEmpty());
    }

    @Test
    void buildGraph_detectsDuplicateFromRealisticDataset() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        CustomQueue<Transaction> queue = pipeline.enqueue(SyntheticDataGenerator.realistic());
        GraphBuildResult result = pipeline.buildGraph(queue);
        assertEquals(238, result.getProcessedCount());
        assertEquals(1, result.getDuplicatesSkipped());
    }

    @Test
    void detectCycles_cycleDataset_findsCycle() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        GraphBuildResult built = pipeline.buildGraph(pipeline.enqueue(SyntheticDataGenerator.cycle()));
        CycleDetector detector = pipeline.detectCycles(built.getGraph());
        assertNotNull(detector.getCyclePath());
        assertEquals(3, detector.getCycleLength());
    }

    @Test
    void detectCycles_normalDataset_findsNoCycle() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        GraphBuildResult built = pipeline.buildGraph(pipeline.enqueue(SyntheticDataGenerator.normal()));
        CycleDetector detector = pipeline.detectCycles(built.getGraph());
        assertNull(detector.getCyclePath());
    }

    @Test
    void analyzeAndRank_hubDataset_topResultIsM005() {
        AnalysisPipeline pipeline = new AnalysisPipeline();
        GraphBuildResult built = pipeline.buildGraph(pipeline.enqueue(SyntheticDataGenerator.hub()));
        CustomLinkedList<AnomalyRecord> records = pipeline.analyze(built.getGraph());
        CustomLinkedList<AnomalyRecord> ranked = pipeline.rank(records);
        assertEquals("M005", ranked.peekFirst().getMerchantId());
    }
}