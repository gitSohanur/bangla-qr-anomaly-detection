package gui;

import datastructure.CustomLinkedList;
import graph.Vertex;

/**
 * Pure membership checks against a DFS-detected cycle path, used by
 * GraphView to decide what to highlight. No JavaFX imports, so this can
 * be unit tested without a running toolkit (same separation as
 * GraphLayout and DashboardStats).
 */
public class CycleHighlighter {

    private CycleHighlighter() { }

    /** True if the given vertex ID appears anywhere in the cycle path. cyclePath may be null. */
    public static boolean isCycleVertex(CustomLinkedList<Vertex> cyclePath, String vertexId) {
        if (cyclePath == null) {
            return false;
        }
        for (Vertex v : cyclePath) {
            if (v.getId().equals(vertexId)) {
                return true;
            }
        }
        return false;
    }

    /** True if (sourceId -> destinationId) is one of the consecutive steps in the cycle path. */
    public static boolean isCycleEdge(CustomLinkedList<Vertex> cyclePath, String sourceId, String destinationId) {
        if (cyclePath == null) {
            return false;
        }
        Vertex previous = null;
        for (Vertex v : cyclePath) {
            if (previous != null && previous.getId().equals(sourceId) && v.getId().equals(destinationId)) {
                return true;
            }
            previous = v;
        }
        return false;
    }
}