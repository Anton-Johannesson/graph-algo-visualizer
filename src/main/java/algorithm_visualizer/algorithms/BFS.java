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
 * Breadth-First Search (BFS) algorithm implementation.
 * <p>
 * BFS explores nodes level by level, visiting all neighbors of a node
 * before moving to the next level. This guarantees finding the shortest
 * path in unweighted graphs.
 * </p>
 */
public class BFS {

    private BFS() {
    }

    /**
     * Runs BFS from the start node, exploring all reachable nodes.
     *
     * @param graph   the graph to search
     * @param startId the starting node ID
     * @return the result containing visit order, parent map, distances, and steps
     */
    public static BfsResult run(Graph graph, int startId) {
        return run(graph, startId, null);
    }

    /**
     * Runs BFS from start node, optionally stopping when target is found.
     *
     * @param graph    the graph to search
     * @param startId  the starting node ID
     * @param targetId optional target node ID (null to explore entire graph)
     * @return the result containing visit order, parent map, distances, and steps
     * @throws IllegalArgumentException if start or target node does not exist
     */
    public static BfsResult run(Graph graph, int startId, Integer targetId) {
        Objects.requireNonNull(graph, "graph must not be null");

        if (!graph.hasNode(startId)) {
            throw new IllegalArgumentException("Start node does not exist: " + startId);
        }
        if (targetId != null && !graph.hasNode(targetId)) {
            throw new IllegalArgumentException("Target node does not exist: " + targetId);
        }

        Queue<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> parent = new HashMap<>();
        Map<Integer, Integer> distance = new HashMap<>();
        List<Integer> visitOrder = new ArrayList<>();
        List<AlgorithmStep> steps = new ArrayList<>();

        visited.add(startId);
        queue.add(startId);
        distance.put(startId, 0);

        boolean foundTarget = false;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            visitOrder.add(current);
            steps.add(AlgorithmStep.visitNode(current));

            if (targetId != null && current == targetId) {
                foundTarget = true;
                break;
            }

            for (Edge edge : graph.neighbors(current)) {
                int neighbor = edge.getToId();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    distance.put(neighbor, distance.get(current) + 1);
                    queue.add(neighbor);
                    steps.add(AlgorithmStep.setParent(neighbor, current));
                }
            }
        }

        if (targetId == null) {
            foundTarget = true;
        }

        steps.add(AlgorithmStep.done(foundTarget ? "BFS complete" : "BFS target not reachable"));

        return new BfsResult(foundTarget, visitOrder, parent, distance, steps);
    }

    /**
     * Contains the results of a BFS execution.
     */
    public static class BfsResult {
        private final boolean found;
        private final List<Integer> visitOrder;
        private final Map<Integer, Integer> parent;
        private final Map<Integer, Integer> distance;
        private final List<AlgorithmStep> steps;

        public BfsResult(boolean found,
                List<Integer> visitOrder,
                Map<Integer, Integer> parent,
                Map<Integer, Integer> distance,
                List<AlgorithmStep> steps) {
            this.found = found;
            this.visitOrder = new ArrayList<>(visitOrder);
            this.parent = new HashMap<>(parent);
            this.distance = new HashMap<>(distance);
            this.steps = new ArrayList<>(steps);
        }

        /** Returns true if the target was found (or no target was specified). */
        public boolean isFound() {
            return found;
        }

        /** Returns the order in which nodes were visited. */
        public List<Integer> getVisitOrder() {
            return Collections.unmodifiableList(visitOrder);
        }

        /** Returns a map from each node to its parent in the BFS tree. */
        public Map<Integer, Integer> getParent() {
            return Collections.unmodifiableMap(parent);
        }

        /** Returns a map from each node to its distance (in hops) from the start. */
        public Map<Integer, Integer> getDistance() {
            return Collections.unmodifiableMap(distance);
        }

        /** Returns the list of algorithm steps for visualization. */
        public List<AlgorithmStep> getSteps() {
            return Collections.unmodifiableList(steps);
        }
    }
}