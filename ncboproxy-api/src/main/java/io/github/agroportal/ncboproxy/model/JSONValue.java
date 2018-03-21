package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonValue;

import java.util.Optional;

public interface JSONValue extends NCBOOutputModel {

    @Override
    default boolean isValue() {
        return true;
    }

    boolean isString();

    boolean isInteger();


    boolean isBoolean();

    Optional<String> asString();
    Optional<Integer> asInteger();
    Optional<Boolean> asBoolean();

    static JSONValue create(final JsonValue modelRoot){
        return new JSONValueImpl(modelRoot);
    }
}
