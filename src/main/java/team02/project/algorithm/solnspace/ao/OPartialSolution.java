package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.Schedule;
import team02.project.algorithm.solnspace.PartialSolution;

import java.util.Set;

public class OPartialSolution implements PartialSolution {
    @Override
    public int getEstimate() {
        return 0;
    }

    @Override
    public Set<PartialSolution> expand() {
        return null;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public Schedule makeComplete() {
        return null;
    }
}
