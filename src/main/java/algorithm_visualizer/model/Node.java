package algorithm_visualizer.model;

import java.util.Objects;

public class Node {
    private final int id;
    private Position pos;
    private String label;

    public Node(int id, Position pos, String label) {
        if (id < 0) {
            throw new IllegalArgumentException("id must be >= 0");
        }
        this.id = id;
        this.pos = Objects.requireNonNull(pos, "position must not be null");
        this.label = (label == null || label.isBlank()) ? "N" + this.id : label;
    }

    public int getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public Position getPosition() {
        return this.pos;
    }

    public double getX() {
        return this.pos.getX();
    }

    public double getY() {
        return this.pos.getY();
    }

    public void moveTo(double x, double y) {
        this.pos = new Position(x, y);
    }

    public double distanceTo(Node other) {
        Objects.requireNonNull(other, "other node must not be null");
        return this.pos.distanceTo(other.pos);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        Node other = (Node) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}
