package team02.project.algorithm;

import lombok.val;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;

import java.util.LinkedList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelBranchAndBound implements SchedulingAlgorithm {
    private static final int SYNC_COUNT = 10000;
    private static final int SEARCH_THRESHOLD = 10;

    private int ubound;
    private PartialSolution best = null;
    private SolutionSpace solutionSpace;
    private int parallelism;

    public ParallelBranchAndBound(SolutionSpace solutionSpace) {
        this.solutionSpace = solutionSpace;
        this.parallelism = Runtime.getRuntime().availableProcessors(); // default is max
    }

    public ParallelBranchAndBound(SolutionSpace solutionSpace, int numProcessors){
        this.solutionSpace = solutionSpace;
        this.parallelism = numProcessors;
    }


    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {
        ; // this should be passed in later
        Schedule simpleListSchedule = new TopologicalSortAlgorithm().calculateOptimal(ctx);
        ubound = simpleListSchedule.getFinishTime();
        LinkedList<PartialSolution> scheduleStack = new LinkedList<>();
        scheduleStack.add(solutionSpace.getRoot(ctx));
        ForkSearch fs = new ForkSearch(scheduleStack);
        ForkJoinPool pool = new ForkJoinPool(parallelism);
        pool.invoke(fs);
        if (best == null) {
            return simpleListSchedule;
        } else {
            return best.makeComplete();
        }
    }

    private class ForkSearch extends RecursiveAction {
        LinkedList<PartialSolution> scheduleStack;
        private int localBound = ubound;
        private int syncCounter = 0;

        private ForkSearch(LinkedList<PartialSolution> scheduleStack) {
            this.scheduleStack = scheduleStack;
        }

        @Override
        protected void compute() {
            while (!scheduleStack.isEmpty()) {
                val schedule = scheduleStack.pop();
                if (schedule.isComplete()) { // don't expand
                    int estimate = schedule.getEstimatedFinishTime();
                    if (estimate < localBound) { // update the upper bound
                        localBound = estimate;
                        updateBest(schedule);
                    }
                    continue;
                }

                val children = schedule.expand(); // branch
                for (val child : children) {
                    if (child.getEstimatedFinishTime() < localBound) { // bound
                        scheduleStack.push(child);
                    }
                }

                if (scheduleStack.size() > SEARCH_THRESHOLD) {
                    // if my portion of the work is to big
                    LinkedList<PartialSolution> takeThis = new LinkedList<>();
                    takeThis.add(scheduleStack.removeLast());
                    invokeAll(new ForkSearch(scheduleStack), new ForkSearch(takeThis));
                }

                syncCounter++;
                if(syncCounter > SYNC_COUNT) {
                    syncCounter = 0;
                    localBound = ubound;
                }
            }
        }

        private synchronized void updateBest(PartialSolution p) {
            // check best again in case of multiple threads
            val estimate = p.getEstimatedFinishTime();
            if(estimate < ubound) {
                ubound = estimate;
                best = p;
            }
        }
    }
}
