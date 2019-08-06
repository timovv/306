package team02.project.cli;

public class CLIException extends Exception {
    private String message;

    public CLIException(String message) {
        this.message = message;
    }

    public String getExceptionMessage() {
        return this.message;
    }
}
