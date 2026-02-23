package algorithm_visualizer.steps;

/**
 * Represents a single step in an algorithm's execution.
 * <p>
 * Algorithm steps are produced by algorithms and consumed by the GUI
 * for visualization. Each step has a type and optional associated data
 * such as node IDs, edge endpoints, values, and messages.
 * </p>
 */
public class AlgorithmStep {
    private final StepType type;
    private final Integer nodeId;
    private final Integer fromId;
    private final Integer toId;
    private final Double value;
    private final String message;

    /**
     * Creates a new algorithm step with all parameters.
     *
     * @param type    the step type
     * @param nodeId  the primary node ID (if applicable)
     * @param fromId  the source node ID for edge operations
     * @param toId    the target node ID for edge operations
     * @param value   a numeric value (e.g., distance, flow)
     * @param message a human-readable description
     */
    public AlgorithmStep(StepType type, Integer nodeId, Integer fromId, Integer toId, Double value, String message) {
        this.type = type;
        this.nodeId = nodeId;
        this.fromId = fromId;
        this.toId = toId;
        this.value = value;
        this.message = message;
    }

    /** Returns the type of this step. */
    public StepType getType() {
        return this.type;
    }

    /** Returns the primary node ID, or null if not applicable. */
    public Integer getNodeId() {
        return this.nodeId;
    }

    /** Returns the source node ID for edge operations, or null. */
    public Integer getFromId() {
        return this.fromId;
    }

    /** Returns the target node ID for edge operations, or null. */
    public Integer getToId() {
        return this.toId;
    }

    /** Returns the associated numeric value, or null. */
    public Double getValue() {
        return this.value;
    }

    /** Returns the human-readable message describing this step. */
    public String getMessage() {
        return this.message;
    }

    /**
     * Creates a VISIT_NODE step.
     *
     * @param nodeId the visited node ID
     * @return the step
     */
    public static AlgorithmStep visitNode(int nodeId) {
        return new AlgorithmStep(StepType.VISIT_NODE, nodeId, null, null, null, "Visit node " + nodeId);
    }

    /**
     * Creates a SET_PARENT step.
     *
     * @param nodeId   the child node ID
     * @param parentId the parent node ID
     * @return the step
     */
    public static AlgorithmStep setParent(int nodeId, int parentId) {
        return new AlgorithmStep(StepType.SET_PARENT, nodeId, parentId, nodeId, null,
                "Set parent of " + nodeId + " to " + parentId);
    }

    /**
     * Creates a RELAX_EDGE step for Dijkstra's algorithm.
     *
     * @param fromId  the source node ID
     * @param toId    the target node ID
     * @param newDist the new distance value
     * @return the step
     */
    public static AlgorithmStep relaxEdge(int fromId, int toId, double newDist) {
        return new AlgorithmStep(StepType.RELAX_EDGE, null, fromId, toId, newDist,
                "Relax edge " + fromId + " -> " + toId + " (dist=" + newDist + ")");
    }

    /**
     * Creates an UPDATE_FLOW step for max flow algorithms.
     *
     * @param fromId the source node ID
     * @param toId   the target node ID
     * @param delta  the flow delta
     * @return the step
     */
    public static AlgorithmStep updateFlow(int fromId, int toId, double delta) {
        return new AlgorithmStep(StepType.UPDATE_FLOW, null, fromId, toId, delta,
                "Update flow " + fromId + " -> " + toId + " (dist=" + delta + ")");
    }

    /**
     * Creates a DONE step to signal algorithm completion.
     *
     * @param message the completion message
     * @return the step
     */
    public static AlgorithmStep done(String message) {
        return new AlgorithmStep(StepType.DONE, null, null, null, null, message);
    }
}
