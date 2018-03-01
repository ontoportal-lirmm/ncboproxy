package io.github.agroportal.ncboproxy.output;


/**
 * Default implementation of ProxyOutput
 */
public class NCBOProxyOutput implements ProxyOutput {
    private final String content;
    private final String mimeType;

    public NCBOProxyOutput(final String content, final String mimeType) {
        this.content = content;
        this.mimeType = mimeType;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }
}
