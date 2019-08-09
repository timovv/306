package team02.project.algorithm;

import com.google.common.collect.Iterators;
import lombok.*;
import team02.project.graph.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Value
public class Schedule implements Iterable<ScheduledTask>, Comparable<Schedule> {

    private static final Schedule EMPTY = new Schedule();

    Schedule parent;
    ScheduledTask scheduledTask;
    int depth;
    int cost;

    private Schedule() {
        parent = null;
        scheduledTask = null;
        depth = 0;
        cost = 0;
    }

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

    public Set<Schedule> expand(SchedulingContext context) {
        Set<Schedule> output = new HashSet<>();

        Set<Node> nodesInSchedule = new HashSet<>();
        for(val node : this) {
            nodesInSchedule.add(node.getTask());
        }

        // this is spaghetto
        outer:
        for(val node : context.getTaskGraph().getNodes()) {
            int c = 0;
            // not a candidate if it's already in the schedule
            if(nodesInSchedule.contains(node)) {
                continue;
            }

            // not a candidate if it has incoming edges which are not satisfied
            for(val edge : node.getIncomingEdges().entrySet()) {
                if(!nodesInSchedule.contains(edge.getKey())) {
                    c += node.getWeight();
                    continue outer;
                }
            }

            // we conclude this node is a candidate for expansion. try and place it on each processor.
            for(int i = 0; i < context.getProcessorCount(); ++i) {
                // start time = max(finish time of dependencies not on this processor + transfer time, finish time of last task on this processor)
                int startTime = 0;
                c += node.getWeight();

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

                // calculate heuristic
                output.add(new Schedule(this, new ScheduledTask(i, startTime, node), Math.max(this.cost, startTime + node.getBottomLevel())));
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
    public int compareTo(Schedule o) {
        return Integer.compare(this.cost, o.cost);
    }
}
