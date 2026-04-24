package id.co.msiglife.pollux.schduler.exception;

public class FileException extends RuntimeException {
    private final Integer processId;

    public FileException(String message, Integer processId) {
        super(message);
        this.processId = processId;
    }

    public Integer getProcessId() {
        return processId;
    }
}