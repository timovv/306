package team02.project.schedule;

import lombok.Value;
import team02.project.util.Parameters;

/** NOT USED **/
@Value
public class Allocation {

    // This number should probably be determined using a theoretical upper bound calc
    private static final int CACHED_START_TIME_RANGE = 40;
    private static Allocation[][] cache;

    int processor;
    int startTime;

    public static Allocation of(int processor, int startTime) {
        if (cache == null) {
            populateCache();
        }
        if (processor >= Parameters.getNumProcessors() || startTime >= CACHED_START_TIME_RANGE) {
            return new Allocation(processor, startTime);
        }
        return cache[processor][startTime];
    }

    private static void populateCache() {
        cache = new Allocation[Parameters.getNumProcessors()][CACHED_START_TIME_RANGE];
        for (int proc = 0; proc < Parameters.getNumProcessors(); proc++) {
            for (int start = 0; start < CACHED_START_TIME_RANGE; start++) {
                cache[proc][start] = new Allocation(proc, start);
            }
        }
    }
}
