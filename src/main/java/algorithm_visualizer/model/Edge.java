package algorithm_visualizer.model;

public class Edge {
    private final int fromId;
    private final int toId;
    private double weight;
    private double capacity;
    private double flow;

    public Edge(int fromId, int toId, double weight) {
        this(fromId, toId, weight, 0.0);
    }

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

    public int getFromId() {
        return fromId;
    }

    public int getToId() {
        return toId;
    }

    public double getWeight() {
        return weight;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getFlow() {
        return flow;
    }

    public boolean isFlowEdge() {
        return capacity > 0;
    }

    public double residualCapacity() {
        return capacity - flow;
    }

    public void addFlow(double delta) {
        double newFlow = flow + delta;
        if (newFlow < 0 || newFlow > capacity) {
            throw new IllegalArgumentException("flow must be in range [0, capacity]");
        }
        flow = newFlow;
    }

}
