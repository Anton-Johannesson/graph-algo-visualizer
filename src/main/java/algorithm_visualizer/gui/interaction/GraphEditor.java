package algorithm_visualizer.gui.interaction;

import java.util.function.BiConsumer;

import algorithm_visualizer.gui.components.GraphCanvas;
import algorithm_visualizer.model.Edge;
import algorithm_visualizer.model.Graph;
import algorithm_visualizer.model.Position;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Handles graph editing operations via mouse interaction.
 * <p>
 * Supports creating nodes by clicking empty space, creating edges between
 * nodes,
 * and deleting nodes via context menu. Can toggle between node and edge
 * creation modes.
 * </p>
 */
public class GraphEditor {

    private final GraphCanvas canvas;
    private int nextNodeId = 0;
    private Integer edgeStartNode = null;
    private boolean edgeMode = false;

    private BiConsumer<Integer, Integer> onEdgeCreated;
    private Runnable onGraphChanged;

    public GraphEditor(GraphCanvas canvas) {
        this.canvas = canvas;
        setupMouseHandlers();
    }

    private void setupMouseHandlers() {
        canvas.setOnMouseClicked(this::handleMouseClick);
    }

    private void handleMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            handleLeftClick(event);
        } else if (event.getButton() == MouseButton.SECONDARY) {
            handleRightClick(event);
        }
    }

    private void handleLeftClick(MouseEvent event) {
        // A click that is really the end of a drag must not create nodes/edges
        if (!event.isStillSincePress()) {
            return;
        }

        Integer clickedNode = canvas.getNodeAt(event.getX(), event.getY());

        if (edgeMode) {
            if (clickedNode != null) {
                if (edgeStartNode == null) {
                    edgeStartNode = clickedNode;
                } else if (!edgeStartNode.equals(clickedNode)) {
                    // Create edge
                    promptForEdgeWeight(edgeStartNode, clickedNode);
                    edgeStartNode = null;
                }
            }
        } else {
            // Node creation mode - only if clicked on empty space
            if (clickedNode == null) {
                Position pos = canvas.screenToGraph(event.getX(), event.getY());
                addNode(pos);
            }
        }
    }

    private void handleRightClick(MouseEvent event) {
        Integer clickedNode = canvas.getNodeAt(event.getX(), event.getY());

        ContextMenu contextMenu = new ContextMenu();

        if (clickedNode != null) {
            MenuItem deleteItem = new MenuItem("Delete Node " + clickedNode);
            deleteItem.setOnAction(e -> deleteNode(clickedNode));
            contextMenu.getItems().add(deleteItem);
        } else {
            MenuItem addNodeItem = new MenuItem("Add Node Here");
            addNodeItem.setOnAction(e -> {
                Position pos = canvas.screenToGraph(event.getX(), event.getY());
                addNode(pos);
            });
            contextMenu.getItems().add(addNodeItem);
        }

        MenuItem toggleEdgeMode = new MenuItem(edgeMode ? "Exit Edge Mode" : "Enter Edge Mode");
        toggleEdgeMode.setOnAction(e -> {
            edgeMode = !edgeMode;
            edgeStartNode = null;
        });
        contextMenu.getItems().add(toggleEdgeMode);

        contextMenu.show(canvas, event.getScreenX(), event.getScreenY());
    }

    private void addNode(Position pos) {
        Graph graph = canvas.getGraph();
        if (graph == null)
            return;

        // Find next available ID
        while (graph.hasNode(nextNodeId)) {
            nextNodeId++;
        }

        graph.addNode(nextNodeId, pos);
        nextNodeId++;
        canvas.draw();
        notifyGraphChanged();
    }

    private void deleteNode(int nodeId) {
        Graph graph = canvas.getGraph();
        if (graph == null || !graph.removeNode(nodeId)) {
            return;
        }
        if (edgeStartNode != null && edgeStartNode == nodeId) {
            edgeStartNode = null;
        }
        canvas.draw();
        notifyGraphChanged();
    }

    private void promptForEdgeWeight(int fromId, int toId) {
        TextInputDialog dialog = new TextInputDialog("1.0");
        dialog.setTitle("Add Edge");
        dialog.setHeaderText("Creating edge from " + fromId + " to " + toId);
        dialog.setContentText("Weight/Capacity:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                double weight = Double.parseDouble(input.trim());
                if (!Double.isFinite(weight) || weight < 0) {
                    showError("Weight/capacity must be a finite number >= 0.");
                    return;
                }
                Graph graph = canvas.getGraph();
                if (graph != null && !graph.hasEdge(fromId, toId)) {
                    graph.addEdge(new Edge(fromId, toId, weight, weight));
                    canvas.draw();
                    if (onEdgeCreated != null) {
                        onEdgeCreated.accept(fromId, toId);
                    }
                    notifyGraphChanged();
                }
            } catch (NumberFormatException e) {
                showError("Not a number: " + input);
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        });
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    public void setEdgeMode(boolean edgeMode) {
        this.edgeMode = edgeMode;
        this.edgeStartNode = null;
    }

    public boolean isEdgeMode() {
        return edgeMode;
    }

    public void setOnEdgeCreated(BiConsumer<Integer, Integer> handler) {
        this.onEdgeCreated = handler;
    }

    public void setOnGraphChanged(Runnable handler) {
        this.onGraphChanged = handler;
    }

    private void notifyGraphChanged() {
        if (onGraphChanged != null) {
            onGraphChanged.run();
        }
    }
}
