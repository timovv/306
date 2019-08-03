package team02.project.graph;

import java.util.Set;

public interface TaskGraph {
    Set<Integer> getStartingTaskIds();
    Task findTaskById(int id);
}
