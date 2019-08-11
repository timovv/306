package team02.project.io;

import lombok.var;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import team02.project.graph.GraphBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class GraphReader {
    private GraphReader() {

    }

    /**
     * Given a {@link Path} to a dotfile and {@link GraphBuilder}, this method will invoke the appropriate
     * methods on the GraphBuilder interface for nodes, edges and weights
     * @param path the {@link Path} to input dotfile
     * @param builder the {@link GraphBuilder} used to construct Graph
     */
    public static void readInto(Path path, GraphBuilder builder) {
        try {
            readInto(CharStreams.fromChannel(Files.newByteChannel(path, StandardOpenOption.READ)), builder);
        } catch(IOException e) {
            throw new GraphParseException(e);
        }
    }

    /**
     * Given a {@link InputStream} to a dotfile and {@link GraphBuilder}, this method will invoke the appropriate
     * methods on the GraphBuilder interface for nodes, edges and weights
     * @param inputStream the {@link InputStream} to input dotfile
     * @param builder the {@link GraphBuilder} used to construct Graph
     */
    public static void readInto(InputStream inputStream, GraphBuilder builder) {
        try {
            readInto(CharStreams.fromStream(inputStream), builder);
        } catch(IOException e) {
            throw new GraphParseException(e);
        }
    }

    /**
     * Given a {@link CharStream} to a dotfile and {@link GraphBuilder}, this method will invoke the appropriate
     * methods on the GraphBuilder interface for nodes, edges and weights
     * @param stream the {@link CharStream} to input dotfile
     * @param builder the {@link GraphBuilder} used to construct Graph
     */
    private static void readInto(CharStream stream, GraphBuilder builder) {
        var lexer = new DOTLexer(stream);
        var tokens = new CommonTokenStream(lexer);
        var parser = new DOTParser(tokens);
        var listener = new DOTListenerAdapter();
        listener.setGraphBuilder(builder);

        ParseTreeWalker.DEFAULT.walk(listener, parser.graph());
    }
}
