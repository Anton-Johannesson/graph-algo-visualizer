package algorithm_visualizer.model;

/**
 * Represents a 2D position with x and y coordinates.
 * Used to define node positions in the graph for visualization.
 *
 * @param x the x-coordinate
 * @param y the y-coordinate
 */
public record Position(double x, double y) {

    /**
     * Calculates the Euclidean distance to another position.
     *
     * @param other the other position
     * @return the distance between this position and the other
     */
    public double distanceTo(Position other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
