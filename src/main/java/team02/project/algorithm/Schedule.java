package team02.project.algorithm;

import com.google.common.collect.Iterators;
import lombok.*;
import team02.project.graph.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents a partial (or complete) schedule in the solution space.
 * A {@link Schedule} is a delta encoding containing a new {@link ScheduledTask task assignment}.
 */
@Value
public class Schedule implements Iterable<ScheduledTask>, Comparable<Schedule> {

    private static final Schedule EMPTY = new Schedule();

    Schedule parent;
    ScheduledTask scheduledTask;
    int depth;
    int cost; // for A* or any other algorithm with heuristic

    private Schedule() {
        parent = null;
        scheduledTask = null;
        depth = 0;
        cost = 0;
    }

    // Constructor without heuristic
    public Schedule(@NonNull Schedule parent, @NonNull ScheduledTask scheduledTask) {
        this.parent = parent;
        this.scheduledTask = scheduledTask;
        this.depth = parent.getDepth() + 1;
        this.cost = 0;
    }

    // Constructor with heuristic
    public Schedule(@NonNull Schedule parent, @NonNull ScheduledTask scheduledTask, int heuristic) {
        this.parent = parent;
        this.scheduledTask = scheduledTask;
        this.depth = parent.getDepth() + 1;
        this.cost = heuristic;
    }

    public static Schedule empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return scheduledTask == null;
    }

    public boolean isCompleteFor(SchedulingContext ctx) {
        return getDepth() == ctx.getTaskGraph().getNodes().size();
    }

    public int getFinishTime() {
        if(isEmpty()) {
            return 0;
        }

        int finishTime = 0;
        for(val scheduledTask : this) {
            finishTime = Math.max(finishTime, scheduledTask.getFinishTime());
        }

        return finishTime;
    }

    /**
     * Computes all permutations of unassigned {@link Node tasks} to the earliest
     * possible starting time for each processor (accounting for communication delays)
     * @param context The {@link SchedulingContext}
     * @return A {@link Set<Schedule> set} containing all computed permutations of schedules
     */
    public Set<Schedule> expand(SchedulingContext context) {
        Set<Schedule> output = new HashSet<>();

        outer:
        for(val node : context.getTaskGraph().getNodes()) {
            // not a candidate if it's already in the schedule
            if(Iterators.contains(nodesIterator(), node)) {
                continue;
            }

            // not a candidate if it has incoming edges which are not satisfied
            for(val edge : node.getIncomingEdges().entrySet()) {
                if(!Iterators.contains(nodesIterator(), edge.getKey())) {
                    continue outer;
                }
            }

            // we conclude this node is a candidate for expansion. try and place it on each processor.
            for(int i = 0; i < context.getProcessorCount(); ++i) {
                // start time = max(finish time of dependencies not on this processor + transfer time, finish time of last task on this processor)
                int startTime = 0;

                // find last task on this processor if any
                for(val schedule : this) {
                    if (schedule.getProcessorId() == i) {
                        startTime = schedule.getStartTime() + schedule.getTask().getWeight();
                        break;
                    }
                }

                // constrain based on transfer times
                for(val schedule : this) {
                    if(schedule.getProcessorId() != i && node.getIncomingEdges().containsKey(schedule.getTask())) {
                        val finishTime = schedule.getStartTime() + schedule.getTask().getWeight();
                        val transferTime = node.getIncomingEdges().get(schedule.getTask());
                        startTime = Math.max(startTime, finishTime + transferTime);
                    }
                }

                output.add(new Schedule(this, new ScheduledTask(i, startTime, node),
                        Math.max(this.cost, startTime + node.getBottomLevel())));
            }

        }

        return output;
    }

    /**
     * Finds the partial schedule that is a subschedule of this schedule that has the given node as head.
     * @param node
     * @return the corresponding partial schedule, or null if none found
     */
    private Schedule findPartialScheduleFor(Node node) {
        var sched = this;
        while(!sched.isEmpty()) {
            if(sched.getScheduledTask().getTask().equals(node)) {
                return sched;
            }
            sched = sched.getParent();
        }

        return null;
    }

    private Iterator<Node> nodesIterator() {
        return Iterators.transform(iterator(), ScheduledTask::getTask);
    }

    @Override
    public Iterator<ScheduledTask> iterator() {
        return new Iterator<ScheduledTask>() {
            private Schedule current = Schedule.this;

            @Override
            public boolean hasNext() {
                return !current.isEmpty();
            }

            @Override
            public ScheduledTask next() {
                val ret = current.getScheduledTask();
                current = current.getParent();
                return ret;
            }
        };
    }

    @Override
    public int compareTo(Schedule o){
        return Integer.compare(this.cost, o.cost);
    }
}
