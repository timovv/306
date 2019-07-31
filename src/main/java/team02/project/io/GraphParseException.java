package team02.project.io;

import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class GraphParseException extends RuntimeException {
    private static final long serialVersionUID = 20190731L;

    public GraphParseException(String message) {
        super(message);
    }

    public GraphParseException(Throwable e) {
        super(e);
    }
}
