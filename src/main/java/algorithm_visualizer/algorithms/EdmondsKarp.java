package algorithm_visualizer.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import algorithm_visualizer.model.Edge;
import algorithm_visualizer.model.Graph;
import algorithm_visualizer.steps.AlgorithmStep;

/**
 * Edmonds-Karp algorithm for computing maximum flow in a flow network.
 * <p>
 * This is an implementation of the Ford-Fulkerson method using BFS to find
 * augmenting paths. The BFS approach ensures that the algorithm runs in
 * O(VE²) time, making it polynomial regardless of edge capacities.
 * </p>
 */
public class EdmondsKarp {

    private EdmondsKarp() {
    }

    /**
     * Computes the maximum flow from source to sink using the Edmonds-Karp
     * algorithm.
     *
     * @param graph    the flow network (edge weights used as capacities if capacity
     *                 is 0)
     * @param sourceId the source node ID
     * @param sinkId   the sink node ID
     * @return the result containing max flow value, augmenting paths, and steps
     * @throws IllegalArgumentException if source or sink does not exist, or if they
     *                                  are the same
     */
    public static EdmondsKarpResult run(Graph graph, int sourceId, int sinkId) {
        Objects.requireNonNull(graph, "graph must not be null");

        if (!graph.hasNode(sourceId)) {
            throw new IllegalArgumentException("Source node does not exist: " + sourceId);
        }
        if (!graph.hasNode(sinkId)) {
            throw new IllegalArgumentException("Sink node does not exist: " + sinkId);
        }
        if (sourceId == sinkId) {
            throw new IllegalArgumentException("Source and sink must be different");
        }

        List<AlgorithmStep> steps = new ArrayList<>();

        // Build residual graph: for each edge, track residual capacity
        // Key: fromId -> (toId -> residual capacity)
        Map<Integer, Map<Integer, Double>> residual = new HashMap<>();
        initializeResidualGraph(graph, residual);

        double maxFlow = 0.0;
        List<List<Integer>> augmentingPaths = new ArrayList<>();

        while (true) {
            // BFS to find shortest augmenting path
            BfsPathResult pathResult = bfsAugmentingPath(residual, sourceId, sinkId, steps);

            if (pathResult.path.isEmpty()) {
                // No augmenting path found - we're done
                break;
            }

            List<Integer> path = pathResult.path;
            augmentingPaths.add(new ArrayList<>(path));

            // Find bottleneck (minimum residual capacity along path)
            double bottleneck = Double.MAX_VALUE;
            for (int i = 0; i < path.size() - 1; i++) {
                int from = path.get(i);
                int to = path.get(i + 1);
                double cap = residual.get(from).getOrDefault(to, 0.0);
                bottleneck = Math.min(bottleneck, cap);
            }

            // Augment flow along the path
            for (int i = 0; i < path.size() - 1; i++) {
                int from = path.get(i);
                int to = path.get(i + 1);

                // Decrease forward residual
                residual.get(from).merge(to, -bottleneck, Double::sum);

                // Increase backward residual (for flow cancellation)
                residual.computeIfAbsent(to, k -> new HashMap<>());
                residual.get(to).merge(from, bottleneck, Double::sum);

                steps.add(AlgorithmStep.updateFlow(from, to, bottleneck));
            }

            maxFlow += bottleneck;
        }

        steps.add(AlgorithmStep.done("Max flow: " + maxFlow));

        return new EdmondsKarpResult(maxFlow, augmentingPaths, steps);
    }

    /**
     * Initializes the residual graph from the original graph.
     * For each edge, sets the forward capacity and zero backward capacity.
     */
    private static void initializeResidualGraph(Graph graph, Map<Integer, Map<Integer, Double>> residual) {
        for (var node : graph.getNodes()) {
            int nodeId = node.getId();
            residual.putIfAbsent(nodeId, new HashMap<>());

            for (Edge edge : graph.neighbors(nodeId)) {
                int to = edge.getToId();
                double capacity = edge.getCapacity();

                // If capacity is 0, use weight as capacity (for simple weighted graphs)
                if (capacity == 0) {
                    capacity = edge.getWeight();
                }

                residual.get(nodeId).merge(to, capacity, Double::sum);

                // Ensure backward edge entry exists (starts at 0)
                residual.computeIfAbsent(to, k -> new HashMap<>());
                residual.get(to).putIfAbsent(nodeId, 0.0);
            }
        }
    }

    /**
     * Performs BFS on the residual graph to find an augmenting path from source to
     * sink.
     *
     * @return the path as a list of node IDs, or empty list if no path exists
     */
    private static BfsPathResult bfsAugmentingPath(
            Map<Integer, Map<Integer, Double>> residual,
            int source,
            int sink,
            List<AlgorithmStep> steps) {

        Queue<Integer> queue = new ArrayDeque<>();
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(source);
        visited.add(source);
        steps.add(AlgorithmStep.visitNode(source));

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == sink) {
                // Reconstruct path
                List<Integer> path = new ArrayList<>();
                int node = sink;
                while (node != source) {
                    path.add(node);
                    node = parent.get(node);
                }
                path.add(source);
                Collections.reverse(path);
                return new BfsPathResult(path);
            }

            Map<Integer, Double> neighbors = residual.getOrDefault(current, new HashMap<>());
            for (Map.Entry<Integer, Double> entry : neighbors.entrySet()) {
                int neighbor = entry.getKey();
                double capacity = entry.getValue();

                if (!visited.contains(neighbor) && capacity > 0) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                    steps.add(AlgorithmStep.visitNode(neighbor));
                }
            }
        }

        // No path found
        return new BfsPathResult(new ArrayList<>());
    }

    /**
     * Internal class to hold the result of a BFS search for an augmenting path.
     */
    private static class BfsPathResult {
        final List<Integer> path;

        BfsPathResult(List<Integer> path) {
            this.path = path;
        }
    }

    /**
     * Contains the results of an Edmonds-Karp max flow computation.
     */
    public static class EdmondsKarpResult {
        private final double maxFlow;
        private final List<List<Integer>> augmentingPaths;
        private final List<AlgorithmStep> steps;

        public EdmondsKarpResult(double maxFlow,
                List<List<Integer>> augmentingPaths,
                List<AlgorithmStep> steps) {
            this.maxFlow = maxFlow;
            this.augmentingPaths = new ArrayList<>();
            for (List<Integer> path : augmentingPaths) {
                this.augmentingPaths.add(new ArrayList<>(path));
            }
            this.steps = new ArrayList<>(steps);
        }

        /** Returns the computed maximum flow value. */
        public double getMaxFlow() {
            return maxFlow;
        }

        /** Returns the list of augmenting paths found during execution. */
        public List<List<Integer>> getAugmentingPaths() {
            List<List<Integer>> copy = new ArrayList<>();
            for (List<Integer> path : augmentingPaths) {
                copy.add(Collections.unmodifiableList(path));
            }
            return copy;
        }

        /** Returns the list of algorithm steps for visualization. */
        public List<AlgorithmStep> getSteps() {
            return Collections.unmodifiableList(steps);
        }
    }
}