package io.github.agroportal.ncboproxy.output;

/**
 * This interface specifies the output of the annotator
 */
public interface ProxyOutput {
    int HTTP_INTERNAL_APPLICATION_ERROR = 500;

    /**
     * The content of the output
     * @return the content of the output
     */
    String getContent();

    /**
     * Get the mime type of the content
     * @return the mime type of the content
     */
    String getMimeType();
}
