package io.github.agroportal.ncboproxy;

/**
 * This is a generic exception encapsulating error messages from an NCBO Bioportal api service
 */
public class NCBOProxyErrorException extends RuntimeException {
    private static final long serialVersionUID = 1297825996601621464L;

    public NCBOProxyErrorException(final String message) {
        super(message);
    }
}
