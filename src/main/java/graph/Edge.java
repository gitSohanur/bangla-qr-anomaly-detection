package graph;

/** A directed, weighted transaction link from one vertex to another. */
public class Edge {
    private final String transactionId;
    private final Vertex destination;
    private final long weight; // transaction amount

    public Edge(String transactionId, Vertex destination, long weight) {
        this.transactionId = transactionId;
        this.destination = destination;
        this.weight = weight;
    }

    public String getTransactionId() { return transactionId; }
    public Vertex getDestination() { return destination; }
    public long getWeight() { return weight; }

    @Override
    public String toString() {
        return "--" + weight + " (" + transactionId + ")--> " + destination.getId();
    }
}