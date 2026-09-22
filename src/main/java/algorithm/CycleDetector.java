package algorithm;

import datastructure.CustomLinkedList;
import graph.Edge;
import graph.Graph;
import graph.Vertex;
import graph.VisitState;

/**
 * Detects directed cycles in a transaction graph using recursive DFS.
 * DFS traversal and cycle detection are treated as one algorithmic component.
 */
public class CycleDetector {
    private final Graph graph;
    private CustomLinkedList<Vertex> cyclePath; // set only when a cycle is found

    public CycleDetector(Graph graph) {
        this.graph = graph;
    }

    /** Runs DFS from every unvisited vertex until a cycle is found or the graph is exhausted. */
    public boolean hasCycle() {
        graph.resetTraversalState();
        cyclePath = null;
        for (Vertex v : graph.getVertices()) {
            if (v.getVisitState() == VisitState.UNVISITED) {
                if (dfs(v)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(Vertex current) {
        current.setVisitState(VisitState.VISITING);
        for (Edge edge : current.getEdges()) {
            Vertex neighbor = edge.getDestination();
            if (neighbor.getVisitState() == VisitState.VISITING) {
                // Back edge: neighbor is still "in progress" on this recursion path,
                // so current -> neighbor closes a directed cycle.
                buildCyclePath(current, neighbor);
                return true;
            }
            if (neighbor.getVisitState() == VisitState.UNVISITED) {
                neighbor.setParent(current);
                if (dfs(neighbor)) {
                    return true;
                }
            }
        }
        current.setVisitState(VisitState.VISITED);
        return false;
    }

    /** Walks parent pointers from `current` back up to `ancestor`, then closes the cycle. */
    private void buildCyclePath(Vertex current, Vertex ancestor) {
        CustomLinkedList<Vertex> path = new CustomLinkedList<>();
        Vertex v = current;
        while (true) {
            path.addFirst(v); // prepend, so the final order reads start -> ... -> end
            if (v.equals(ancestor)) {
                break;
            }
            v = v.getParent();
        }
        path.addLast(ancestor); // repeat the start vertex to show the cycle closing
        this.cyclePath = path;
    }

    /** The vertices of the most recently found cycle, e.g. [M001, A001, M007, M001]. Null if none found. */
    public CustomLinkedList<Vertex> getCyclePath() {
        return cyclePath;
    }

    /** Number of edges in the detected cycle (0 if none found). */
    public int getCycleLength() {
        return cyclePath == null ? 0 : cyclePath.size() - 1;
    }
}