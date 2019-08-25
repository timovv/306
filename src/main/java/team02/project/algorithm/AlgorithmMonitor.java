package team02.project.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AlgorithmMonitor {
    Schedule currentBest;
    long allocationsExpanded;
    long orderingsExpanded;
    long completeSchedules;
}
