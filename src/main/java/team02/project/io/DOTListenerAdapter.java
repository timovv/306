package team02.project.io;

import lombok.Getter;
import lombok.Setter;
import lombok.var;
import team02.project.graph.GraphBuilder;

class DOTListenerAdapter extends DOTBaseListener {

    private static final String WEIGHT = "Weight";
    private static final int ATTR_KEY = 0;
    private static final int ATTR_VALUE = 1;

    @Getter
    @Setter
    private GraphBuilder graphBuilder;

    @Override
    public void exitGraph(DOTParser.GraphContext ctx) {
        if(ctx.id() != null) {
            graphBuilder.setId(ctx.id().getText());
        }
    }

    @Override
    public void exitNode_stmt(DOTParser.Node_stmtContext ctx) {
        if (ctx.attr_list() == null || ctx.attr_list().a_list() == null) {
            throw new RuntimeException("Weight must be specified for a node");
        }

        var nodeId = ctx.node_id().id().getText();
        graphBuilder.addNode(Integer.parseInt(nodeId), getWeight(ctx.attr_list()));
}

    @Override
    public void exitEdge_stmt(DOTParser.Edge_stmtContext ctx) {
        if (ctx.node_id() == null) {
            throw new RuntimeException("Subgraph edge ends are not supported");
        }
        if (ctx.edgeRHS().node_id().size() != 1) {
            throw new RuntimeException("Only edges with a single start and end vertex are supported");
        }
        if (ctx.edgeRHS().subgraph().size() != 0) {
            throw new RuntimeException("Subgraph edge ends are not supported");
        }

        var lhsId = ctx.node_id().id().getText();
        var rhsId = ctx.edgeRHS().node_id(0).id().getText();
        graphBuilder.addEdge(Integer.parseInt(lhsId), Integer.parseInt(rhsId), getWeight(ctx.attr_list()));
    }

    private int getWeight(DOTParser.Attr_listContext context) {
        return context.a_list()
                .stream()
                .filter(x -> x.id(ATTR_KEY).getText().equals(WEIGHT))
                .findFirst()
                .map(x -> x.id(ATTR_VALUE).getText())
                .map(Integer::parseInt)
                .orElseThrow((() -> new RuntimeException("Weight must be specified")));
    }
}

