package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public interface JSONLDObject extends NCBOOutputModel {
    Optional<String> getId();
    List<JSONLDLink> getLinks();

    Optional<Integer> getIntegerValue(String... fieldsName);

    @SuppressWarnings("all")
    Optional<Boolean> getBooleanValue(final String fieldName);
    Optional<JSONLDObject> getObject(final String fieldName);
    Optional<NCBOCollection> getCollection(final String fieldName);
    URI expandFieldFromContext(final String fieldName) throws URISyntaxException;

    Optional<String> getStringValue(String fieldName);

    Optional<Integer> getIntegerValue(String fieldName);

    /**
     * Get first field value that exists and that is not null out of the list supplied in {@code fieldsName}
     * @param fieldsName The list of fields in descending order of priority
     * @return The optional value of the first non null property
     */
    Optional<String> getStringValue(final String... fieldsName);

    static JSONLDObject create(final JsonObject jsonObject){
        return new JSONLDObjectImpl(jsonObject);
    }
}
