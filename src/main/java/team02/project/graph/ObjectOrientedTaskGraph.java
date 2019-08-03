package team02.project.graph;

import lombok.var;
import team02.project.io.GraphBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ObjectOrientedTaskGraph implements TaskGraph {

    private String id;
    private Map<Integer,Task> taskMap;
    private Set<Integer> startingTaskIds;

    public static final class Builder implements GraphBuilder {
        private ObjectOrientedTaskGraph taskGraph =  new ObjectOrientedTaskGraph();
        private Map<String, Task> tasks = new HashMap<>();

        @Override
        public void setId(String id) {
           taskGraph.id = id;
        }

        @Override
        public void addNode(String name, int weight) {
            var task = new Task(name, weight);
            tasks.put(name, task);
        }

        @Override
        public void addEdge(String toNode, String fromNode, int weight) {
            var from = tasks.get(fromNode);
            var to = tasks.get(toNode);
            var dependency = new Dependency(from, to, weight);
            from.getChildren().add(dependency);
            to.getParents().add(dependency);
        }

        public TaskGraph build() {
            var startingTaskIds = new HashSet<Integer>();
            var taskMap = new HashMap<Integer, Task>();
            for (var entry : tasks.entrySet()) {
                var task = entry.getValue();
                taskMap.put(task.getId(), task);
                if (task.getParents().isEmpty()) {
                    startingTaskIds.add(task.getId());
                }
            }
            taskGraph.taskMap = taskMap;
            taskGraph.startingTaskIds = startingTaskIds;
            return taskGraph;
        }
    }

    private ObjectOrientedTaskGraph() {}

    @Override
    public Set<Integer> getStartingTaskIds() {
        return Collections.unmodifiableSet(startingTaskIds);
    }


    @Override
    public Task findTaskById(int id) {
        return taskMap.get(id);
    }


}
