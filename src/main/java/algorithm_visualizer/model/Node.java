package algorithm_visualizer.model;

import java.util.Objects;

/**
 * Represents a node in a graph.
 * <p>
 * Each node has a unique ID, a position for visualization, and an optional
 * label.
 * Nodes are equal if they have the same ID.
 * </p>
 */
public class Node {
    private final int id;
    private Position pos;
    private String label;

    /**
     * Creates a new node with the specified ID, position, and label.
     *
     * @param id    the unique node identifier (must be &ge; 0)
     * @param pos   the position of the node (must not be null)
     * @param label the display label (defaults to "N" + id if null or blank)
     * @throws IllegalArgumentException if id is negative
     */
    public Node(int id, Position pos, String label) {
        if (id < 0) {
            throw new IllegalArgumentException("id must be >= 0");
        }
        this.id = id;
        this.pos = Objects.requireNonNull(pos, "position must not be null");
        this.label = (label == null || label.isBlank()) ? "N" + this.id : label;
    }

    /** Returns the unique ID of this node. */
    public int getId() {
        return this.id;
    }

    /** Returns the display label of this node. */
    public String getLabel() {
        return this.label;
    }

    /** Returns the current position of this node. */
    public Position getPosition() {
        return this.pos;
    }

    /** Returns the x-coordinate of this node's position. */
    public double getX() {
        return this.pos.getX();
    }

    /** Returns the y-coordinate of this node's position. */
    public double getY() {
        return this.pos.getY();
    }

    /**
     * Moves this node to a new position.
     *
     * @param x the new x-coordinate
     * @param y the new y-coordinate
     */
    public void moveTo(double x, double y) {
        this.pos = new Position(x, y);
    }

    /**
     * Calculates the distance from this node to another node.
     *
     * @param other the other node
     * @return the Euclidean distance between the nodes
     */
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
