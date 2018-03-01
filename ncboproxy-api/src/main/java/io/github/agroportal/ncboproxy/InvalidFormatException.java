package io.github.agroportal.ncboproxy;

/**
 * This exception is thrown when the format of the input Bioportal JSON-LD syntax is invalid
 */
public class InvalidFormatException extends RuntimeException {
    private static final long serialVersionUID = -7838902555762036816L;

    public InvalidFormatException(final String message) {
        super(String.format("Invalid format: %s", message));
    }
}
