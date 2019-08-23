package team02.project.graph;

import java.util.Map;
import java.util.Set;

public interface Node {
    /**
     * Get id of {@link Node}
     * @return The id of current node
     */
    String getId();

    /**
     * Get weight of {@link Node}
     * @return The weight of current node
     */
    int getWeight();


    int getBottomLevel();
    int getTopLevel();

    /**
     * Get a unique integer index of this Node, which is guaranteed to be constrained by 0 <= index < graph.getNodes().
     * @return The index of this node.
     */
    int getIndex();

    /**
     * Get the parent {@link Node nodes} and their associated weight
     * @return
     */
    Map<Node, Integer> getOutgoingEdges();
    Map<Node, Integer> getIncomingEdges();

    Set<Node> getDependencies();

    Set<Node> getDependents();
}
