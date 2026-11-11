package algorithm_visualizer.gui;

import java.util.HashSet;
import java.util.List;
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

    /** The configuration (algorithm, start, target) the loaded steps were computed from. */
    private record RunConfig(AlgorithmType type, int startId, Integer targetId) {
    }

    private final GraphCanvas canvas;
    private final ControlPanel controlPanel;
    private final AlgorithmSelector algorithmSelector;
    private final InfoPanel infoPanel;
    private final StepPlayer stepPlayer;

    private Graph graph;
    private final Set<Integer> visitedNodes = new HashSet<>();
    private RunConfig loadedConfig;

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

        RunConfig cfg = currentConfig();
        infoPanel.appendLog("Running " + cfg.type().getDisplayName() + "...");
        infoPanel.appendLog("Start: " + cfg.startId()
                + (cfg.targetId() != null ? ", Target/Sink: " + cfg.targetId() : ""));

        if (loadSteps(cfg)) {
            stepPlayer.play();
        }
    }

    private RunConfig currentConfig() {
        return new RunConfig(algorithmSelector.getSelectedAlgorithm(),
                algorithmSelector.getStartNode(), algorithmSelector.getTargetNode());
    }

    /** Validates the configuration and runs the algorithm. Returns null (after logging) on error. */
    private List<AlgorithmStep> computeSteps(RunConfig cfg) {
        if (!graph.hasNode(cfg.startId())) {
            infoPanel.appendLog("Error: Start node " + cfg.startId() + " does not exist");
            return null;
        }
        if (cfg.targetId() != null && !graph.hasNode(cfg.targetId())) {
            infoPanel.appendLog("Error: Target node " + cfg.targetId() + " does not exist");
            return null;
        }

        // Edmonds-Karp requires a sink node
        if (cfg.type() == AlgorithmType.EDMONDS_KARP && cfg.targetId() == null) {
            infoPanel.appendLog("Error: Edmonds-Karp requires a sink node (enter a Sink Node ID).");
            return null;
        }

        try {
            return switch (cfg.type()) {
                case BFS -> BFS.run(graph, cfg.startId(), cfg.targetId()).getSteps();
                case DFS -> DFS.runDfs(graph, cfg.startId(), cfg.targetId()).getSteps();
                case DIJKSTRA -> Dijkstra.run(graph, cfg.startId(), cfg.targetId()).getSteps();
                case EDMONDS_KARP -> EdmondsKarp.run(graph, cfg.startId(), cfg.targetId()).getSteps();
            };
        } catch (Exception e) {
            infoPanel.appendLog("Error: " + e.getMessage());
            return null;
        }
    }

    /** Computes and loads the steps for cfg. Returns false if nothing was loaded. */
    private boolean loadSteps(RunConfig cfg) {
        List<AlgorithmStep> steps = computeSteps(cfg);
        if (steps == null || steps.isEmpty()) {
            return false;
        }
        stepPlayer.load(steps);
        stepPlayer.setSpeed(controlPanel.getSpeed());
        loadedConfig = cfg;
        markSourceAndSink(cfg);
        return true;
    }

    private void markSourceAndSink(RunConfig cfg) {
        canvas.setNodeStyle(cfg.startId(), NodeStyle.SOURCE);
        if (cfg.targetId() != null) {
            canvas.setNodeStyle(cfg.targetId(), NodeStyle.SINK);
        }
    }

    private void pauseAlgorithm() {
        stepPlayer.pause();
    }

    private void stepForward() {
        RunConfig cfg = currentConfig();
        if (stepPlayer.getTotalSteps() == 0 || !cfg.equals(loadedConfig)) {
            // Nothing loaded, or the selection changed: reload from scratch
            resetVisualization();
            if (!loadSteps(cfg)) {
                return;
            }
        }
        stepPlayer.stepForward();
    }

    private void stepBackward() {
        if (loadedConfig != null && stepPlayer.getCurrentIndex() >= 0) {
            // Replay from beginning to previous step
            int targetIndex = stepPlayer.getCurrentIndex() - 1;
            replayToStep(targetIndex);
        }
    }

    private void replayToStep(int targetIndex) {
        RunConfig cfg = loadedConfig;
        if (cfg == null) {
            return;
        }

        // Reset visualization
        canvas.resetVisualization();
        visitedNodes.clear();
        markSourceAndSink(cfg);

        // Replay steps up to targetIndex
        var steps = stepPlayer.getSteps();
        for (int i = 0; i <= targetIndex && i < steps.size(); i++) {
            applyStepSilent(steps.get(i));
        }

        // Sync the player's index (does not fire onStep)
        stepPlayer.seekTo(targetIndex);
        canvas.draw();
    }

    private void applyStep(AlgorithmStep step) {
        applyStepSilent(step);
        infoPanel.appendLog(step.getMessage());
        canvas.draw();
    }

    private void applyStepSilent(AlgorithmStep step) {
        if (loadedConfig == null) {
            return;
        }
        int startId = loadedConfig.startId();
        Integer targetId = loadedConfig.targetId();

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
        stepPlayer.load(List.of());
        loadedConfig = null;
        canvas.resetVisualization();
        visitedNodes.clear();
        infoPanel.reset();
        canvas.draw();
    }

    /** Called by the graph editor when nodes or edges change. */
    public void onGraphChanged() {
        resetVisualization();
    }

    public Graph getGraph() {
        return graph;
    }

    public GraphCanvas getCanvas() {
        return canvas;
    }
}
