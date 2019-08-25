package team02.project.algorithm;

import team02.project.graph.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Simple algorithm that calculates a valid (but not optimal) schedule
 * by creating a topological ordering of the tasks in the input graph,
 * and then scheduling all tasks on N processor in that order.
 */
public class TopologicalSortAlgorithm implements SchedulingAlgorithm {


    /**
     * @param ctx The {@link SchedulingContext} of the algorithm
     * @return A valid schedule based on topological ordering
     */
    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {
        Map<Node, ScheduledTask> scheduledTaskMap = new HashMap<>();
        int[] earliestProcessorFree = new int[ctx.getProcessorCount()];

        // It is assumed that the task graph is already sorted into
        // topological order by the GraphBuilderImpl
        for (Node node : ctx.getTaskGraph().getNodes()) {
            int bestProc = 0;
            int bestStartTime = Integer.MAX_VALUE;
            for (int procNum = 0; procNum < ctx.getProcessorCount(); procNum++) {
                int dataReadyTime = 0;

                for(int i = 0; i < node.getIncomingEdgeNodes().length; ++i) {
                    Node edgeNode = node.getIncomingEdgeNodes()[i];
                    int edgeWeight = node.getIncomingEdgeWeights()[i];

                    ScheduledTask parentTask = scheduledTaskMap.get(edgeNode);
                    if (parentTask.getProcessorId() != procNum) {
                        dataReadyTime = Math.max(dataReadyTime, parentTask.getFinishTime() + edgeWeight);
                    } else {
                        dataReadyTime = Math.max(dataReadyTime, parentTask.getFinishTime());
                    }
                }

                int startTime = Math.max(earliestProcessorFree[procNum], dataReadyTime);
                if (startTime < bestStartTime) {
                    bestStartTime = startTime;
                    bestProc = procNum;
                }
            }
            scheduledTaskMap.put(node, new ScheduledTask(bestProc, bestStartTime, node));
            earliestProcessorFree[bestProc] += bestStartTime + node.getWeight();
        }

        return new Schedule(new HashSet<>(scheduledTaskMap.values()));
    }
}
