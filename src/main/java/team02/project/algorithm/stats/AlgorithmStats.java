package team02.project.algorithm.stats;

import lombok.Value;
import team02.project.algorithm.Schedule;

import java.util.Optional;

@Value
public class AlgorithmStats {
    Schedule currentBest;
    long allocationsExpanded;
    long orderingsExpanded;
    long completeSchedules;
}
