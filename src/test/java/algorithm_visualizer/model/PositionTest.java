package algorithm_visualizer.model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PositionTest {

    @Test
    public void shouldCreatePositionWithCoordinates() {
        Position pos = new Position(10.0, 20.0);

        assertEquals(10.0, pos.x(), 0.0001);
        assertEquals(20.0, pos.y(), 0.0001);
    }

    @Test
    public void shouldCalculateDistanceBetweenPositions() {
        Position p1 = new Position(0.0, 0.0);
        Position p2 = new Position(3.0, 4.0);

        assertEquals(5.0, p1.distanceTo(p2), 0.0001);
        assertEquals(5.0, p2.distanceTo(p1), 0.0001);
    }

    @Test
    public void shouldCalculateZeroDistanceForSamePosition() {
        Position p1 = new Position(5.0, 7.0);
        Position p2 = new Position(5.0, 7.0);

        assertEquals(0.0, p1.distanceTo(p2), 0.0001);
    }
}
