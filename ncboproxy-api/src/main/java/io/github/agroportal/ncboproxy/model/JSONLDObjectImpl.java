package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class JSONLDObjectImpl implements JSONLDObject {


    private List<JSONLDLink> links;
    private final JsonObject jsonObject;
    private final Map<String, String> contextMap;

    JSONLDObjectImpl(final JsonObject jsonObject) {
        this.jsonObject = jsonObject;

        contextMap = new HashMap<>();
        if (jsonObject
                .names()
                .contains(JSONLDConstants.CONTEXT)) {
            final JsonObject contextObject = jsonObject
                    .get(JSONLDConstants.CONTEXT)
                    .asObject();
            for (final String name : contextObject.names()) {
                if(contextObject.get(name).isString()) {
                    contextMap.put(name, contextObject.getString(name, ""));
                }
            }
        }
    }

    @Override
    public String getId() {
        return getStringValue("@id");
    }

    @Override
    public List<JSONLDLink> getLinks() {
        if (links == null) {
            links = JSONLDLink.create(jsonObject);
        }
        return Collections.unmodifiableList(links);
    }

    @Override
    public String getStringValue(final String fieldName) {
        String result= "";
        if (jsonObject
                .names()
                .contains(fieldName)) {
            final JsonValue value = jsonObject.get(fieldName);
            if(value.isString()){
                result = value.asString();
            }
        }
        return result;
    }

    @Override
    public boolean getBooleanValue(final String fieldName) {
        boolean result = false;
        if (jsonObject
                .names()
                .contains(fieldName)) {
            final JsonValue value = jsonObject.get(fieldName);
            result = value.isBoolean() && value.asBoolean();
        }
        return result;
    }

    @Override
    public JSONLDObject getObject(final String fieldName) {
        JSONLDObject jsonldObject = null;
        if (jsonObject
                .names()
                .contains(fieldName)) {
            final JsonValue value = jsonObject.get(fieldName);
            if (value.isObject()){
                jsonldObject = JSONLDObject.create(value.asObject());
            }
        }
        return jsonldObject;
    }

    @Override
    public Collection<JsonValue> getCollection(final String fieldName) {
        final Collection<JsonValue> collection = new ArrayList<>();
        if (jsonObject
                .names()
                .contains(fieldName)) {
            final JsonValue value = jsonObject.get(fieldName);
            if (value.isArray()) {
                value.asArray().forEach(collection::add);
            }
        }
        return collection;
    }

    @Override
    public URI expandFieldFromContext(final String fieldName) throws URISyntaxException {
        return new URI(contextMap.get(fieldName));
    }

    @Override
    public JsonValue getModelRoot() {
        return jsonObject;
    }

    @Override
    public boolean isPaginatedCollection() {
        return false;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final JSONLDObjectImpl that = (JSONLDObjectImpl) o;
        return (getStringValue("@id").equals(that.getStringValue("@id"))) || Objects.equals(jsonObject, that.jsonObject);
    }

    @Override
    public int hashCode() {

        return Objects.hash(jsonObject);
    }
}
