package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;

import java.util.Optional;

@FunctionalInterface
public interface NCBOOutputModel {
    JsonValue getModelRoot();

    default String toPrettyString(){
        return getModelRoot().toString(WriterConfig.PRETTY_PRINT);
    }

    default boolean isPaginatedCollection() {
        return false;
    }

    default boolean isCollection() {
        return false;
    }

    default boolean isObject() {
        return false;
    }

    default boolean isError() {
        return false;
    }

    default boolean isValue(){return false;}

    default Optional<NCBOPaginatedCollection> asPaginatedCollection() {
        return Optional.empty();
    }

    default Optional<NCBOCollection> asCollection() {
        return Optional.empty();
    }

    default Optional<JSONLDObject> asObject() {
        return Optional.empty();
    }

    default Optional<NCBOErrorModel> asError() {
        return Optional.empty();
    }

    default Optional<JSONValue> asValue(){return Optional.empty();}

    static NCBOOutputModel error(final JsonObject errorObject) {
        return new NCBOErrorModel(errorObject);
    }

    static NCBOOutputModel error(final String errorMessage, final int status) {
        return new NCBOErrorModel(errorMessage,status);
    }

    static NCBOOutputModel annotatorError(final JsonArray errorArray) {
        return new NCBOAnnotatorErrorModel(errorArray);
    }

    static NCBOOutputModel annotatorError(final String errorMessage, final int status) {
        return new NCBOAnnotatorErrorModel(errorMessage,status);
    }
}
