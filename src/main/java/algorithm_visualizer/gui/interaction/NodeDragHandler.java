package algorithm_visualizer.gui.interaction;

import algorithm_visualizer.gui.components.GraphCanvas;
import algorithm_visualizer.model.Graph;
import algorithm_visualizer.model.Node;
import algorithm_visualizer.model.Position;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

/**
 * Handles node dragging operations for repositioning nodes in the graph.
 * <p>
 * Allows users to click and drag nodes to new positions on the canvas.
 * </p>
 */
public class NodeDragHandler {

    private final GraphCanvas canvas;
    private Integer draggedNodeId = null;
    private double dragOffsetX;
    private double dragOffsetY;

    public NodeDragHandler(GraphCanvas canvas) {
        this.canvas = canvas;
        setupDragHandlers();
    }

    private void setupDragHandlers() {
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
        canvas.setOnMouseMoved(this::handleMouseMoved);
    }

    private void handleMousePressed(MouseEvent event) {
        Integer nodeId = canvas.getNodeAt(event.getX(), event.getY());
        if (nodeId != null) {
            draggedNodeId = nodeId;
            Graph graph = canvas.getGraph();
            if (graph != null) {
                Node node = graph.getNode(nodeId);
                if (node != null) {
                    Position graphPos = canvas.screenToGraph(event.getX(), event.getY());
                    dragOffsetX = graphPos.getX() - node.getPosition().getX();
                    dragOffsetY = graphPos.getY() - node.getPosition().getY();
                }
            }
            canvas.setCursor(Cursor.CLOSED_HAND);
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (draggedNodeId != null) {
            Graph graph = canvas.getGraph();
            if (graph != null) {
                Node node = graph.getNode(draggedNodeId);
                if (node != null) {
                    Position graphPos = canvas.screenToGraph(event.getX(), event.getY());
                    double newX = graphPos.getX() - dragOffsetX;
                    double newY = graphPos.getY() - dragOffsetY;
                    node.moveTo(newX, newY);
                    canvas.draw();
                }
            }
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        draggedNodeId = null;
        canvas.setCursor(Cursor.DEFAULT);
    }

    private void handleMouseMoved(MouseEvent event) {
        Integer nodeId = canvas.getNodeAt(event.getX(), event.getY());
        if (nodeId != null) {
            canvas.setCursor(Cursor.HAND);
        } else {
            canvas.setCursor(Cursor.DEFAULT);
        }
    }

    public boolean isDragging() {
        return draggedNodeId != null;
    }
}
