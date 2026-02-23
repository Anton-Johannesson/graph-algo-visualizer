package algorithm_visualizer.gui;

import java.util.HashSet;
import java.util.Set;

import algorithm_visualizer.algorithms.BFS;
import algorithm_visualizer.algorithms.DFS;
import algorithm_visualizer.algorithms.Dijkstra;
import algorithm_visualizer.algorithms.EdmondsKarp;
import algorithm_visualizer.gui.animation.NodeStyle;
import algorithm_visualizer.gui.animation.StepPlayer;
import algorithm_visualizer.gui.components.AlgorithmSelector;
import algorithm_visualizer.gui.components.AlgorithmSelector.AlgorithmType;
import algorithm_visualizer.gui.components.ControlPanel;
import algorithm_visualizer.gui.components.GraphCanvas;
import algorithm_visualizer.gui.components.InfoPanel;
import algorithm_visualizer.model.Graph;
import algorithm_visualizer.model.Position;
import algorithm_visualizer.steps.AlgorithmStep;

/**
 * Central controller coordinating the algorithm visualization GUI.
 * <p>
 * Connects the graph model with UI components, manages algorithm execution,
 * and handles step playback with visual updates to the canvas.
 * </p>
 */
public class MainController {

    private final GraphCanvas canvas;
    private final ControlPanel controlPanel;
    private final AlgorithmSelector algorithmSelector;
    private final InfoPanel infoPanel;
    private final StepPlayer stepPlayer;

    private Graph graph;
    private final Set<Integer> visitedNodes = new HashSet<>();

    public MainController(GraphCanvas canvas, ControlPanel controlPanel,
            AlgorithmSelector algorithmSelector, InfoPanel infoPanel) {
        this.canvas = canvas;
        this.controlPanel = controlPanel;
        this.algorithmSelector = algorithmSelector;
        this.infoPanel = infoPanel;
        this.stepPlayer = new StepPlayer();

        setupGraph();
        setupBindings();
    }

    private void setupGraph() {
        graph = new Graph();

        // Create a sample graph for demonstration
        graph.addNode(0, new Position(100, 200));
        graph.addNode(1, new Position(250, 100));
        graph.addNode(2, new Position(250, 300));
        graph.addNode(3, new Position(400, 100));
        graph.addNode(4, new Position(400, 300));
        graph.addNode(5, new Position(550, 200));

        graph.addEdge(0, 1, 2.0);
        graph.addEdge(0, 2, 4.0);
        graph.addEdge(1, 3, 3.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 4, 2.0);
        graph.addEdge(3, 5, 2.0);
        graph.addEdge(4, 5, 3.0);
        graph.addEdge(4, 3, 1.0);

        canvas.setGraph(graph);
    }

    private void setupBindings() {
        // Control panel buttons
        controlPanel.setOnRun(this::runAlgorithm);
        controlPanel.setOnPause(this::pauseAlgorithm);
        controlPanel.setOnStepForward(this::stepForward);
        controlPanel.setOnStepBackward(this::stepBackward);
        controlPanel.setOnReset(this::resetVisualization);

        // Speed slider
        controlPanel.getSpeedSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            stepPlayer.setSpeed(newVal.doubleValue());
        });

        // Step player callbacks
        stepPlayer.setOnStep(this::applyStep);
        stepPlayer.setOnIndexChange(index -> {
            infoPanel.setStepCount(index + 1, stepPlayer.getTotalSteps());
        });
        stepPlayer.setOnComplete(() -> {
            infoPanel.appendLog("Algorithm complete!");
            controlPanel.setPlaying(false);
        });
        stepPlayer.setOnPlayStateChange(() -> {
            controlPanel.setPlaying(stepPlayer.isPlaying());
        });
    }

    private void runAlgorithm() {
        resetVisualization();

        AlgorithmType type = algorithmSelector.getSelectedAlgorithm();
        int startId = algorithmSelector.getStartNode();
        Integer targetId = algorithmSelector.getTargetNode();

        if (!graph.hasNode(startId)) {
            infoPanel.appendLog("Error: Start node " + startId + " does not exist");
            return;
        }
        if (targetId != null && !graph.hasNode(targetId)) {
            infoPanel.appendLog("Error: Target node " + targetId + " does not exist");
            return;
        }

        // Edmonds-Karp requires a sink node
        if (type == AlgorithmType.EDMONDS_KARP && targetId == null) {
            infoPanel.appendLog("Error: Edmonds-Karp requires a sink node.");
            infoPanel.appendLog("Please enter a Sink Node ID in the field above.");
            return;
        }

        infoPanel.appendLog("Running " + type.getDisplayName() + "...");
        infoPanel.appendLog("Start: " + startId + (targetId != null ? ", Target/Sink: " + targetId : ""));

        try {
            var steps = switch (type) {
                case BFS -> BFS.run(graph, startId, targetId).getSteps();
                case DFS -> DFS.runDfs(graph, startId, targetId).getSteps();
                case DIJKSTRA -> Dijkstra.run(graph, startId, targetId).getSteps();
                case EDMONDS_KARP -> EdmondsKarp.run(graph, startId, targetId).getSteps();
            };

            if (!steps.isEmpty()) {
                stepPlayer.load(steps);
                stepPlayer.setSpeed(controlPanel.getSpeed());

                // Mark source/sink
                canvas.setNodeStyle(startId, NodeStyle.SOURCE);
                if (targetId != null) {
                    canvas.setNodeStyle(targetId, NodeStyle.SINK);
                }

                stepPlayer.play();
            }
        } catch (Exception e) {
            infoPanel.appendLog("Error: " + e.getMessage());
        }
    }

    private void pauseAlgorithm() {
        stepPlayer.pause();
    }

    private void stepForward() {
        if (stepPlayer.getTotalSteps() == 0) {
            // Need to load algorithm first
            loadAlgorithmSteps();
        }
        stepPlayer.stepForward();
    }

    private void stepBackward() {
        if (stepPlayer.getCurrentIndex() >= 0) {
            // Replay from beginning to previous step
            int targetIndex = stepPlayer.getCurrentIndex() - 1;
            replayToStep(targetIndex);
        }
    }

    private void loadAlgorithmSteps() {
        AlgorithmType type = algorithmSelector.getSelectedAlgorithm();
        int startId = algorithmSelector.getStartNode();
        Integer targetId = algorithmSelector.getTargetNode();

        if (!graph.hasNode(startId))
            return;

        // Edmonds-Karp requires sink
        if (type == AlgorithmType.EDMONDS_KARP && targetId == null) {
            infoPanel.appendLog("Error: Please enter a Sink Node for Edmonds-Karp.");
            return;
        }

        try {
            var steps = switch (type) {
                case BFS -> BFS.run(graph, startId, targetId).getSteps();
                case DFS -> DFS.runDfs(graph, startId, targetId).getSteps();
                case DIJKSTRA -> Dijkstra.run(graph, startId, targetId).getSteps();
                case EDMONDS_KARP -> EdmondsKarp.run(graph, startId, targetId).getSteps();
            };
            stepPlayer.load(steps);

            canvas.setNodeStyle(startId, NodeStyle.SOURCE);
            if (targetId != null) {
                canvas.setNodeStyle(targetId, NodeStyle.SINK);
            }
        } catch (Exception e) {
            infoPanel.appendLog("Error: " + e.getMessage());
        }
    }

    private void replayToStep(int targetIndex) {
        // Reset visualization
        canvas.resetVisualization();
        visitedNodes.clear();

        int startId = algorithmSelector.getStartNode();
        Integer targetId = algorithmSelector.getTargetNode();
        canvas.setNodeStyle(startId, NodeStyle.SOURCE);
        if (targetId != null) {
            canvas.setNodeStyle(targetId, NodeStyle.SINK);
        }

        // Replay steps up to targetIndex
        var steps = stepPlayer.getSteps();
        for (int i = 0; i <= targetIndex && i < steps.size(); i++) {
            applyStepSilent(steps.get(i));
        }

        // Update step player index (via reflection or direct access)
        infoPanel.setStepCount(targetIndex + 1, stepPlayer.getTotalSteps());
        canvas.draw();
    }

    private void applyStep(AlgorithmStep step) {
        applyStepSilent(step);
        infoPanel.appendLog(step.getMessage());
        canvas.draw();
    }

    private void applyStepSilent(AlgorithmStep step) {
        int startId = algorithmSelector.getStartNode();
        Integer targetId = algorithmSelector.getTargetNode();

        switch (step.getType()) {
            case VISIT_NODE -> {
                int nodeId = step.getNodeId();
                visitedNodes.add(nodeId);
                // Don't override source/sink colors
                if (nodeId != startId && (targetId == null || nodeId != targetId)) {
                    canvas.setNodeStyle(nodeId, NodeStyle.VISITED);
                }
            }
            case SET_PARENT -> {
                canvas.highlightEdge(step.getFromId(), step.getToId());
            }
            case RELAX_EDGE -> {
                canvas.highlightEdge(step.getFromId(), step.getToId());
                if (step.getValue() != null) {
                    canvas.setNodeDistance(step.getToId(), step.getValue());
                }
            }
            case UPDATE_FLOW -> {
                canvas.setEdgeFlow(step.getFromId(), step.getToId(), step.getValue());
            }
            case HIGHLIGHT_PATH -> {
                canvas.setPathEdge(step.getFromId(), step.getToId());
            }
            case DONE -> {
                infoPanel.setCurrentStep(step.getMessage());
            }
        }
    }

    private void resetVisualization() {
        stepPlayer.stop();
        canvas.resetVisualization();
        visitedNodes.clear();
        infoPanel.reset();
        canvas.draw();
    }

    public Graph getGraph() {
        return graph;
    }

    public GraphCanvas getCanvas() {
        return canvas;
    }
}
