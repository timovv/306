package team02.project.schedule;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TreeSchedule {
    TreeSchedule parent;
    int taskId;
    int processor;
    int startTime;
}
