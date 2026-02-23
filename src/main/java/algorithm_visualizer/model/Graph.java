package algorithm_visualizer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a graph data structure supporting both directed and undirected
 * graphs.
 * <p>
 * The graph stores nodes and edges using adjacency lists. Nodes are identified
 * by unique integer IDs. The graph can be directed (default) or undirected.
 * </p>
 */
public class Graph {
    private final Map<Integer, Node> nodes;
    private final Map<Integer, List<Edge>> adjacanecy;
    private final boolean directed;

    /**
     * Creates an empty directed graph.
     */
    public Graph() {
        this(true);
    }

    /**
     * Creates an empty graph.
     *
     * @param directed true for directed graph, false for undirected
     */
    public Graph(boolean directed) {
        this.nodes = new HashMap<>();
        this.adjacanecy = new HashMap<>();
        this.directed = directed;
    }

    /**
     * Adds an existing node to the graph.
     *
     * @param node the node to add
     */
    public void addNode(Node node) {
        Objects.requireNonNull(node, "node must not be null");
        nodes.put(node.getId(), node);
    }

    /**
     * Creates and adds a new node with the given ID and position.
     *
     * @param id  the node ID
     * @param pos the node position
     * @return the created node
     */
    public Node addNode(int id, Position pos) {
        Node node = new Node(id, pos, "N" + id);
        nodes.put(id, node);
        return node;

    }

    /**
     * Adds an existing edge to the graph.
     * For undirected graphs, also adds the reverse edge.
     *
     * @param edge the edge to add
     * @throws IllegalArgumentException if either endpoint node does not exist
     */
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

    /**
     * Creates and adds a new weighted edge.
     *
     * @param fromId the source node ID
     * @param toId   the target node ID
     * @param weight the edge weight
     */
    public void addEdge(int fromId, int toId, double weight) {
        Edge edge = new Edge(fromId, toId, weight);
        addEdge(edge);

    }

    private void ensureAdjacencyList(int nodeId) {
        adjacanecy.putIfAbsent(nodeId, new ArrayList<>());
    }

    /**
     * Checks if a node exists in the graph.
     *
     * @param nodeId the node ID to check
     * @return true if the node exists
     */
    public boolean hasNode(int nodeId) {
        return nodes.containsKey(nodeId);
    }

    /**
     * Returns the edges originating from the specified node.
     *
     * @param nodeId the node ID
     * @return list of outgoing edges
     * @throws IllegalArgumentException if the node does not exist
     */
    public List<Edge> neighbors(int nodeId) {
        if (!hasNode(nodeId)) {
            throw new IllegalArgumentException("Node does not exist");
        }
        return this.adjacanecy.getOrDefault(nodeId, new ArrayList<>());
    }

    /**
     * Alias for {@link #neighbors(int)}.
     *
     * @param nodeId the node ID
     * @return list of outgoing edges
     */
    public List<Edge> getEdgesFrom(int nodeId) {
        return neighbors(nodeId);
    }

    /**
     * Returns the node with the specified ID.
     *
     * @param nodeId the node ID
     * @return the node, or null if not found
     */
    public Node getNode(int nodeId) {
        return this.nodes.get(nodeId);
    }

    /**
     * Checks if an edge exists between two nodes.
     *
     * @param fromId the source node ID
     * @param toId   the target node ID
     * @return true if the edge exists
     */
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

    /** Returns all nodes in the graph. */
    public Collection<Node> getNodes() {
        return this.nodes.values();
    }

    /** Returns the number of nodes in the graph. */
    public int size() {
        return this.nodes.size();
    }

    /** Removes all nodes and edges from the graph. */
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
