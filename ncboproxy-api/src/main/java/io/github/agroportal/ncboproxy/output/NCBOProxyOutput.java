package io.github.agroportal.ncboproxy.output;


import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Default implementation of ProxyOutput
 */
public class NCBOProxyOutput implements ProxyOutput {
    public static final String PUBLIC_VIEWING_RESTRICTIONS_VALUE = "public";
    private final String stringContent;
    private final String mimeType;
    private final byte[] binaryContent;
    private final boolean binary;
    private final Map<String,List<String>> customHeaders;

    NCBOProxyOutput(final String stringContent, final String mimeType) {
        this.stringContent = stringContent;
        binaryContent = new byte[1];
        this.mimeType = mimeType;
        binary = false;
        customHeaders = new HashMap<>();
    }

    NCBOProxyOutput(final byte[] binaryContent, final String mimeType) {
        this.binaryContent = binaryContent.clone();
        stringContent = "";
        this.mimeType = mimeType;
        binary = true;
        customHeaders = new HashMap<>();
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
    public void addCustomHeader(final String name, final String value) {
        customHeaders.putIfAbsent(name, new ArrayList<>());
        customHeaders.get(name).add(value);
    }

    @Override
    public ProxyOutput transferCustomHeadersToResponse(final HttpServletResponse servletResponse) {
        for(final Map.Entry<String, List<String>> entry : customHeaders.entrySet()){
            for(final String value: entry.getValue()){
                servletResponse.addHeader(entry.getKey(),value);
            }
        }
        return this;
    }

    @Override
    public ProxyOutput makeFileTransfer(final String filename) {
        addCustomHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        addCustomHeader("Cache-Control", PUBLIC_VIEWING_RESTRICTIONS_VALUE);
        addCustomHeader("Content-Description", "File Transfer");
        addCustomHeader("Content-Disposition", String.format("attachment; filename=\"%s\"",filename));
        addCustomHeader("Content-Transfer-Encoding", "binary");
        return this;
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
