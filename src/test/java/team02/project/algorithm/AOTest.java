package team02.project.algorithm;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import team02.project.TestUtils;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;
import team02.project.algorithm.solnspace.ao.AOCompleteSolution;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;
import team02.project.algorithm.solnspace.ao.APartialSolution;

import java.util.Set;
import java.util.Stack;

public class AOTest {

    private SolutionSpace solutionSpace;

    @Before
    public void setUp() {
        this.solutionSpace = new AOSolutionSpace();
    }

    @Test
    public void testSmallBoi() {
        SchedulingContext ctx = TestUtils.getContext("smol_boi.dot");
        Stack<PartialSolution> stack = new Stack<>();
        stack.push(APartialSolution.makeEmpty(ctx));

        Set<PartialSolution> firstExpand = stack.pop().expand();
        assertEquals(1, firstExpand.size());


        for (PartialSolution item : firstExpand) {
            stack.push(item);
        }

        Set<PartialSolution> secondExpand = stack.pop().expand();
        assertEquals(1, secondExpand.size());

        for (PartialSolution item : secondExpand) {
            stack.push(item);
        }

        Set<PartialSolution> thirdExpand = stack.pop().expand();
        assertEquals(1, thirdExpand.size());


        for (PartialSolution item : thirdExpand) {
            stack.push(item);
        }

        Set<PartialSolution> fourthExpand = stack.pop().expand();
        assertEquals(1, fourthExpand.size());

        for (PartialSolution item : fourthExpand) {
            stack.push(item);
        }

        // Allocation complete
        APartialSolution as = (APartialSolution) stack.pop();
        assertEquals(true, as.isCompleteAllocation());

        // We start with Ordering
        Set<PartialSolution> firstOExpand = as.expand();
        assertEquals(1, firstOExpand.size());

        for (PartialSolution item : firstOExpand) {
            stack.push(item);
        }

        Set<PartialSolution> secondOExpand = stack.pop().expand();
        assertEquals(1, secondOExpand.size());

        for (PartialSolution item : secondOExpand) {
            stack.push(item);
        }

        Set<PartialSolution> thirdOExpand = stack.pop().expand();
        assertEquals(2, thirdOExpand.size());

        for (PartialSolution item : thirdOExpand) {
            stack.push(item);
        }

        Set<PartialSolution> fourthOExpand = stack.pop().expand();
        assertEquals(1, fourthOExpand.size());

        for (PartialSolution item : fourthOExpand) {
            stack.push(item);
        }

        Set<PartialSolution> fifthOExpand = stack.pop().expand();
        assertEquals(1, fifthOExpand.size());

        for (PartialSolution item : fifthOExpand) {
            stack.push(item);
        }

        Set<PartialSolution> sixthOExpand = stack.pop().expand();
        assertEquals(1, sixthOExpand.size());

        for (PartialSolution item : sixthOExpand) {
            stack.push(item);
        }

        // Completed
        AOCompleteSolution seventhOExpand = (AOCompleteSolution) stack.pop();
        assertEquals(true, seventhOExpand.isComplete());
        assertEquals(9, seventhOExpand.getEstimatedFinishTime());
    }




}