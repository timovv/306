package team02.project.algorithm;

import team02.project.graph.Node;

import java.util.LinkedHashSet;

/**
 * Simple algorithm that calculates a valid (but not optimal) schedule
 * by creating a topological ordering of the tasks in the input graph,
 * and then scheduling all tasks on one processor in that order.
 */
public class TopologicalSortAlgorithm implements SchedulingAlgorithm {

    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {
        LinkedHashSet<Node> order = new LinkedHashSet<>();

        for(Node node : ctx.getTaskGraph().getNodes()) {
            visit(order, node);
        }

        Schedule output = Schedule.empty();
        int startTime = 0;
        for(Node node : order) {
            output = new Schedule(output, new ScheduledTask(0, startTime, node));
            startTime += node.getWeight();
        }

        return output;
    }

    private void visit(LinkedHashSet<Node> order, Node toVisit) {
        if(order.contains(toVisit)) {
            return;
        }

        for(Node node : toVisit.getIncomingEdges().keySet()) {
            visit(order, node);
        }

        order.add(toVisit);
    }
}
