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

public class DFS {

    private DFS() {

    }

    public static DfsResult run(Graph graph, int startId) {
        return runDfs(graph, startId, null);
    }

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

        public boolean isFound() {
            return this.found;
        }

        public List<Integer> getVisitOrder() {
            return this.visitOrder;
        }

        public Map<Integer, Integer> getParent() {
            return this.parent;
        }

        public List<AlgorithmStep> getSteps() {
            return this.steps;
        }
    }

}
