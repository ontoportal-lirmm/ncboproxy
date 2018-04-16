package io.github.agroportal.ncboproxy.parameters;


@SuppressWarnings("SerializableHasSerializationMethods")
public class InvalidParameterException extends RuntimeException {

    private static final long serialVersionUID = 6449276614063517190L;

    InvalidParameterException(final String message) {
        super(message);
    }
}
