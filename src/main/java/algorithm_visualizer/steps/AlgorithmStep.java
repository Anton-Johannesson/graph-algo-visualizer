package algorithm_visualizer.steps;

public class AlgorithmStep {
    private final StepType type;
    private final Integer nodeId;
    private final Integer fromId;
    private final Integer toId;
    private final Double value;
    private final String message;

    public AlgorithmStep(StepType type, Integer nodeId, Integer fromId, Integer toId, Double value, String message) {
        this.type = type;
        this.nodeId = nodeId;
        this.fromId = fromId;
        this.toId = toId;
        this.value = value;
        this.message = message;
    }

    public StepType getType() {
        return this.type;
    }

    public Integer getNodeId() {
        return this.nodeId;
    }

    public Integer getFromId() {
        return this.fromId;
    }

    public Integer getToId() {
        return this.toId;
    }

    public Double getValue() {
        return this.value;
    }

    public String getMessage() {
        return this.message;
    }

    public static AlgorithmStep visitNode(int nodeId) {
        return new AlgorithmStep(StepType.VISIT_NODE, nodeId, null, null, null, "Visit node " + nodeId);
    }

    public static AlgorithmStep setParent(int nodeId, int parentId) {
        return new AlgorithmStep(StepType.SET_PARENT, nodeId, parentId, nodeId, null,
                "Set parent of " + nodeId + " to " + parentId);
    }

    public static AlgorithmStep relaxEdge(int fromId, int toId, double newDist) {
        return new AlgorithmStep(StepType.RELAX_EDGE, null, fromId, toId, newDist,
                "Relax edge " + fromId + " -> " + toId + " (dist=" + newDist + ")");
    }

    public static AlgorithmStep updateFlow(int fromId, int toId, double delta) {
        return new AlgorithmStep(StepType.UPDATE_FLOW, null, fromId, toId, delta,
                "Update flow " + fromId + " -> " + toId + " (dist=" + delta + ")");
    }

    public static AlgorithmStep done(String message) {
        return new AlgorithmStep(StepType.DONE, null, null, null, null, message);
    }
}
