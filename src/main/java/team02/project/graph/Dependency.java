package team02.project.graph;

import lombok.Getter;

import java.util.Objects;

public class Dependency {

    @Getter private final Task from;
    @Getter private final Task to;
    @Getter private final int cost;

    public Dependency(Task from, Task to, int cost) {
        this.from = from;
        this.to = to;
        this.cost = cost;
    }

    /**
     * {@link Dependency Dependencies} with equal {@link #from} and {@link #to} are considered equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return from.equals(that.from) &&
                to.equals(that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
