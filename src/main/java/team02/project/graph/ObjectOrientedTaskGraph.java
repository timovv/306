package team02.project.graph;

import lombok.var;
import team02.project.io.GraphBuilder;

import java.util.HashMap;
import java.util.Map;

public class ObjectOrientedTaskGraph implements TaskGraph {

    private String id;
    private Map<String,Task> tasks = new HashMap<>();

    public static final class Builder implements GraphBuilder {
        private ObjectOrientedTaskGraph taskGraph =  new ObjectOrientedTaskGraph();

        @Override
        public void setId(String id) {
           taskGraph.id = id;
        }

        @Override
        public void addNode(String name, int weight) {
            taskGraph.tasks.put(name, new Task(name, weight));
        }

        @Override
        public void addEdge(String toNode, String fromNode, int weight) {
            var from = taskGraph.tasks.get(fromNode);
            var to = taskGraph.tasks.get(toNode);
            var dependency = new Dependency(from, to, weight);
            from.getDependents().add(dependency);
        }

        public TaskGraph build() {
            return taskGraph;
        }
    }

    private ObjectOrientedTaskGraph() {}
}
