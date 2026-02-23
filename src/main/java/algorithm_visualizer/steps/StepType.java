package algorithm_visualizer.steps;

/**
 * Enumeration of algorithm step types for visualization.
 * <p>
 * Each step type represents a specific action that algorithms can perform,
 * allowing the GUI to animate and highlight the corresponding graph elements.
 * </p>
 */
public enum StepType {
    /** Marks a node as visited/processed. */
    VISIT_NODE,
    /** Sets the parent of a node in the traversal tree. */
    SET_PARENT,
    /** Relaxes an edge (updates distance in Dijkstra). */
    RELAX_EDGE,
    /** Highlights a path in the graph. */
    HIGHLIGHT_PATH,
    /** Updates flow along an edge (for max flow algorithms). */
    UPDATE_FLOW,
    /** Signals algorithm completion. */
    DONE

}
