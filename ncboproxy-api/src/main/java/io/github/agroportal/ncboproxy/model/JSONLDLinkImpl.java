package io.github.agroportal.ncboproxy.model;

class JSONLDLinkImpl implements JSONLDLink {

    private final String linkName;
    private String linkValue = null;
    private final String linkContext;

    @SuppressWarnings("all")
    JSONLDLinkImpl(final String linkName, final String linkValue, final String linkContext) {
        this.linkName = linkName;
        this.linkValue = linkValue;
        this.linkContext = linkContext;
    }

    @Override
    public String getLinkName() {
        return linkName;
    }

    @Override
    public String getLinkValue() {
        return linkValue;
    }

    @Override
    public String getLinkContext() {
        return linkContext;
    }
}
