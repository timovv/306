package team02.project.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.var;
import team02.project.graph.TaskGraph;
import team02.project.util.Parameters;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class Schedule implements Comparable<Schedule>{

    private static final int PROCESSOR_NUM = 0;
    private static final int START_TIME = 1;
    private static final int FINISH_TIME = 2;

    @Getter private final Schedule parent;
    @Getter private final int taskId;
    @Getter private final int processor;
    @Getter private final int startTime;

    public Set<Schedule> expand(TaskGraph taskGraph) {
        var completeSchedule = buildCompleteSchedule(taskGraph);
        var earliestProcessorAvailable = earliestProcessorAvailable(completeSchedule);
        var startingTaskIds = taskGraph.getStartingTaskIds();
        var result = new HashSet<Schedule>();

        outer: for (int id = 0; id < Parameters.getNumTasks(); id++) {
            // If this task has already been scheduled, ignore it
            if (completeSchedule[id][FINISH_TIME] != 0) continue;

            // If this task is a starting task, schedule in the next available slot
            if (startingTaskIds.contains(id)) {
                for (int procNum = 0; procNum < Parameters.getNumProcessors(); procNum++) {
                    result.add(new Schedule(this, id, procNum, earliestProcessorAvailable[procNum]));
                }
            }

            // Otherwise calculate earliest start times based on dependencies
            var earliestStartTimes = new int[Parameters.getNumProcessors()];
            for (var parentDep : taskGraph.findTaskById(id).getParents()) {
                int parentId = parentDep.getFrom().getId();

                // Missing dependency, skip this task
                if (completeSchedule[parentId][FINISH_TIME] == 0) continue outer;

                // Take into account communication delays
                for (int procNum = 0; procNum < Parameters.getNumProcessors(); procNum++) {
                    if (procNum != completeSchedule[parentId][PROCESSOR_NUM]) {
                        earliestStartTimes[procNum] = Math.max(earliestStartTimes[procNum],
                                completeSchedule[parentId][FINISH_TIME] + parentDep.getDelay()
                        );
                    }
                }
            }

            // Actual start times are the max between earliest start times (governed by
            // dependencies) and the earliest time processor is available
            for (int procNum = 0; procNum < Parameters.getNumProcessors(); procNum++) {
                result.add(new Schedule(this, id, procNum, Math.max(earliestProcessorAvailable[procNum],
                        earliestStartTimes[procNum])));
            }
        }
        return result;
    }

    private int[] earliestProcessorAvailable(int[][] completeSchedule) {
        var finishingTimes = new int[Parameters.getNumProcessors()];
        int procNum;
        for (int id = 0; id < Parameters.getNumTasks(); id++) {
            procNum = completeSchedule[id][PROCESSOR_NUM];
            finishingTimes[procNum] = Math.max(finishingTimes[procNum], completeSchedule[id][FINISH_TIME]);
        }
        return finishingTimes;
    }

    private int[][] buildCompleteSchedule(TaskGraph taskGraph) {
        var completeSchedule = new int[Parameters.getNumTasks()][3];
        var curr = this;
        int id;
        do {
            id = curr.getTaskId();
            completeSchedule[id][PROCESSOR_NUM] = processor;
            completeSchedule[id][START_TIME] = startTime;
            completeSchedule[id][FINISH_TIME] = startTime + taskGraph.findTaskById(taskId).getDuration();
        } while ((curr = curr.parent) != null);
        return completeSchedule;
    }

    @Override
    public int compareTo(Schedule o) {
        return 0;
    }
}
