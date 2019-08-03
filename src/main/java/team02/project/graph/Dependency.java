package team02.project.graph;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
public class Dependency {
    Task from;
    Task to;
    @EqualsAndHashCode.Exclude int delay;
}
