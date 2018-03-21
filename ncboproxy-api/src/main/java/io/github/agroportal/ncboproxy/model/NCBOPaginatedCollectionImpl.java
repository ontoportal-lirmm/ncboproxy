package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import io.github.agroportal.ncboproxy.model.retrieval.BioportalRESTRequest;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;

public class NCBOPaginatedCollectionImpl extends NCBOCollectionImpl implements NCBOPaginatedCollection {

    private final int numberOfPages;
    private final int currentPageNumber;
    private final int totalSize;

    private final APIContext apiContext;

    private final JsonObject pageObject;

    NCBOPaginatedCollectionImpl(final JsonObject jsonObject, final APIContext apiContext) {
        super(jsonObject
                .get("collection")
                .asArray());

        pageObject = jsonObject;

        this.apiContext = apiContext;
        currentPageNumber = jsonObject.getInt("page", 0);
        numberOfPages = jsonObject.getInt("pageCount", 0);
        totalSize = jsonObject.getInt("totalCount", 0);
    }

    @Override
    public NCBOPaginatedCollection getPreviousPage() throws IOException {
        return fetchPage("prevPage");
    }

    private String fetchPageJson(final String url) throws IOException {
        return BioportalRESTRequest.query(url, apiContext);
    }

    private NCBOPaginatedCollection fetchPage(final String fieldValue) throws IOException {
        if (!pageObject.get(fieldValue).isNull()) {
            final int nextPageNumber = pageObject
                    .getInt(fieldValue, -1);
            if (nextPageNumber >= 1) {
                final String nextPageURL = pageObject
                        .asObject()
                        .get("links")
                        .asObject()
                        .get(fieldValue)
                        .asString();
                final JsonValue nextPageJsonValue = Json.parse(fetchPageJson(nextPageURL));
                return new NCBOPaginatedCollectionImpl(nextPageJsonValue.asObject(), apiContext);
            }
        }
        throw new IOException(MessageFormat.format("Page change exception: Cannot access {1}", fieldValue));
    }

    @Override
    public NCBOPaginatedCollection getNextPage() throws IOException {
        return fetchPage("nextPage");
    }

    @Override
    public int getNumberOfPages() {
        return numberOfPages;
    }

    @Override
    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    @Override
    public int getTotalSize() {
        return totalSize;
    }

    @Override
    public boolean isPaginatedCollection() {
        return true;
    }

    @Override
    public Optional<NCBOPaginatedCollection> asPaginatedCollection() {
        return Optional.of(this);
    }
}
