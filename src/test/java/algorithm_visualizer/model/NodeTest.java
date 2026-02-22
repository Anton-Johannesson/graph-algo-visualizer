package algorithm_visualizer.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

public class NodeTest {

    @Test
    public void shouldCreateNodeWithCustomLabel() {
        Position pos = new Position(10.0, 20.0);
        Node node = new Node(1, pos, "Start");

        assertEquals(1, node.getId());
        assertEquals("Start", node.getLabel());
        assertEquals(10.0, node.getX(), 0.0001);
        assertEquals(20.0, node.getY(), 0.0001);
    }

    @Test
    public void shouldUseDefaultLabelWhenNull() {
        Position pos = new Position(0.0, 0.0);
        Node node = new Node(5, pos, null);

        assertEquals("N5", node.getLabel());
    }

    @Test
    public void shouldUseDefaultLabelWhenBlank() {
        Position pos = new Position(0.0, 0.0);
        Node node = new Node(3, pos, "   ");

        assertEquals("N3", node.getLabel());
    }

    @Test
    public void shouldMoveNodeToNewPosition() {
        Position pos = new Position(0.0, 0.0);
        Node node = new Node(1, pos, "A");

        node.moveTo(5.0, 10.0);

        assertEquals(5.0, node.getX(), 0.0001);
        assertEquals(10.0, node.getY(), 0.0001);
    }

    @Test
    public void shouldCalculateDistanceBetweenNodes() {
        Node n1 = new Node(1, new Position(0.0, 0.0), "A");
        Node n2 = new Node(2, new Position(3.0, 4.0), "B");

        assertEquals(5.0, n1.distanceTo(n2), 0.0001);
    }

    @Test
    public void shouldCompareNodesByIdOnly() {
        Node n1 = new Node(7, new Position(0.0, 0.0), "A");
        Node n2 = new Node(7, new Position(100.0, 200.0), "B");
        Node n3 = new Node(8, new Position(0.0, 0.0), "A");

        assertEquals(n1, n2);
        assertEquals(n1.hashCode(), n2.hashCode());
        assertNotEquals(n1, n3);
    }
}
