package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

public interface JSONLDObject extends NCBOOutputModel {
    String getId();
    List<JSONLDLink> getLinks();
    String getStringValue(final String fieldName);
    @SuppressWarnings("all")
    boolean getBooleanValue(final String fieldName);
    JSONLDObject getObject(final String fieldName);
    Collection<JsonValue> getCollection(final String fieldName);
    URI expandFieldFromContext(final String fieldName) throws URISyntaxException;

    static JSONLDObject create(final JsonObject jsonObject){
        return new JSONLDObjectImpl(jsonObject);
    }
}
