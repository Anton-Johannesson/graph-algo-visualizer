package algorithm_visualizer.gui.animation;

import javafx.scene.paint.Color;

/**
 * Defines visual styles for nodes during algorithm visualization.
 * <p>
 * Each style specifies fill and text colors to represent different node states.
 * </p>
 */
public enum NodeStyle {
    /** Default unvisited state. */
    DEFAULT(Color.LIGHTGRAY, Color.BLACK),
    /** Node is in the queue/frontier. */
    QUEUED(Color.YELLOW, Color.BLACK),
    /** Currently processing this node. */
    CURRENT(Color.DODGERBLUE, Color.WHITE),
    /** Node has been fully processed. */
    VISITED(Color.LIGHTGREEN, Color.BLACK),
    /** Node is on the computed path. */
    PATH(Color.ORANGE, Color.BLACK),
    /** Source node for the algorithm. */
    SOURCE(Color.LIMEGREEN, Color.WHITE),
    /** Sink/target node for the algorithm. */
    SINK(Color.CRIMSON, Color.WHITE);

    private final Color fillColor;
    private final Color textColor;

    NodeStyle(Color fillColor, Color textColor) {
        this.fillColor = fillColor;
        this.textColor = textColor;
    }

    /** Returns the fill color for this node style. */
    public Color getFillColor() {
        return fillColor;
    }

    /** Returns the text color for this node style. */
    public Color getTextColor() {
        return textColor;
    }

    /** Returns the default edge color. */
    public static Color getEdgeDefaultColor() {
        return Color.DARKGRAY;
    }

    /** Returns the edge highlight color. */
    public static Color getEdgeHighlightColor() {
        return Color.DODGERBLUE;
    }

    /** Returns the path edge color. */
    public static Color getEdgePathColor() {
        return Color.ORANGE;
    }

    /** Returns the flow edge color. */
    public static Color getEdgeFlowColor() {
        return Color.PURPLE;
    }
}
