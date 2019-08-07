package team02.project.algorithm;

import lombok.Value;
import team02.project.graph.Graph;

@Value
public class SchedulingContext {
    Graph taskGraph;
    int processorCount;
}
