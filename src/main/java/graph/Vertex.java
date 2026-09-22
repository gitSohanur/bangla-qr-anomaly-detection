package graph;

import datastructure.CustomLinkedList;
import model.EntityType;

/** A node in the transaction graph — a User, Merchant, or Account. */
public class Vertex {
    private final String id;
    private final EntityType type;
    private final CustomLinkedList<Edge> edges; // outgoing edges only

    // DFS traversal state — lives here rather than in an external set, because
    // without HashMap/HashSet, an external "visited" check would require an
    // O(V) linear scan per lookup. A field on the vertex gives O(1) instead.
    private VisitState visitState;
    private Vertex parent;

    public Vertex(String id) {
        this.id = id;
        this.type = EntityType.fromId(id); // validates the ID format too
        this.edges = new CustomLinkedList<>();
        this.visitState = VisitState.UNVISITED;
        this.parent = null;
    }

    public String getId() { return id; }
    public EntityType getType() { return type; }
    public CustomLinkedList<Edge> getEdges() { return edges; }

    public VisitState getVisitState() { return visitState; }
    public void setVisitState(VisitState visitState) { this.visitState = visitState; }
    public Vertex getParent() { return parent; }
    public void setParent(Vertex parent) { this.parent = parent; }

    /** Package-private: only Graph should be able to mutate a vertex's edges. */
    void addEdge(Edge edge) {
        edges.addLast(edge);
    }

    /** Removes the first outgoing edge to the given destination, if any. */
    boolean removeEdgeTo(String destinationId) {
        for (Edge edge : edges) {
            if (edge.getDestination().getId().equals(destinationId)) {
                return edges.remove(edge);
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vertex)) return false;
        return id.equals(((Vertex) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + "(" + type + ")";
    }
}