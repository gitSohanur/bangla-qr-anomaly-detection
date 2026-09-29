package pipeline;

import graph.Graph;

/** Holds the outcome of processing a queue of transactions into a graph. */
public class GraphBuildResult {
    private final Graph graph;
    private final int processedCount;
    private final int duplicatesSkipped;

    public GraphBuildResult(Graph graph, int processedCount, int duplicatesSkipped) {
        this.graph = graph;
        this.processedCount = processedCount;
        this.duplicatesSkipped = duplicatesSkipped;
    }

    public Graph getGraph() { return graph; }
    public int getProcessedCount() { return processedCount; }
    public int getDuplicatesSkipped() { return duplicatesSkipped; }
}