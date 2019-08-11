package team02.project.io;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class GraphParseException extends RuntimeException {
    private static final long serialVersionUID = 20190731L;


    public static final int NO_LINE_NUMBER = -1;

    @Getter
    private int lineNumber = NO_LINE_NUMBER;

    public GraphParseException(String message, int lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public GraphParseException(String message) {
        super(message);
    }

    public GraphParseException(Throwable e) {
        super(e);
    }
}
