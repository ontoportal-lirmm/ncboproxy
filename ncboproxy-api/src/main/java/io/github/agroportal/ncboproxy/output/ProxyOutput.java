package io.github.agroportal.ncboproxy.output;

import javax.servlet.http.HttpServletResponse;

/**
 * This interface specifies the output of the annotator
 */
public interface ProxyOutput {
    int HTTP_INTERNAL_APPLICATION_ERROR = 500;

    void addCustomHeader(String name, String value);
    ProxyOutput transferCustomHeadersToResponse(final HttpServletResponse servletResponse);

    ProxyOutput makeFileTransfer(final String filename);

    /**
     * The content of the output
     * @return the content of the output
     */
    String getStringContent();

    byte[] getBinaryContent();

    boolean isBinary();

    /**
     * Get the mime type of the content
     * @return the mime type of the content
     */
    String getMimeType();

    static ProxyOutput create(final String content, final String mimeType){
        return new NCBOProxyOutput(content, mimeType);
    }

    static ProxyOutput create(final byte[] content, final String mimeType){
        return new NCBOProxyOutput(content, mimeType);
    }
}
