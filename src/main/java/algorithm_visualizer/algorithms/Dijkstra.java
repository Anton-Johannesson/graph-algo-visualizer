package algorithm_visualizer.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

import algorithm_visualizer.model.Edge;
import algorithm_visualizer.model.Graph;
import algorithm_visualizer.steps.AlgorithmStep;

public class Dijkstra {

    private Dijkstra() {

    }

    public static DijkstraResult run(Graph graph, int startId) {
        return run(graph, startId, null);
    }

    public static DijkstraResult run(Graph graph, int startId, Integer targetId) {
        Objects.requireNonNull(graph, "graph must not be null");

        if (!graph.hasNode(startId)) {
            throw new IllegalArgumentException("Start node does not exist: " + startId);
        }
        if (targetId != null && !graph.hasNode(targetId)) {
            throw new IllegalArgumentException("Target node does not exist: " + targetId);
        }

        Comparator<PQElement> cmp = Comparator.comparingDouble(e -> e.distance);
        PriorityQueue<PQElement> queue = new PriorityQueue<>(cmp);

        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        List<Integer> visitOrder = new ArrayList<>();
        List<AlgorithmStep> steps = new ArrayList<>();

        queue.add(new PQElement(startId, 0.0));
        dist.put(startId, 0.0);

        boolean foundTarget = false;

        while (!queue.isEmpty()) {
            PQElement current = queue.poll();
            int nodeId = current.node;
            double d = current.distance;

            if (d > dist.getOrDefault(nodeId, Double.MAX_VALUE)) {
                continue;
            }

            visitOrder.add(nodeId);
            steps.add(AlgorithmStep.visitNode(nodeId));

            if (targetId != null && nodeId == targetId) {
                foundTarget = true;
                break;
            }

            for (Edge edge : graph.neighbors(nodeId)) {
                int neighbor = edge.getToId();
                double newDist = d + edge.getWeight();
                double currentDist = dist.getOrDefault(neighbor, Double.MAX_VALUE);

                if (newDist < currentDist) {
                    dist.put(neighbor, newDist);
                    parent.put(neighbor, nodeId);
                    queue.add(new PQElement(neighbor, newDist));
                    steps.add(AlgorithmStep.relaxEdge(nodeId, neighbor, newDist));
                }
            }
        }
        if (targetId == null) {
            foundTarget = true;
        }
        steps.add(AlgorithmStep.done(foundTarget ? "Dijkstra complete" : "Target not reachable"));

        return new DijkstraResult(foundTarget, visitOrder, parent, dist, steps);

    }

    private static class PQElement {
        private final int node;
        private double distance;

        PQElement(int node, double distnace) {
            this.node = node;
            this.distance = distnace;
        }
    }

    public static class DijkstraResult {
        private final boolean found;
        private final List<Integer> visitOrder;
        private final Map<Integer, Integer> parent;
        private final Map<Integer, Double> distance;
        private final List<AlgorithmStep> steps;

        public DijkstraResult(boolean found,
                List<Integer> visitOrder,
                Map<Integer, Integer> parent,
                Map<Integer, Double> distance,
                List<AlgorithmStep> steps) {
            this.found = found;
            this.visitOrder = new ArrayList<>(visitOrder);
            this.parent = new HashMap<>(parent);
            this.distance = new HashMap<>(distance);
            this.steps = new ArrayList<>(steps);
        }

        public boolean isFound() {
            return found;
        }

        public List<Integer> getVisitOrder() {
            return Collections.unmodifiableList(visitOrder);
        }

        public Map<Integer, Integer> getParent() {
            return Collections.unmodifiableMap(parent);
        }

        public Map<Integer, Double> getDistance() {
            return Collections.unmodifiableMap(distance);
        }

        public List<AlgorithmStep> getSteps() {
            return Collections.unmodifiableList(steps);
        }
    }
}
