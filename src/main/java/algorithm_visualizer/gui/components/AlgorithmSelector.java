package algorithm_visualizer.gui.components;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * UI component for selecting an algorithm and specifying start/target nodes.
 * <p>
 * Provides a dropdown for algorithm selection and text fields for node IDs.
 * Automatically adjusts labels based on the selected algorithm (e.g., "Sink"
 * for max flow).
 * </p>
 */
public class AlgorithmSelector extends VBox {

    /**
     * Enumeration of available algorithms.
     */
    public enum AlgorithmType {
        BFS("Breadth-First Search", false),
        DFS("Depth-First Search", false),
        DIJKSTRA("Dijkstra's Shortest Path", false),
        EDMONDS_KARP("Edmonds-Karp Max Flow", true);

        private final String displayName;
        private final boolean needsSink;

        AlgorithmType(String displayName, boolean needsSink) {
            this.displayName = displayName;
            this.needsSink = needsSink;
        }

        /** Returns the human-readable display name. */
        public String getDisplayName() {
            return displayName;
        }

        /** Returns true if this algorithm requires a sink node. */
        public boolean needsSink() {
            return needsSink;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private final ComboBox<AlgorithmType> algorithmCombo;
    private final TextField startNodeField;
    private final TextField targetNodeField;
    private final Label targetLabel;

    public AlgorithmSelector() {
        setSpacing(10);
        setPadding(new Insets(10));

        Label algoLabel = new Label("Algorithm:");
        algorithmCombo = new ComboBox<>();
        algorithmCombo.getItems().addAll(AlgorithmType.values());
        algorithmCombo.setValue(AlgorithmType.BFS);

        HBox algoRow = new HBox(10, algoLabel, algorithmCombo);

        Label startLabel = new Label("Start Node:");
        startNodeField = new TextField("0");
        startNodeField.setPrefWidth(60);
        HBox startRow = new HBox(10, startLabel, startNodeField);

        targetLabel = new Label("Target Node:");
        targetNodeField = new TextField("");
        targetNodeField.setPrefWidth(60);
        targetNodeField.setPromptText("optional");
        HBox targetRow = new HBox(10, targetLabel, targetNodeField);

        getChildren().addAll(algoRow, startRow, targetRow);

        // Update label based on algorithm
        algorithmCombo.setOnAction(e -> {
            if (getSelectedAlgorithm().needsSink()) {
                targetLabel.setText("Sink Node:");
                targetNodeField.setPromptText("required");
            } else {
                targetLabel.setText("Target Node:");
                targetNodeField.setPromptText("optional");
            }
        });
    }

    /** Returns the currently selected algorithm. */
    public AlgorithmType getSelectedAlgorithm() {
        return algorithmCombo.getValue();
    }

    /** Returns the start node ID entered by the user. */
    public int getStartNode() {
        try {
            return Integer.parseInt(startNodeField.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Returns the target/sink node ID, or null if not specified. */
    public Integer getTargetNode() {
        String text = targetNodeField.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Sets the start node ID in the text field. */
    public void setStartNode(int nodeId) {
        startNodeField.setText(String.valueOf(nodeId));
    }

    /** Sets the target node ID in the text field. */
    public void setTargetNode(Integer nodeId) {
        targetNodeField.setText(nodeId != null ? String.valueOf(nodeId) : "");
    }
}
