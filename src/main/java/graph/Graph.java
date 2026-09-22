package graph;

import datastructure.CustomLinkedList;

/** A directed, weighted transaction graph, stored as an adjacency list. */
public class Graph {
    private final CustomLinkedList<Vertex> vertices;
    private final CustomLinkedList<String> processedTransactionIds;

    public Graph() {
        vertices = new CustomLinkedList<>();
        processedTransactionIds = new CustomLinkedList<>();
    }

    /** Adds a vertex if it doesn't already exist. Returns the existing or new vertex. */
    public Vertex addVertex(String id) {
        Vertex existing = getVertex(id);
        if (existing != null) {
            return existing;
        }
        Vertex vertex = new Vertex(id);
        vertices.addLast(vertex);
        return vertex;
    }

    /**
     * Adds a directed weighted edge, auto-creating endpoint vertices as needed.
     * @return true if added, false if this transactionId was already processed (duplicate)
     * @throws IllegalArgumentException on a self-loop or a non-positive weight
     */
    public boolean addEdge(String transactionId, String sourceId, String destinationId, long weight) {
        if (sourceId.equals(destinationId)) {
            throw new IllegalArgumentException("Self-loops are not supported: " + sourceId);
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Edge weight must be positive: " + weight);
        }
        if (processedTransactionIds.contains(transactionId)) {
            return false;
        }
        Vertex source = addVertex(sourceId);
        Vertex destination = addVertex(destinationId);
        source.addEdge(new Edge(transactionId, destination, weight));
        processedTransactionIds.addLast(transactionId);
        return true;
    }

    /** Removes the first edge from sourceId to destinationId. Returns false if not found. */
    public boolean removeEdge(String sourceId, String destinationId) {
        Vertex source = getVertex(sourceId);
        if (source == null) {
            return false;
        }
        return source.removeEdgeTo(destinationId);
    }

    /** Returns the vertex with this ID, or null if it doesn't exist. */
    public Vertex getVertex(String id) {
        for (Vertex v : vertices) {
            if (v.getId().equals(id)) {
                return v;
            }
        }
        return null;
    }

    public CustomLinkedList<Vertex> getVertices() {
        return vertices;
    }

    public int vertexCount() {
        return vertices.size();
    }

    public void displayGraph() {
        for (Vertex v : vertices) {
            StringBuilder sb = new StringBuilder(v.getId()).append(": ");
            for (Edge e : v.getEdges()) {
                sb.append(e).append("  ");
            }
            System.out.println(sb);
        }
    }
}