package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

@FunctionalInterface
public interface NCBOOutputModel {
    JsonValue getModelRoot();

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
