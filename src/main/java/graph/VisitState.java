package graph;

/** DFS visitation state for a vertex, following the classic CLRS "coloring" scheme. */
public enum VisitState {
    UNVISITED,  // not yet reached by this DFS run
    VISITING,   // currently on the recursion stack (an ancestor of the current vertex)
    VISITED     // fully explored — this vertex and everything reachable from it is done
}