package algorithm_visualizer.gui;

import algorithm_visualizer.gui.components.AlgorithmSelector;
import algorithm_visualizer.gui.components.ControlPanel;
import algorithm_visualizer.gui.components.GraphCanvas;
import algorithm_visualizer.gui.components.InfoPanel;
import algorithm_visualizer.gui.interaction.GraphEditor;
import algorithm_visualizer.gui.interaction.NodeDragHandler;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main JavaFX Application for the Graph Algorithm Visualizer.
 * <p>
 * Creates and lays out all GUI components including the canvas, control panel,
 * algorithm selector, and info panel. Sets up the main controller and node drag
 * handling.
 * </p>
 */
public class AlgorithmVisualizerApp extends Application {

    private MainController controller;

    @Override
    public void start(Stage primaryStage) {
        // Create components
        GraphCanvas canvas = new GraphCanvas(700, 500);
        ControlPanel controlPanel = new ControlPanel();
        AlgorithmSelector algorithmSelector = new AlgorithmSelector();
        InfoPanel infoPanel = new InfoPanel();

        // Create controller
        controller = new MainController(canvas, controlPanel, algorithmSelector, infoPanel);

        // Setup graph editor and drag handler
        GraphEditor editor = new GraphEditor(canvas);
        editor.setOnGraphChanged(controller::onGraphChanged);
        new NodeDragHandler(canvas);

        // Layout
        BorderPane root = new BorderPane();

        // Left panel: algorithm selector + controls
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("Graph Algorithm Visualizer");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        leftPanel.getChildren().addAll(
                titleLabel,
                new Separator(),
                algorithmSelector,
                new Separator(),
                controlPanel);

        // Center: canvas
        BorderPane canvasPane = new BorderPane(canvas);
        canvasPane.setStyle("-fx-background-color: white;");
        canvasPane.setPadding(new Insets(10));

        // Right panel: info
        root.setLeft(leftPanel);
        root.setCenter(canvasPane);
        root.setRight(infoPanel);

        // Instructions
        Label instructions = new Label(
                "Left-click empty space: add node | Drag: move node | "
                        + "To add an edge: right-click (two-finger click on Mac) > 'Enter Edge Mode', "
                        + "then click two nodes and enter a weight/capacity | "
                        + "Right-click again > 'Exit Edge Mode' to add nodes again | "
                        + "Right-click a node > 'Delete Node'");
        instructions.setWrapText(true);
        instructions.setPadding(new Insets(5));
        instructions.setStyle("-fx-background-color: #e0e0e0;");
        root.setBottom(instructions);

        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setTitle("Graph Algorithm Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial draw
        canvas.draw();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
