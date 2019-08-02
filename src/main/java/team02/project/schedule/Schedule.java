package team02.project.schedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import team02.project.graph.Task;
import team02.project.util.Parameters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Schedule {

    @Getter private final Schedule parent;
    @Getter private final Set<Task> assignedTasks;
    @Getter private final Task[] latestTasks;
    @Getter private final int[] latestFinishTimes;

    public Schedule(Schedule parent, Set<Task> assignedTasks, Task[] latestTasks, int[] latestFinishTimes) {
        this.parent = parent;
        this.assignedTasks = assignedTasks;
        this.latestTasks = latestTasks;
        this.latestFinishTimes = latestFinishTimes;
    }

//    public Set<Schedule> expand() {
//        var children = new HashSet<>();
//        for (int i = 0; i < Parameters.getNumProcessors(); i++) {
//
//        }
//    }


    @RequiredArgsConstructor
    public static class Allocation {
        @Getter private final int processor;
        @Getter private final int startTime;
    }

}
