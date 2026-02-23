package algorithm_visualizer.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import algorithm_visualizer.algorithms.Dijkstra.DijkstraResult;
import algorithm_visualizer.model.Graph;
import algorithm_visualizer.model.Position;
import algorithm_visualizer.steps.AlgorithmStep;
import algorithm_visualizer.steps.StepType;

public class DijkstraTest {

    private Graph graph;

    @Before
    public void setUp() {
        graph = new Graph();
    }

    @Test
    public void shouldFindTargetInSimpleGraph() {
        // Graf: 0 --(1.0)--> 1 --(2.0)--> 2
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(2.0, 2.0));
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 2.0);

        DijkstraResult result = Dijkstra.run(graph, 0, 2);

        assertTrue(result.isFound());
        assertEquals(Double.valueOf(3.0), result.getDistance().get(2));
        assertEquals(Integer.valueOf(1), result.getParent().get(2));
        assertEquals(Integer.valueOf(0), result.getParent().get(1));
    }

    @Test
    public void shouldFindShortestPathWithDifferentWeights() {
        // Graf: 0 --(10.0)--> 2, 0 --(1.0)--> 1 --(1.0)--> 2
        // Shortest path is 0 -> 1 -> 2 (total: 2.0)
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 0.0));
        graph.addNode(2, new Position(2.0, 0.0));
        graph.addEdge(0, 2, 10.0); // Direct but expensive
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);

        DijkstraResult result = Dijkstra.run(graph, 0, 2);

        assertTrue(result.isFound());
        assertEquals(Double.valueOf(2.0), result.getDistance().get(2));
        // Shortest path is through node 1
        assertEquals(Integer.valueOf(1), result.getParent().get(2));
    }

    @Test
    public void shouldNotFindUnreachableTarget() {
        // Graf: 0 -> 1, 2 (isolated)
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(2.0, 2.0));
        graph.addEdge(0, 1, 1.0);

        DijkstraResult result = Dijkstra.run(graph, 0, 2);

        assertFalse(result.isFound());
        assertFalse(result.getParent().containsKey(2));
    }

    @Test
    public void shouldGenerateStepsInCorrectOrder() {
        // Graf: 0 -> 1 -> 2
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(2.0, 2.0));
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);

        DijkstraResult result = Dijkstra.run(graph, 0, 2);

        assertNotNull(result.getSteps());
        assertTrue(result.getSteps().size() > 0);

        // First step should be VISIT_NODE for start node
        AlgorithmStep firstStep = result.getSteps().get(0);
        assertEquals(StepType.VISIT_NODE, firstStep.getType());
        assertEquals(Integer.valueOf(0), firstStep.getNodeId());

        // Last step should be DONE
        AlgorithmStep lastStep = result.getSteps().get(result.getSteps().size() - 1);
        assertEquals(StepType.DONE, lastStep.getType());
    }

    @Test
    public void shouldGenerateRelaxEdgeSteps() {
        // Graf: 0 -> 1
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addEdge(0, 1, 5.0);

        DijkstraResult result = Dijkstra.run(graph, 0);

        // Should have a RELAX_EDGE step
        boolean hasRelaxStep = result.getSteps().stream()
                .anyMatch(step -> step.getType() == StepType.RELAX_EDGE);
        assertTrue("Should have RELAX_EDGE step", hasRelaxStep);

        // Verify the relax step details
        AlgorithmStep relaxStep = result.getSteps().stream()
                .filter(step -> step.getType() == StepType.RELAX_EDGE)
                .findFirst()
                .orElse(null);
        assertNotNull(relaxStep);
        assertEquals(Integer.valueOf(0), relaxStep.getFromId());
        assertEquals(Integer.valueOf(1), relaxStep.getToId());
        assertEquals(Double.valueOf(5.0), relaxStep.getValue());
    }

    @Test
    public void shouldVisitAllReachableNodesWhenNoTarget() {
        // Graf: 0 -> 1, 0 -> 2
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(2.0, 2.0));
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(0, 2, 2.0);

        DijkstraResult result = Dijkstra.run(graph, 0);

        assertTrue(result.isFound());
        assertEquals(3, result.getVisitOrder().size());
        assertEquals(Double.valueOf(0.0), result.getDistance().get(0));
        assertEquals(Double.valueOf(1.0), result.getDistance().get(1));
        assertEquals(Double.valueOf(2.0), result.getDistance().get(2));
    }

    @Test
    public void shouldBuildCorrectParentTree() {
        // Graf: 0 -> 1, 0 -> 2, 1 -> 3
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(2.0, 2.0));
        graph.addNode(3, new Position(3.0, 3.0));
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(0, 2, 1.0);
        graph.addEdge(1, 3, 1.0);

        DijkstraResult result = Dijkstra.run(graph, 0);

        assertEquals(Integer.valueOf(0), result.getParent().get(1));
        assertEquals(Integer.valueOf(0), result.getParent().get(2));
        assertEquals(Integer.valueOf(1), result.getParent().get(3));
    }

    @Test
    public void shouldHandleDiamondGraphCorrectly() {
        // Path 0->1->3 = 2+5 = 7
        // Path 0->2->3 = 1+1 = 2 (shorter!)
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(1.0, -1.0));
        graph.addNode(3, new Position(2.0, 0.0));
        graph.addEdge(0, 1, 2.0);
        graph.addEdge(0, 2, 1.0);
        graph.addEdge(1, 3, 5.0);
        graph.addEdge(2, 3, 1.0);

        DijkstraResult result = Dijkstra.run(graph, 0, 3);

        assertTrue(result.isFound());
        assertEquals(Double.valueOf(2.0), result.getDistance().get(3));
        assertEquals(Integer.valueOf(2), result.getParent().get(3)); // Came from node 2
    }

    @Test
    public void shouldHandleSingleNodeGraph() {
        graph.addNode(0, new Position(0.0, 0.0));

        DijkstraResult result = Dijkstra.run(graph, 0, 0);

        assertTrue(result.isFound());
        assertEquals(Double.valueOf(0.0), result.getDistance().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenStartNodeDoesNotExist() {
        graph.addNode(0, new Position(0.0, 0.0));
        Dijkstra.run(graph, 99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenTargetNodeDoesNotExist() {
        graph.addNode(0, new Position(0.0, 0.0));
        Dijkstra.run(graph, 0, 99);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenGraphIsNull() {
        Dijkstra.run(null, 0);
    }
}
