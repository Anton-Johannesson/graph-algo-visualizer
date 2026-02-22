package algorithm_visualizer.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class EdgeTest {

    @Test
    public void shouldCreateWeightedEdge() {
        Edge edge = new Edge(1, 2, 4.5);

        assertEquals(1, edge.getFromId());
        assertEquals(2, edge.getToId());
        assertEquals(4.5, edge.getWeight(), 0.0001);
        assertEquals(0.0, edge.getCapacity(), 0.0001);
        assertEquals(0.0, edge.getFlow(), 0.0001);
        assertFalse(edge.isFlowEdge());
    }

    @Test
    public void shouldCalculateResidualCapacity() {
        Edge edge = new Edge(1, 2, 1.0, 10.0);
        edge.addFlow(3.0);

        assertEquals(7.0, edge.residualCapacity(), 0.0001);
    }

    @Test
    public void shouldAddFlowWithinCapacity() {
        Edge edge = new Edge(1, 2, 1.0, 5.0);

        edge.addFlow(2.0);
        edge.addFlow(3.0);

        assertEquals(5.0, edge.getFlow(), 0.0001);
        assertEquals(0.0, edge.residualCapacity(), 0.0001);
        assertTrue(edge.isFlowEdge());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenFlowExceedsCapacity() {
        Edge edge = new Edge(1, 2, 1.0, 5.0);
        edge.addFlow(6.0);
    }
}