package algorithm_visualizer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Graph {
    private final Map<Integer, Node> nodes;
    private final Map<Integer, List<Edge>> adjacanecy;
    private final boolean directed;

    public Graph() {
        this(true);
    }

    public Graph(boolean directed) {
        this.nodes = new HashMap<>();
        this.adjacanecy = new HashMap<>();
        this.directed = directed;
    }

    public void addNode(Node node) {
        Objects.requireNonNull(node, "node must not be null");
        nodes.put(node.getId(), node);
    }

    public Node addNode(int id, Position pos) {
        Node node = new Node(id, pos, "N" + id);
        nodes.put(id, node);
        return node;

    }

    public void addEdge(Edge edge) {
        Objects.requireNonNull(edge, "edge must not be null");
        requireNodeExists(edge.getFromId());
        requireNodeExists(edge.getToId());
        ensureAdjacencyList(edge.getFromId());

        adjacanecy.get(edge.getFromId()).add(edge);

        if (!directed) {
            Edge reverseEdge = new Edge(edge.getToId(), edge.getFromId(), edge.getWeight(), edge.getCapacity());
            ensureAdjacencyList(edge.getToId());
            adjacanecy.get(edge.getToId()).add(reverseEdge);
        }
    }

    public void addEdge(int fromId, int toId, double weight) {
        Edge edge = new Edge(fromId, toId, weight);
        addEdge(edge);

    }

    private void ensureAdjacencyList(int nodeId) {
        adjacanecy.putIfAbsent(nodeId, new ArrayList<>());
    }

    public boolean hasNode(int nodeId) {
        return nodes.containsKey(nodeId);
    }

    public List<Edge> neighbors(int nodeId) {
        if (!hasNode(nodeId)) {
            throw new IllegalArgumentException("Node does not exist");
        }
        return this.adjacanecy.getOrDefault(nodeId, new ArrayList<>());
    }

    public List<Edge> getEdgesFrom(int nodeId) {
        return neighbors(nodeId);
    }

    public Node getNode(int nodeId) {
        return this.nodes.get(nodeId);
    }

    public boolean hasEdge(int fromId, int toId) {
        if (!hasNode(fromId)) {
            return false;
        }
        List<Edge> edges = this.adjacanecy.get(fromId);
        if (edges == null) {
            return false;
        }
        for (Edge e : edges) {
            if (e.getToId() == toId) {
                return true;
            }
        }
        return false;
    }

    public Collection<Node> getNodes() {
        return this.nodes.values();
    }

    public int size() {
        return this.nodes.size();
    }

    public void clear() {
        this.nodes.clear();
        this.adjacanecy.clear();
    }

    private void requireNodeExists(int nodeId) {
        if (!hasNode(nodeId)) {
            throw new IllegalArgumentException("Node does not exist: " + nodeId);
        }
    }

}
