package algorithm_visualizer.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GraphRemoveNodeTest {

    private static Graph graphWithNodes(boolean directed, int count) {
        Graph g = new Graph(directed);
        for (int i = 0; i < count; i++) {
            g.addNode(i, new Position(i, 0.0));
        }
        return g;
    }

    @Test
    public void removesNodeAndAllIncomingAndOutgoingEdges() {
        Graph g = graphWithNodes(true, 3);
        g.addEdge(0, 1, 1.0);
        g.addEdge(1, 2, 1.0);
        g.addEdge(2, 1, 1.0);
        g.addEdge(0, 2, 1.0);

        assertTrue(g.removeNode(1));

        assertFalse(g.hasNode(1));
        assertEquals(2, g.size());
        assertFalse(g.hasEdge(0, 1));
        assertFalse(g.hasEdge(2, 1));
        assertTrue(g.hasEdge(0, 2));
    }

    @Test
    public void returnsFalseForMissingNode() {
        Graph g = graphWithNodes(true, 2);

        assertFalse(g.removeNode(42));
        assertEquals(2, g.size());
    }

    @Test
    public void removesReverseEdgesInUndirectedGraph() {
        Graph g = graphWithNodes(false, 3);
        g.addEdge(0, 1, 1.0);
        g.addEdge(1, 2, 1.0);

        assertTrue(g.removeNode(1));

        assertTrue(g.neighbors(0).isEmpty());
        assertTrue(g.neighbors(2).isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void neighborsOfRemovedNodeThrows() {
        Graph g = graphWithNodes(true, 2);
        g.removeNode(1);

        g.neighbors(1);
    }
}
