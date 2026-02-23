package algorithm_visualizer.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import algorithm_visualizer.algorithms.DFS.DfsResult;
import algorithm_visualizer.model.Graph;
import algorithm_visualizer.model.Position;
import algorithm_visualizer.steps.AlgorithmStep;
import algorithm_visualizer.steps.StepType;

public class DFSTest {

    private Graph graph;

    @Before
    public void setUp() {
        graph = new Graph();
    }

    @Test
    public void shouldFindTargetInSimpleGraph() {
        // Graf: 0 -> 1 -> 2
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(2.0, 2.0));
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);

        DfsResult result = DFS.runDfs(graph, 0, 2);

        assertTrue(result.isFound());
        assertEquals(Integer.valueOf(1), result.getParent().get(2));
        assertEquals(Integer.valueOf(0), result.getParent().get(1));
    }

    @Test
    public void shouldNotFindUnreachableTarget() {
        // Graf: 0 -> 1, 2 (isolerad)
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(2.0, 2.0));
        graph.addEdge(0, 1, 1.0);

        DfsResult result = DFS.runDfs(graph, 0, 2);

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

        DfsResult result = DFS.runDfs(graph, 0, 2);

        assertNotNull(result.getSteps());
        assertTrue(result.getSteps().size() > 0);

        // Första steget ska vara VISIT_NODE för startnod
        AlgorithmStep firstStep = result.getSteps().get(0);
        assertEquals(StepType.VISIT_NODE, firstStep.getType());
        assertEquals(Integer.valueOf(0), firstStep.getNodeId());

        // Sista steget ska vara DONE
        AlgorithmStep lastStep = result.getSteps().get(result.getSteps().size() - 1);
        assertEquals(StepType.DONE, lastStep.getType());
    }

    @Test
    public void shouldVisitAllReachableNodesWhenNoTarget() {
        // Graf: 0 -> 1, 0 -> 2
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(2.0, 2.0));
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(0, 2, 1.0);

        DfsResult result = DFS.run(graph, 0);

        assertTrue(result.isFound());
        assertEquals(3, result.getVisitOrder().size());
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

        DfsResult result = DFS.run(graph, 0);

        assertEquals(Integer.valueOf(0), result.getParent().get(1));
        // DFS går djupt först, så den ska hitta 3 via 1 innan den besöker 2
        assertEquals(Integer.valueOf(1), result.getParent().get(3));
    }

    @Test
    public void shouldExploreDepthFirst() {
        // Graf: 0 -> 1 -> 3
        // |
        // v
        // 2
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(2.0, 2.0));
        graph.addNode(3, new Position(3.0, 3.0));
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(0, 2, 1.0);
        graph.addEdge(1, 3, 1.0);

        DfsResult result = DFS.run(graph, 0);

        // DFS ska besöka 0, sen 1, sen 3 (djup-först), sen 2
        // BFS skulle besöka 0, 1, 2, 3
        assertEquals(4, result.getVisitOrder().size());
        assertEquals(Integer.valueOf(0), result.getVisitOrder().get(0));
        assertEquals(Integer.valueOf(1), result.getVisitOrder().get(1));
        assertEquals(Integer.valueOf(3), result.getVisitOrder().get(2));
        assertEquals(Integer.valueOf(2), result.getVisitOrder().get(3));
    }

    @Test
    public void shouldStopWhenTargetFound() {
        // Graf: 0 -> 1 -> 3
        // |
        // v
        // 2
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(2.0, 2.0));
        graph.addNode(3, new Position(3.0, 3.0));
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(0, 2, 1.0);
        graph.addEdge(1, 3, 1.0);

        DfsResult result = DFS.runDfs(graph, 0, 3);

        // Ska stanna när 3 hittas och inte besöka 2
        assertTrue(result.isFound());
        assertTrue(result.getVisitOrder().contains(3));
        assertEquals(3, result.getVisitOrder().size()); // 0, 1, 3
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenStartNodeDoesNotExist() {
        graph.addNode(0, new Position(0.0, 0.0));
        DFS.run(graph, 99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenTargetNodeDoesNotExist() {
        graph.addNode(0, new Position(0.0, 0.0));
        DFS.runDfs(graph, 0, 99);
    }
}