package algorithm_visualizer.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import algorithm_visualizer.algorithms.EdmondsKarp.EdmondsKarpResult;
import algorithm_visualizer.model.Edge;
import algorithm_visualizer.model.Graph;
import algorithm_visualizer.model.Position;
import algorithm_visualizer.steps.AlgorithmStep;
import algorithm_visualizer.steps.StepType;

public class EdmondsKarpTest {

    private Graph graph;

    @Before
    public void setUp() {
        graph = new Graph();
    }

    @Test
    public void shouldFindMaxFlowInSimpleGraph() {
        // Simple graph: 0 --(10)--> 1
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 0.0));
        graph.addEdge(new Edge(0, 1, 0.0, 10.0));

        EdmondsKarpResult result = EdmondsKarp.run(graph, 0, 1);

        assertEquals(10.0, result.getMaxFlow(), 0.001);
        assertEquals(1, result.getAugmentingPaths().size());
    }

    @Test
    public void shouldFindMaxFlowWithMultiplePaths() {
        // Graph with two parallel paths:
        // 1
        // / \
        // 5 5
        // / \
        // 0 3
        // \ /
        // 5 5
        // \ /
        // 2
        // Max flow = 10 (5 through each path)
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(1.0, -1.0));
        graph.addNode(3, new Position(2.0, 0.0));
        graph.addEdge(new Edge(0, 1, 0.0, 5.0));
        graph.addEdge(new Edge(0, 2, 0.0, 5.0));
        graph.addEdge(new Edge(1, 3, 0.0, 5.0));
        graph.addEdge(new Edge(2, 3, 0.0, 5.0));

        EdmondsKarpResult result = EdmondsKarp.run(graph, 0, 3);

        assertEquals(10.0, result.getMaxFlow(), 0.001);
    }

    @Test
    public void shouldHandleBottleneck() {
        // Graph: 0 --(10)--> 1 --(5)--> 2
        // Bottleneck is edge 1->2 with capacity 5
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 0.0));
        graph.addNode(2, new Position(2.0, 0.0));
        graph.addEdge(new Edge(0, 1, 0.0, 10.0));
        graph.addEdge(new Edge(1, 2, 0.0, 5.0));

        EdmondsKarpResult result = EdmondsKarp.run(graph, 0, 2);

        assertEquals(5.0, result.getMaxFlow(), 0.001);
    }

    @Test
    public void shouldReturnZeroWhenNoPath() {
        // Graph: 0, 1 (disconnected)
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 0.0));

        EdmondsKarpResult result = EdmondsKarp.run(graph, 0, 1);

        assertEquals(0.0, result.getMaxFlow(), 0.001);
        assertTrue(result.getAugmentingPaths().isEmpty());
    }

    @Test
    public void shouldFindMaxFlowInClassicExample() {
        // Classic max-flow example:
        // 1 ---8---> 3
        // /| /|
        // 10 |2 |10
        // / v 6 v
        // 0 4 -----> 5
        // \ ^ ^
        // 10 |9 |10
        // \| |
        // 2 ---9--> (to 4)
        // This is a simplified version
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(1.0, -1.0));
        graph.addNode(3, new Position(2.0, 0.0));
        graph.addEdge(new Edge(0, 1, 0.0, 10.0));
        graph.addEdge(new Edge(0, 2, 0.0, 10.0));
        graph.addEdge(new Edge(1, 3, 0.0, 10.0));
        graph.addEdge(new Edge(2, 3, 0.0, 10.0));
        graph.addEdge(new Edge(1, 2, 0.0, 5.0)); // Cross edge

        EdmondsKarpResult result = EdmondsKarp.run(graph, 0, 3);

        assertEquals(20.0, result.getMaxFlow(), 0.001);
    }

    @Test
    public void shouldUseWeightAsCapacityWhenCapacityIsZero() {
        // When capacity is 0, algorithm should fall back to weight
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 0.0));
        graph.addEdge(0, 1, 7.0); // weight=7, capacity=0 (default)

        EdmondsKarpResult result = EdmondsKarp.run(graph, 0, 1);

        assertEquals(7.0, result.getMaxFlow(), 0.001);
    }

    @Test
    public void shouldGenerateSteps() {
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 0.0));
        graph.addEdge(new Edge(0, 1, 0.0, 10.0));

        EdmondsKarpResult result = EdmondsKarp.run(graph, 0, 1);

        assertNotNull(result.getSteps());
        assertFalse(result.getSteps().isEmpty());

        // Should have VISIT_NODE steps
        boolean hasVisitStep = result.getSteps().stream()
                .anyMatch(step -> step.getType() == StepType.VISIT_NODE);
        assertTrue("Should have VISIT_NODE step", hasVisitStep);

        // Should have UPDATE_FLOW step
        boolean hasFlowStep = result.getSteps().stream()
                .anyMatch(step -> step.getType() == StepType.UPDATE_FLOW);
        assertTrue("Should have UPDATE_FLOW step", hasFlowStep);

        // Last step should be DONE
        AlgorithmStep lastStep = result.getSteps().get(result.getSteps().size() - 1);
        assertEquals(StepType.DONE, lastStep.getType());
    }

    @Test
    public void shouldTrackAugmentingPaths() {
        // Two-path graph
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(1.0, -1.0));
        graph.addNode(3, new Position(2.0, 0.0));
        graph.addEdge(new Edge(0, 1, 0.0, 5.0));
        graph.addEdge(new Edge(0, 2, 0.0, 5.0));
        graph.addEdge(new Edge(1, 3, 0.0, 5.0));
        graph.addEdge(new Edge(2, 3, 0.0, 5.0));

        EdmondsKarpResult result = EdmondsKarp.run(graph, 0, 3);

        // Should have found 2 augmenting paths
        assertEquals(2, result.getAugmentingPaths().size());

        // Each path should start at source and end at sink
        for (var path : result.getAugmentingPaths()) {
            assertEquals(Integer.valueOf(0), path.get(0));
            assertEquals(Integer.valueOf(3), path.get(path.size() - 1));
        }
    }

    @Test
    public void shouldHandleFlowCancellation() {
        // Graph where flow cancellation is needed for optimal solution:
        // 1
        // /|\
        // 1 | 1
        // / | \
        // 0 1 3
        // \ | /
        // 1 | 1
        // \|/
        // 2
        // Without flow cancellation, greedy approach might get stuck
        graph.addNode(0, new Position(0.0, 0.0));
        graph.addNode(1, new Position(1.0, 1.0));
        graph.addNode(2, new Position(1.0, -1.0));
        graph.addNode(3, new Position(2.0, 0.0));
        graph.addEdge(new Edge(0, 1, 0.0, 1.0));
        graph.addEdge(new Edge(0, 2, 0.0, 1.0));
        graph.addEdge(new Edge(1, 2, 0.0, 1.0)); // Cross edge
        graph.addEdge(new Edge(1, 3, 0.0, 1.0));
        graph.addEdge(new Edge(2, 3, 0.0, 1.0));

        EdmondsKarpResult result = EdmondsKarp.run(graph, 0, 3);

        // Max flow should be 2 (both paths from 0 can reach 3)
        assertEquals(2.0, result.getMaxFlow(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSourceDoesNotExist() {
        graph.addNode(0, new Position(0.0, 0.0));
        EdmondsKarp.run(graph, 99, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSinkDoesNotExist() {
        graph.addNode(0, new Position(0.0, 0.0));
        EdmondsKarp.run(graph, 0, 99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSourceEqualsSink() {
        graph.addNode(0, new Position(0.0, 0.0));
        EdmondsKarp.run(graph, 0, 0);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenGraphIsNull() {
        EdmondsKarp.run(null, 0, 1);
    }
}
