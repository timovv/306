package team02.project.algorithm;

import lombok.Value;

import java.util.Set;

@Value
public class Schedule {
    Set<ScheduledTask> tasks;

    public int getFinishTime() {
        return tasks.stream().mapToInt(ScheduledTask::getFinishTime).max().orElse(0);
    }
}
