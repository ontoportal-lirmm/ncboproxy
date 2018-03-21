package io.github.agroportal.ncboproxy.output;


/**
 * Default implementation of ProxyOutput
 */
public class NCBOProxyOutput implements ProxyOutput {
    private final String stringContent;
    private final String mimeType;
    private final byte[] binaryContent;
    private final boolean binary;

    NCBOProxyOutput(final String stringContent, final String mimeType) {
        this.stringContent = stringContent;
        binaryContent = new byte[1];
        this.mimeType = mimeType;
        binary = false;
    }

    NCBOProxyOutput(final byte[] binaryContent, final String mimeType) {
        this.binaryContent = binaryContent.clone();
        stringContent = "";
        this.mimeType = mimeType;
        binary = true;
    }

    @Override
    public boolean isBinary() {
        return binary;
    }


    @Override
    public byte[] getBinaryContent() {
        return binaryContent.clone();
    }

    @Override
    public String getStringContent() {
        return stringContent;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }
}
