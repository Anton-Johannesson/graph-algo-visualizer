package algorithm_visualizer.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import algorithm_visualizer.algorithms.EdmondsKarp.EdmondsKarpResult;
import algorithm_visualizer.model.Edge;
import algorithm_visualizer.model.Graph;
import algorithm_visualizer.model.Position;
import algorithm_visualizer.steps.AlgorithmStep;
import algorithm_visualizer.steps.StepType;

/**
 * Verifies that UPDATE_FLOW steps carry the total flow on an edge, not the
 * delta of the latest augmentation.
 */
public class EdmondsKarpFlowStepsTest {

    private static final double EPS = 1e-6;

    private static Graph build(int nodeCount, double[][] edges) {
        Graph g = new Graph();
        for (int i = 0; i < nodeCount; i++) {
            g.addNode(i, new Position(i, 0.0));
        }
        for (double[] e : edges) {
            g.addEdge(new Edge((int) e[0], (int) e[1], 0.0, e[2]));
        }
        return g;
    }

    /** Last emitted flow per edge, keyed "from->to". */
    private static Map<String, Double> lastFlows(List<AlgorithmStep> steps) {
        Map<String, Double> flows = new HashMap<>();
        for (AlgorithmStep s : steps) {
            if (s.getType() == StepType.UPDATE_FLOW) {
                flows.put(s.getFromId() + "->" + s.getToId(), s.getValue());
            }
        }
        return flows;
    }

    private static double flow(Map<String, Double> flows, int from, int to) {
        return flows.getOrDefault(from + "->" + to, 0.0);
    }

    @Test
    public void singleEdgeReportsTotalFlow() {
        Graph g = build(2, new double[][] { { 0, 1, 10 } });

        EdmondsKarpResult result = EdmondsKarp.run(g, 0, 1);

        assertEquals(10.0, flow(lastFlows(result.getSteps()), 0, 1), EPS);
    }

    @Test
    public void sharedEdgeAccumulatesFlowAcrossAugmentations() {
        // 0->1 (10) is shared by two augmenting paths: 0-1-3 (4) and 0-1-2-3 (6)
        Graph g = build(4, new double[][] {
                { 0, 1, 10 }, { 1, 3, 4 }, { 1, 2, 6 }, { 2, 3, 6 } });

        EdmondsKarpResult result = EdmondsKarp.run(g, 0, 3);
        Map<String, Double> flows = lastFlows(result.getSteps());

        assertEquals(10.0, result.getMaxFlow(), EPS);
        assertEquals(10.0, flow(flows, 0, 1), EPS);
        assertEquals(4.0, flow(flows, 1, 3), EPS);
        assertEquals(6.0, flow(flows, 1, 2), EPS);
        assertEquals(6.0, flow(flows, 2, 3), EPS);
    }

    @Test
    public void finalFlowsAreConsistentWithOpposingEdges() {
        double[][] edges = {
                { 0, 1, 10 }, { 0, 2, 10 }, { 1, 2, 2 },
                { 2, 1, 6 }, { 1, 3, 4 }, { 2, 3, 9 } };
        Graph g = build(4, edges);

        EdmondsKarpResult result = EdmondsKarp.run(g, 0, 3);
        Map<String, Double> flows = lastFlows(result.getSteps());

        // Capacity respected
        for (double[] e : edges) {
            double f = flow(flows, (int) e[0], (int) e[1]);
            assertTrue("flow below 0 on " + e[0] + "->" + e[1], f >= -EPS);
            assertTrue("flow above capacity on " + e[0] + "->" + e[1], f <= e[2] + EPS);
        }

        // Conservation at intermediate nodes
        for (int node = 1; node <= 2; node++) {
            double in = 0.0;
            double out = 0.0;
            for (double[] e : edges) {
                double f = flow(flows, (int) e[0], (int) e[1]);
                if ((int) e[1] == node) {
                    in += f;
                }
                if ((int) e[0] == node) {
                    out += f;
                }
            }
            assertEquals("conservation at node " + node, in, out, EPS);
        }

        // Inflow to the sink equals the max flow
        double sinkIn = 0.0;
        for (double[] e : edges) {
            if ((int) e[1] == 3) {
                sinkIn += flow(flows, (int) e[0], 3);
            }
        }
        assertEquals(13.0, result.getMaxFlow(), EPS);
        assertEquals(result.getMaxFlow(), sinkIn, EPS);
    }
}
