package algorithm_visualizer.gui.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import algorithm_visualizer.gui.animation.NodeStyle;
import algorithm_visualizer.model.Edge;
import algorithm_visualizer.model.Graph;
import algorithm_visualizer.model.Node;
import algorithm_visualizer.model.Position;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * A JavaFX Canvas for rendering graphs and algorithm visualizations.
 * <p>
 * Draws nodes and edges with support for highlighting, styling, and
 * displaying algorithm state (distances, flows, paths).
 * </p>
 */
public class GraphCanvas extends Canvas {

    private static final double NODE_RADIUS = 25.0;
    private static final double ARROW_SIZE = 10.0;

    private Graph graph;
    private final Map<Integer, NodeStyle> nodeStyles = new HashMap<>();
    private final Set<EdgeKey> highlightedEdges = new HashSet<>();
    private final Set<EdgeKey> pathEdges = new HashSet<>();
    private final Map<EdgeKey, Double> edgeFlows = new HashMap<>();
    private final Map<Integer, Double> nodeDistances = new HashMap<>();

    private double scale = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;

    /**
     * Creates a new GraphCanvas with the specified dimensions.
     *
     * @param width  the canvas width
     * @param height the canvas height
     */
    public GraphCanvas(double width, double height) {
        super(width, height);
    }

    /**
     * Sets the graph to display.
     *
     * @param graph the graph to render
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
        resetVisualization();
        draw();
    }

    /** Returns the currently displayed graph. */
    public Graph getGraph() {
        return graph;
    }

    /** Clears all visualization state (styles, highlights, etc.). */
    public void resetVisualization() {
        nodeStyles.clear();
        highlightedEdges.clear();
        pathEdges.clear();
        edgeFlows.clear();
        nodeDistances.clear();
    }

    /**
     * Sets the visual style for a node.
     *
     * @param nodeId the node ID
     * @param style  the style to apply
     */
    public void setNodeStyle(int nodeId, NodeStyle style) {
        nodeStyles.put(nodeId, style);
        draw();
    }

    /**
     * Highlights an edge.
     *
     * @param fromId the source node ID
     * @param toId   the target node ID
     */
    public void highlightEdge(int fromId, int toId) {
        highlightedEdges.add(new EdgeKey(fromId, toId));
        draw();
    }

    /**
     * Marks an edge as part of the path.
     *
     * @param fromId the source node ID
     * @param toId   the target node ID
     */
    public void setPathEdge(int fromId, int toId) {
        pathEdges.add(new EdgeKey(fromId, toId));
        draw();
    }

    /**
     * Sets the flow value for an edge.
     *
     * @param fromId the source node ID
     * @param toId   the target node ID
     * @param flow   the flow value
     */
    public void setEdgeFlow(int fromId, int toId, double flow) {
        edgeFlows.put(new EdgeKey(fromId, toId), flow);
        draw();
    }

    /**
     * Sets the displayed distance value for a node.
     *
     * @param nodeId   the node ID
     * @param distance the distance value
     */
    public void setNodeDistance(int nodeId, double distance) {
        nodeDistances.put(nodeId, distance);
        draw();
    }

    /** Redraws the canvas. */
    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (graph == null) {
            return;
        }

        gc.save();
        gc.translate(offsetX, offsetY);
        gc.scale(scale, scale);

        // Draw edges first
        for (Node node : graph.getNodes()) {
            for (Edge edge : graph.neighbors(node.getId())) {
                drawEdge(gc, edge);
            }
        }

        // Draw nodes on top
        for (Node node : graph.getNodes()) {
            drawNode(gc, node);
        }

        gc.restore();
    }

    private void drawNode(GraphicsContext gc, Node node) {
        Position pos = node.getPosition();
        double x = pos.getX();
        double y = pos.getY();

        NodeStyle style = nodeStyles.getOrDefault(node.getId(), NodeStyle.DEFAULT);

        // Draw circle
        gc.setFill(style.getFillColor());
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Draw label
        gc.setFill(style.getTextColor());
        gc.setFont(Font.font(14));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(node.getLabel(), x, y + 5);

        // Draw distance if set
        if (nodeDistances.containsKey(node.getId())) {
            double dist = nodeDistances.get(node.getId());
            String distStr = dist == Double.MAX_VALUE ? "∞" : String.format("%.1f", dist);
            gc.setFill(Color.DARKBLUE);
            gc.setFont(Font.font(10));
            gc.fillText(distStr, x, y - NODE_RADIUS - 5);
        }
    }

    private void drawEdge(GraphicsContext gc, Edge edge) {
        Node fromNode = graph.getNode(edge.getFromId());
        Node toNode = graph.getNode(edge.getToId());

        if (fromNode == null || toNode == null)
            return;

        Position from = fromNode.getPosition();
        Position to = toNode.getPosition();

        EdgeKey key = new EdgeKey(edge.getFromId(), edge.getToId());

        // Determine edge color
        Color edgeColor;
        double lineWidth;
        if (pathEdges.contains(key)) {
            edgeColor = NodeStyle.getEdgePathColor();
            lineWidth = 4;
        } else if (highlightedEdges.contains(key)) {
            edgeColor = NodeStyle.getEdgeHighlightColor();
            lineWidth = 3;
        } else if (edgeFlows.containsKey(key)) {
            edgeColor = NodeStyle.getEdgeFlowColor();
            lineWidth = 3;
        } else {
            edgeColor = NodeStyle.getEdgeDefaultColor();
            lineWidth = 2;
        }

        // Calculate edge endpoints (at circle boundary)
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length == 0)
            return;

        double ux = dx / length;
        double uy = dy / length;

        double startX = from.getX() + ux * NODE_RADIUS;
        double startY = from.getY() + uy * NODE_RADIUS;
        double endX = to.getX() - ux * NODE_RADIUS;
        double endY = to.getY() - uy * NODE_RADIUS;

        // Draw line
        gc.setStroke(edgeColor);
        gc.setLineWidth(lineWidth);
        gc.strokeLine(startX, startY, endX, endY);

        // Draw arrowhead
        drawArrowHead(gc, endX, endY, ux, uy, edgeColor);

        // Draw weight/capacity label
        double midX = (startX + endX) / 2;
        double midY = (startY + endY) / 2;
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(11));

        String label;
        if (edgeFlows.containsKey(key)) {
            label = String.format("%.0f/%.0f", edgeFlows.get(key), edge.getCapacity());
        } else if (edge.getCapacity() > 0) {
            label = String.format("%.0f", edge.getCapacity());
        } else {
            label = String.format("%.1f", edge.getWeight());
        }
        gc.fillText(label, midX + 10, midY - 5);
    }

    private void drawArrowHead(GraphicsContext gc, double x, double y, double ux, double uy, Color color) {
        double angle = Math.PI / 6;
        double ax1 = x - ARROW_SIZE * (ux * Math.cos(angle) - uy * Math.sin(angle));
        double ay1 = y - ARROW_SIZE * (uy * Math.cos(angle) + ux * Math.sin(angle));
        double ax2 = x - ARROW_SIZE * (ux * Math.cos(angle) + uy * Math.sin(angle));
        double ay2 = y - ARROW_SIZE * (uy * Math.cos(angle) - ux * Math.sin(angle));

        gc.setFill(color);
        gc.fillPolygon(new double[] { x, ax1, ax2 }, new double[] { y, ay1, ay2 }, 3);
    }

    public Integer getNodeAt(double screenX, double screenY) {
        if (graph == null)
            return null;

        double x = (screenX - offsetX) / scale;
        double y = (screenY - offsetY) / scale;

        for (Node node : graph.getNodes()) {
            Position pos = node.getPosition();
            double dx = x - pos.getX();
            double dy = y - pos.getY();
            if (dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS) {
                return node.getId();
            }
        }
        return null;
    }

    public Position screenToGraph(double screenX, double screenY) {
        double x = (screenX - offsetX) / scale;
        double y = (screenY - offsetY) / scale;
        return new Position(x, y);
    }

    public void setScale(double scale) {
        this.scale = scale;
        draw();
    }

    public void setOffset(double x, double y) {
        this.offsetX = x;
        this.offsetY = y;
        draw();
    }

    public double getNodeRadius() {
        return NODE_RADIUS;
    }

    private static class EdgeKey {
        final int from;
        final int to;

        EdgeKey(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof EdgeKey))
                return false;
            EdgeKey edgeKey = (EdgeKey) o;
            return from == edgeKey.from && to == edgeKey.to;
        }

        @Override
        public int hashCode() {
            return 31 * from + to;
        }
    }
}
