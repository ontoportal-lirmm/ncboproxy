package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonObject;

import java.io.IOException;

public interface NCBOPaginatedCollection extends NCBOCollection {
    NCBOPaginatedCollection getPreviousPage() throws IOException;
    NCBOPaginatedCollection getNextPage() throws IOException;
    int getNumberOfPages();
    int getCurrentPageNumber();
    int getTotalSize();

    static NCBOPaginatedCollection create(final JsonObject jsonObject, final APIContext apiContext){
        return new NCBOPaginatedCollectionImpl(jsonObject, apiContext);
    }

}
