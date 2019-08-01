package team02.project.graph;

import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Task {

    @Getter private final String id;
    @Getter private final Set<Dependency> dependents = new HashSet<>();
    @Getter private final int duration;

    public Task(String id, int duration) {
       this.id = id;
       this.duration = duration;
    }

    /**
     * {@link Task Tasks} with equal {@link #id} are considered equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
