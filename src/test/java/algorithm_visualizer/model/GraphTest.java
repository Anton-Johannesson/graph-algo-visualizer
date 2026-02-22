package algorithm_visualizer.model;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class GraphTest {

    private Graph graph;

    @Before
    public void setUp() {
        graph = new Graph();
    }

    @Test
    public void shouldCreateEmptyGraph() {
        assertEquals(0, graph.size());
    }

    @Test
    public void shouldAddNode() {
        Node node = new Node(1, new Position(0.0, 0.0), "A");
        graph.addNode(node);

        assertTrue(graph.hasNode(1));
        assertEquals(1, graph.size());
        assertEquals(node, graph.getNode(1));
    }

    @Test
    public void shouldAddNodeWithHelper() {
        Node node = graph.addNode(2, new Position(5.0, 5.0));

        assertNotNull(node);
        assertEquals(2, node.getId());
        assertTrue(graph.hasNode(2));
    }

    @Test
    public void shouldAddEdgeBetweenExistingNodes() {
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));

        graph.addEdge(0, 1, 2.5);

        assertTrue(graph.hasEdge(0, 1));
        List<Edge> edges = graph.neighbors(0);
        assertEquals(1, edges.size());
        assertEquals(0, edges.get(0).getFromId());
        assertEquals(1, edges.get(0).getToId());
        assertEquals(2.5, edges.get(0).getWeight(), 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenAddingEdgeWithNonexistentFromNode() {
        graph.addNode(1, new Position(0.0, 0.0));
        graph.addEdge(0, 1, 1.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenAddingEdgeWithNonexistentToNode() {
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addEdge(0, 1, 1.0);
    }

    @Test
    public void shouldReturnEmptyNeighborsForNodeWithNoEdges() {
        graph.addNode(0, new Position(0.0, 0.0));

        List<Edge> edges = graph.neighbors(0);

        assertNotNull(edges);
        assertEquals(0, edges.size());
    }

    @Test
    public void shouldHandleUndirectedGraphCorrectly() {
        Graph undirected = new Graph(false);
        undirected.addNode(0, new Position(0.0, 0.0));
        undirected.addNode(1, new Position(1.0, 1.0));

        undirected.addEdge(0, 1, 5.0);

        assertTrue(undirected.hasEdge(0, 1));
        assertTrue(undirected.hasEdge(1, 0));
        assertEquals(1, undirected.neighbors(0).size());
        assertEquals(1, undirected.neighbors(1).size());
    }

    @Test
    public void shouldNotCreateReverseEdgeInDirectedGraph() {
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));

        graph.addEdge(0, 1, 3.0);

        assertTrue(graph.hasEdge(0, 1));
        assertFalse(graph.hasEdge(1, 0));
    }

    @Test
    public void shouldClearGraph() {
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addEdge(0, 1, 1.0);

        graph.clear();

        assertEquals(0, graph.size());
        assertFalse(graph.hasNode(0));
        assertFalse(graph.hasNode(1));
    }

    @Test
    public void shouldReturnAllNodes() {
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(2.0, 2.0));

        assertEquals(3, graph.getNodes().size());
    }
}
