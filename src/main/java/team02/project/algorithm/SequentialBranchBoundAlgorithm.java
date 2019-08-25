package team02.project.algorithm;

import javafx.application.Platform;
import lombok.val;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;
import team02.project.algorithm.solnspace.ao.APartialSolution;
import team02.project.algorithm.stats.AlgorithmStats;
import team02.project.algorithm.stats.AlgorithmStatsListener;
import team02.project.graph.Node;

import java.util.LinkedList;
import java.util.Optional;

/**
 * Bounds any complete schedules which exceed the current upper bound
 */
public class SequentialBranchBoundAlgorithm implements SchedulingAlgorithm {

    private SolutionSpace solutionSpace;
    private Optional<AlgorithmStatsListener> listener;
    private static final long VISUALIZATION_REFRESH = 10000;

    public SequentialBranchBoundAlgorithm(SolutionSpace solutionSpace, Optional<AlgorithmStatsListener> listener) {
        this.solutionSpace = solutionSpace;
        this.listener = listener;
    }


    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {
        Schedule simpleListSchedule = new TopologicalSortAlgorithm().calculateOptimal(ctx);
        int ubound = simpleListSchedule.getFinishTime();
        PartialSolution best = null;

        long scheduleCreated = 0;
        long allocationsExpanded = 0;
        long orderingsExpanded = 0;
        int iteration = 0;

        LinkedList<PartialSolution> scheduleStack = new LinkedList<>();
        scheduleStack.add(solutionSpace.getRoot(ctx));
        while(!scheduleStack.isEmpty()) {
            val schedule = scheduleStack.pop();
            if (schedule.isComplete()) { // don't expand
                scheduleCreated++;
                val estimate = schedule.getEstimatedFinishTime();
                if(estimate < ubound) { // update the upper bound
                    best = schedule;
                    ubound = estimate;
                }
                continue;
            }

            if (listener.isPresent()) {
                if (schedule instanceof APartialSolution) {
                    allocationsExpanded++;
                } else {
                    orderingsExpanded++;
                }
            }

            val children = schedule.expand(); // branch
            for(val child : children) {
                if(child.getEstimatedFinishTime() < ubound) { // bound
                    scheduleStack.push(child);
                }
            }

            if (iteration == VISUALIZATION_REFRESH && listener.isPresent()) {
                // todo there is probably a cleaner way to do this
                if (best != null) {
                    // copy em for the other thread
                    long finalAllocationsExpanded = allocationsExpanded;
                    long finalOrderingsExpanded = orderingsExpanded;
                    long finalScheduleCreated = scheduleCreated;
                    PartialSolution finalBest = best;
                    Platform.runLater(() -> listener.get().update(new AlgorithmStats(finalBest.makeComplete(),
                            finalAllocationsExpanded, finalOrderingsExpanded, finalScheduleCreated)));
                }
                iteration = 0;
            }
            iteration++;
        }

        if(best == null) {
            return simpleListSchedule;
        }

        return best.makeComplete();
    }
}
