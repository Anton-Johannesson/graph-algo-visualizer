package algorithm_visualizer.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class AlgorithmStepTest {

    @Test
    public void shouldCreateStepFromConstructor() {
        AlgorithmStep step = new AlgorithmStep(
                StepType.VISIT_NODE,
                3,
                null,
                null,
                null,
                "Visit node 3");

        assertEquals(StepType.VISIT_NODE, step.getType());
        assertEquals(Integer.valueOf(3), step.getNodeId());
        assertNull(step.getFromId());
        assertNull(step.getToId());
        assertNull(step.getValue());
        assertEquals("Visit node 3", step.getMessage());
    }

    @Test
    public void shouldCreateVisitNodeStep() {
        AlgorithmStep step = AlgorithmStep.visitNode(5);

        assertEquals(StepType.VISIT_NODE, step.getType());
        assertEquals(Integer.valueOf(5), step.getNodeId());
        assertNull(step.getFromId());
        assertNull(step.getToId());
        assertNull(step.getValue());
        assertTrue(step.getMessage().contains("5"));
    }

    @Test
    public void shouldCreateSetParentStep() {
        AlgorithmStep step = AlgorithmStep.setParent(4, 1);

        assertEquals(StepType.SET_PARENT, step.getType());
        assertEquals(Integer.valueOf(4), step.getNodeId());
        assertEquals(Integer.valueOf(1), step.getFromId());
        assertEquals(Integer.valueOf(4), step.getToId());
        assertNull(step.getValue());
    }

    @Test
    public void shouldCreateRelaxEdgeStep() {
        AlgorithmStep step = AlgorithmStep.relaxEdge(2, 7, 9.5);

        assertEquals(StepType.RELAX_EDGE, step.getType());
        assertNull(step.getNodeId());
        assertEquals(Integer.valueOf(2), step.getFromId());
        assertEquals(Integer.valueOf(7), step.getToId());
        assertEquals(Double.valueOf(9.5), step.getValue());
        assertTrue(step.getMessage().contains("2"));
        assertTrue(step.getMessage().contains("7"));
    }

    @Test
    public void shouldCreateUpdateFlowStep() {
        AlgorithmStep step = AlgorithmStep.updateFlow(8, 9, 3.0);

        assertEquals(StepType.UPDATE_FLOW, step.getType());
        assertNull(step.getNodeId());
        assertEquals(Integer.valueOf(8), step.getFromId());
        assertEquals(Integer.valueOf(9), step.getToId());
        assertEquals(Double.valueOf(3.0), step.getValue());
    }

    @Test
    public void shouldCreateDoneStep() {
        AlgorithmStep step = AlgorithmStep.done("BFS complete");

        assertEquals(StepType.DONE, step.getType());
        assertNull(step.getNodeId());
        assertNull(step.getFromId());
        assertNull(step.getToId());
        assertNull(step.getValue());
        assertEquals("BFS complete", step.getMessage());
    }
}