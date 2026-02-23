package algorithm_visualizer.model;

/**
 * Represents a directed edge in a graph.
 * <p>
 * An edge connects two nodes and can have a weight (for weighted graphs)
 * and a capacity/flow (for flow networks).
 * </p>
 */
public class Edge {
    private final int fromId;
    private final int toId;
    private double weight;
    private double capacity;
    private double flow;

    /**
     * Creates a simple weighted edge with no capacity.
     *
     * @param fromId the source node ID
     * @param toId   the target node ID
     * @param weight the edge weight (must be &ge; 0)
     */
    public Edge(int fromId, int toId, double weight) {
        this(fromId, toId, weight, 0.0);
    }

    /**
     * Creates an edge with weight and capacity (for flow networks).
     *
     * @param fromId   the source node ID
     * @param toId     the target node ID
     * @param weight   the edge weight (must be &ge; 0)
     * @param capacity the edge capacity (must be &ge; 0)
     * @throws IllegalArgumentException if weight or capacity is negative
     */
    public Edge(int fromId, int toId, double weight, double capacity) {
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be >= 0");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be >= 0");
        }
        this.fromId = fromId;
        this.toId = toId;
        this.weight = weight;
        this.capacity = capacity;
        this.flow = 0.0;
    }

    /** Returns the source node ID. */
    public int getFromId() {
        return fromId;
    }

    /** Returns the target node ID. */
    public int getToId() {
        return toId;
    }

    /** Returns the edge weight. */
    public double getWeight() {
        return weight;
    }

    /** Returns the edge capacity (for flow networks). */
    public double getCapacity() {
        return capacity;
    }

    /** Returns the current flow through this edge. */
    public double getFlow() {
        return flow;
    }

    /** Returns true if this edge has capacity (is part of a flow network). */
    public boolean isFlowEdge() {
        return capacity > 0;
    }

    /**
     * Returns the residual capacity of this edge.
     *
     * @return capacity minus current flow
     */
    public double residualCapacity() {
        return capacity - flow;
    }

    /**
     * Adds flow along this edge.
     *
     * @param delta the amount of flow to add (can be negative to cancel flow)
     * @throws IllegalArgumentException if resulting flow is out of bounds
     */
    public void addFlow(double delta) {
        double newFlow = flow + delta;
        if (newFlow < 0 || newFlow > capacity) {
            throw new IllegalArgumentException("flow must be in range [0, capacity]");
        }
        flow = newFlow;
    }

}
