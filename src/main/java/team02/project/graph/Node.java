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
     * @return the weights of the incoming edges corresponding to teh edge at node i of {{@link #getIncomingEdgeNodes()}}.
     */
    int[] getIncomingEdgeWeights();

    /**
     * nodes for which an edge exists from that node to this node.
     * @return
     */
    Node[] getIncomingEdgeNodes();

    /**
     * @return the weights of the outgoing edges corresponding to the edge at node i of {@link #getOutgoingEdgeNodes()}
     */
    int[] getOutgoingEdgeWeights();

    /**
     * nodes for which an edge exists from that node to this node.
     * @return
     */
    Node[] getOutgoingEdgeNodes();

    Set<Node> getDependencies();

    Set<Node> getDependents();
}
