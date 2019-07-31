package team02.project.io;

import lombok.var;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class GraphReader {
    public void readInto(Path path, GraphBuilder builder) {
        try {
            readInto(CharStreams.fromChannel(Files.newByteChannel(path, StandardOpenOption.READ)), builder);
        } catch(IOException e) {
            throw new GraphParseException(e);
        }
    }

    public void readInto(InputStream inputStream, GraphBuilder builder) {
        try {
            readInto(CharStreams.fromStream(inputStream), builder);
        } catch(IOException e) {
            throw new GraphParseException(e);
        }
    }

    private void readInto(CharStream stream, GraphBuilder builder) {
        var lexer = new DOTLexer(stream);
        var tokens = new CommonTokenStream(lexer);
        var parser = new DOTParser(tokens);
        var listener = new DOTListenerAdapter();
        listener.setGraphBuilder(builder);

        ParseTreeWalker.DEFAULT.walk(listener, parser.graph());
    }
}
