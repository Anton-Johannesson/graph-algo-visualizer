package algorithm_visualizer.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import algorithm_visualizer.model.Edge;
import algorithm_visualizer.model.Graph;
import algorithm_visualizer.steps.AlgorithmStep;

/**
 * Depth-First Search (DFS) algorithm implementation.
 * <p>
 * DFS explores as far as possible along each branch before backtracking.
 * It uses recursion (or a stack) to traverse the graph.
 * </p>
 */
public class DFS {

    private DFS() {

    }

    /**
     * Runs DFS from the start node, exploring all reachable nodes.
     *
     * @param graph   the graph to search
     * @param startId the starting node ID
     * @return the result containing visit order, parent map, and steps
     */
    public static DfsResult run(Graph graph, int startId) {
        return runDfs(graph, startId, null);
    }

    /**
     * Runs DFS from start node, optionally stopping when target is found.
     *
     * @param graph    the graph to search
     * @param startId  the starting node ID
     * @param targetId optional target node ID (null to explore entire graph)
     * @return the result containing visit order, parent map, and steps
     * @throws IllegalArgumentException if start or target node does not exist
     */
    public static DfsResult runDfs(Graph graph, int startId, Integer targetId) {
        Objects.requireNonNull(graph, "graph must be not null");

        if (!graph.hasNode(startId)) {
            throw new IllegalArgumentException("Start node does not exist");
        }
        if (targetId != null && !graph.hasNode(targetId)) {
            throw new IllegalArgumentException("Target node does not exist");
        }

        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> parent = new HashMap<>();
        List<Integer> visitOrder = new ArrayList<>();
        List<AlgorithmStep> steps = new ArrayList<>();

        boolean found = dfsRecursive(graph, startId, targetId, visited, parent, visitOrder, steps);

        if (targetId == null) {
            found = true;
        }
        steps.add(AlgorithmStep.done(found ? "DFS complete" : "DFS target not reachable"));
        return new DfsResult(found, visitOrder, parent, steps);
    }

    /**
     * Recursive helper method that performs the actual DFS traversal.
     */
    private static boolean dfsRecursive(Graph graph, int current, Integer targetId,
            Set<Integer> visited, Map<Integer, Integer> parent,
            List<Integer> visitOrder, List<AlgorithmStep> steps) {
        visited.add(current);
        visitOrder.add(current);
        steps.add(AlgorithmStep.visitNode(current));

        if (targetId != null && current == targetId) {
            return true;
        }

        for (Edge edge : graph.neighbors(current)) {
            int neighbor = edge.getToId();
            if (!visited.contains(neighbor)) {
                parent.put(neighbor, current);
                steps.add(AlgorithmStep.setParent(neighbor, current));

                if (dfsRecursive(graph, neighbor, targetId, visited, parent, visitOrder, steps)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Contains the results of a DFS execution.
     */
    public static class DfsResult {
        private final boolean found;
        private final List<Integer> visitOrder;
        private final Map<Integer, Integer> parent;
        private final List<AlgorithmStep> steps;

        public DfsResult(boolean found, List<Integer> visitOrder, Map<Integer, Integer> parent,
                List<AlgorithmStep> steps) {
            this.found = found;
            this.visitOrder = new ArrayList<>(visitOrder);
            this.parent = new HashMap<>(parent);
            this.steps = new ArrayList<>(steps);
        }

        /** Returns true if the target was found (or no target was specified). */
        public boolean isFound() {
            return this.found;
        }

        /** Returns the order in which nodes were visited. */
        public List<Integer> getVisitOrder() {
            return this.visitOrder;
        }

        /** Returns a map from each node to its parent in the DFS tree. */
        public Map<Integer, Integer> getParent() {
            return this.parent;
        }

        /** Returns the list of algorithm steps for visualization. */
        public List<AlgorithmStep> getSteps() {
            return this.steps;
        }
    }

}
