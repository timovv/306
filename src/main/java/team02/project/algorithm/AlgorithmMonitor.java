package team02.project.algorithm;

import lombok.Data;

@Data
public class AlgorithmMonitor {
    Schedule currentBest;
    long allocationsExpanded;
    long orderingsExpanded;
    long completeSchedules;
    boolean isFinished = false;

    public AlgorithmMonitor(Schedule currentBest, long allocationsExpanded, long orderingsExpanded, long completeSchedules) {
        this.currentBest = currentBest;
        this.allocationsExpanded = allocationsExpanded;
        this.orderingsExpanded = orderingsExpanded;
        this.completeSchedules = completeSchedules;
    }
}
