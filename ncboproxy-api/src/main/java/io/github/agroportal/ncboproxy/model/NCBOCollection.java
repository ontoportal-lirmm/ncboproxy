package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonArray;

import java.util.Collection;

public interface NCBOCollection extends NCBOOutputModel, Collection<NCBOOutputModel> {
    static NCBOCollection create(final JsonArray array){
        return new NCBOCollectionImpl(array);
    }
}
