package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonValue;

import java.util.Optional;

class JSONValueImpl implements JSONValue {
    private final JsonValue modelRoot;

    JSONValueImpl(final JsonValue modelRoot) {
        this.modelRoot = modelRoot;
    }

    @Override
    public boolean isString() {
        return modelRoot.isString();
    }

    @Override
    public boolean isInteger() {
        return modelRoot.isNumber();
    }

    @Override
    public boolean isBoolean() {
        return modelRoot.isBoolean();
    }

    @Override
    public Optional<String> asString() {
        Optional<String> result = Optional.empty();
        if(modelRoot.isString()){
            result = Optional.of(modelRoot.asString());
        }
        return result;
    }

    @Override
    public Optional<Integer> asInteger() {
        Optional<Integer> result = Optional.empty();
        if(modelRoot.isNumber()){
            result = Optional.of(modelRoot.asInt());
        }
        return result;
    }

    @Override
    public Optional<Boolean> asBoolean() {
        Optional<Boolean> result = Optional.empty();
        if(modelRoot.isBoolean()){
            result = Optional.of(modelRoot.asBoolean());
        }
        return result;
    }


    @Override
    public Optional<JSONValue> asValue() {
        return Optional.of(this);
    }

    @Override
    public JsonValue getModelRoot() {
        return modelRoot;
    }
}
