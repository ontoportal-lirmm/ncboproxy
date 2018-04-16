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
    public Optional<String> getId() {
        return getStringValue(JSONLDConstants.ID);
    }

    @Override
    public List<JSONLDLink> getLinks() {
        if (links == null) {
            links = JSONLDLink.create(jsonObject);
        }
        return Collections.unmodifiableList(links);
    }


    @Override
    public Optional<String> getStringValue(final String fieldName) {
        Optional<String> result= Optional.empty();
        if (jsonObject
                .names()
                .contains(fieldName)) {
            final JsonValue value = jsonObject.get(fieldName);
            if(value.isString()){
                result = Optional.of(value.asString());
            }
        }
        return result;
    }

    @Override
    public Optional<Integer> getIntegerValue(final String fieldName) {
        Optional<Integer> result= Optional.empty();
        if (jsonObject
                .names()
                .contains(fieldName)) {
            final JsonValue value = jsonObject.get(fieldName);
            if(value.isNumber()){
                result = Optional.of(value.asInt());
            }
        }
        return result;
    }

    @Override
    public Optional<String> getStringValue(final String... fieldsName) {
        Optional<String> result= Optional.empty();
        boolean found = false;
        int i = 0;
        while(!found && (i < fieldsName.length)){
            final Optional<String> current = getStringValue(fieldsName[i]);
            if(current.isPresent() && !current.get().isEmpty()){
                result = current;
                found = true;
            }
            i++;
        }
        return result;
    }

    @Override
    public Optional<Integer> getIntegerValue(final String... fieldsName) {
        Optional<Integer> result= Optional.empty();
        boolean found = false;
        int i = 0;
        while(!found && (i < fieldsName.length)){
            final Optional<Integer> current = getIntegerValue(fieldsName[i]);
            if(current.isPresent()){
                result = current;
                found = true;
            }
            i++;
        }
        return result;
    }

    @Override
    public Optional<Boolean> getBooleanValue(final String fieldName) {
        Optional<Boolean> result = Optional.empty();
        if (jsonObject
                .names()
                .contains(fieldName)) {
            final JsonValue value = jsonObject.get(fieldName);
            if(value.isBoolean()) {
                result = Optional.of(value.asBoolean());
            }
        }
        return result;
    }

    @Override
    public Optional<JSONLDObject> getObject(final String fieldName) {
        Optional<JSONLDObject> jsonldObject = Optional.empty();
        if (jsonObject
                .names()
                .contains(fieldName)) {
            final JsonValue value = jsonObject.get(fieldName);
            if (value.isObject()){
                jsonldObject = Optional.of(JSONLDObject.create(value.asObject()));
            }
        }
        return jsonldObject;
    }

    @Override
    public Optional<NCBOCollection> getCollection(final String fieldName) {
        Optional<NCBOCollection> collection = Optional.empty();
        if (jsonObject
                .names()
                .contains(fieldName)) {
            final JsonValue value = jsonObject.get(fieldName);
            if (value.isArray()) {
                collection = Optional.of(NCBOCollection.create(value.asArray()));
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
    public boolean isObject() {
        return true;
    }

    @Override
    public Optional<JSONLDObject> asObject() {
        return Optional.of(this);
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final JSONLDObjectImpl that = (JSONLDObjectImpl) o;
        return (getStringValue(JSONLDConstants.ID).equals(that.getStringValue(JSONLDConstants.ID))) || Objects.equals(jsonObject, that.jsonObject);
    }

    @Override
    public int hashCode() {

        return Objects.hash(jsonObject);
    }
}
