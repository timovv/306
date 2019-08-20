package team02.project.algorithm;

import lombok.val;
import lombok.var;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;

import java.util.LinkedList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelBranchAndBound implements SchedulingAlgorithm {

    private AtomicInteger ubound = new AtomicInteger(Integer.MAX_VALUE);
    private SolutionSpace solutionSpace = new AOSolutionSpace();
    private static final int SEARCH_THRESHOLD = 10;
    private PartialSolution best = null;


    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {
        int parallelism = Runtime.getRuntime().availableProcessors(); // this should be passed in later
        LinkedList<PartialSolution> scheduleStack = new LinkedList<>();
        scheduleStack.add(solutionSpace.getRoot(ctx));
        ForkSearch fs = new ForkSearch(scheduleStack);
        ForkJoinPool pool = new ForkJoinPool(parallelism);
        pool.invoke(fs);
        return best.makeComplete();
    }

    private class ForkSearch extends RecursiveAction {
        LinkedList<PartialSolution> scheduleStack;

        private ForkSearch(LinkedList<PartialSolution> scheduleStack) {
            this.scheduleStack = scheduleStack;
        }

        @Override
        protected void compute() {
            while (!scheduleStack.isEmpty()) {
                val schedule = scheduleStack.pop();
                if (schedule.isComplete()) { // don't expand
                    int estimate = schedule.getEstimatedFinishTime();
                    if (estimate < ubound.get()) { // update the upper bound
                        updateBest(schedule);
                    }
                    continue;
                }

                val children = schedule.expand(); // branch
                for (val child : children) {
                    if (child.getEstimatedFinishTime() < ubound.get()) { // bound
                        scheduleStack.push(child);
                    }
                }

                if (scheduleStack.size() > SEARCH_THRESHOLD) {
                    // if my portion of the work is to big
                    LinkedList<PartialSolution> takeThis = new LinkedList<>();
                    takeThis.add(scheduleStack.removeLast());
                    invokeAll(new ForkSearch(scheduleStack), new ForkSearch(takeThis));
                }
            }
        }

        private synchronized void updateBest(PartialSolution p) {
            // check best again in case of multiple threads
            val estimate = p.getEstimatedFinishTime();
            if(estimate < ubound.get()) {
                ubound.getAndSet(estimate);
                best = p;
            }
        }
    }
}
