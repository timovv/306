package team02.project.algorithm.solnspace.els;

import com.google.common.collect.Iterators;
import lombok.val;
import team02.project.algorithm.Schedule;
import team02.project.algorithm.ScheduledTask;
import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.graph.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A partial solution in the ELS solution space. Each stage in the ELS solution space represents one additional
 * task being added to a partial schedule: it is allocated a processor and its start time is calculated immediately
 */
public class ELSPartialSolution implements PartialSolution, Iterable<ScheduledTask> {
    private final SchedulingContext context;
    private final ELSPartialSolution parent;
    private final ScheduledTask scheduledTask;
    private final int depth;

    private ELSPartialSolution(SchedulingContext context, ELSPartialSolution parent, ScheduledTask scheduledTask, int depth) {
        this.context = context;
        this.parent = parent;
        this.scheduledTask = scheduledTask;
        this.depth = depth;
    }

    /**
     * Construct an empty ELSPartialSolution for the given context.
     * @param context The context for which to construct the empty ELSPartialSolution
     * @return The created ELSPartialSolution
     */
    public static ELSPartialSolution makeEmpty(SchedulingContext context){
        return new ELSPartialSolution(context, null, null, 0);
    }

    @Override
    public int getEstimatedFinishTime() {
        if(isEmpty()) {
            return 0;
        }

        int finishTime = 0;
        for(val scheduledTask : this) {
            finishTime = Math.max(finishTime, scheduledTask.getFinishTime());
        }

        return finishTime;
    }

    @Override
    public Set<PartialSolution> expand() {
        Set<PartialSolution> output = new HashSet<>();

        Set<Node> nodesAlreadyInSchedule = new HashSet<>();
        for(val node : this) {
            nodesAlreadyInSchedule.add(node.getTask());
        }

        outer:
        for(val node : context.getTaskGraph().getNodes()) {
            // not a candidate if it's already in the schedule
            if(nodesAlreadyInSchedule.contains(node)) {
                continue;
            }

            // not a candidate if it has incoming edges which are not satisfied
            for(val edge : node.getIncomingEdgeNodes()) {
                if(!nodesAlreadyInSchedule.contains(edge)) {
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
                    if(schedule.getProcessorId() != i) {
                        // incoming edge contains getTask
                        boolean found = false;
                        int transferTime = 0;
                        int j = 0;
                        for(Node edge : node.getIncomingEdgeNodes()) {
                            if(edge.equals(schedule.getTask())) {
                                found = true;
                                transferTime = node.getIncomingEdgeWeights()[j];
                                break;
                            }
                            ++j;
                        }

                        if(!found) {
                            break;
                        }

                        val finishTime = schedule.getStartTime() + schedule.getTask().getWeight();
                        startTime = Math.max(startTime, finishTime + transferTime);
                    }
                }

                output.add(new ELSPartialSolution(context, this,
                        new ScheduledTask(i, startTime, node), this.depth + 1));
            }

        }

        return output;
    }

    /**
     * @return true if this ELSPartialSolution is the root, i.e. no tasks have yet been scheduled.
     */
    public boolean isEmpty() {
        return depth == 0;
    }

    private Iterator<Node> nodesIterator() {
        return Iterators.transform(iterator(), ScheduledTask::getTask);
    }

    /**
     * An Iterator that iterates through all tasks that have been scheduled so far.
     * {@inheritDoc}
     * @return iterator of scheduled tasks
     */
    @Override
    public Iterator<ScheduledTask> iterator() {
        return new Iterator<ScheduledTask>() {
            private ELSPartialSolution current = ELSPartialSolution.this;

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

    public ScheduledTask getScheduledTask() {
        return scheduledTask;
    }

    /**
     * Find the parent of this ELSPartialSolution, i.e. the PartialSolution where the current task has not
     * been scheduled.
     * @return The parent of this ELSPartialSolution, or null if the current ELSPartialSolution is the root.
     */
    public ELSPartialSolution getParent() {
        return parent;
    }

    @Override
    public boolean isComplete(){
        return depth == context.getTaskGraph().getNodes().length;
    }

    @Override
    public Schedule makeComplete(){
        Set<ScheduledTask> tasks = new HashSet<>();
        for(val s : this){
            tasks.add(s);
        }
        return new Schedule(tasks);
    }
}
