package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class NCBOErrorModel implements NCBOOutputModel{
    public static final String ERROR_FORMAT_STRING = "error";
    public static final String ERRORS_FORMAT_STRING = "errors";
    public static final String ERROR_STATUS = "status";
    private final JsonValue modelRoot;

    NCBOErrorModel(final JsonObject errorObject) {
        modelRoot = errorObject;
    }

    NCBOErrorModel(final String errorMessage, final int status) {
        final JsonObject errorObject = new JsonObject();
        final JsonArray errorDescriptionArray = new JsonArray();
        errorDescriptionArray.add(errorMessage);
        errorObject.set(ERRORS_FORMAT_STRING,errorDescriptionArray);
        errorObject.set(ERROR_STATUS, status);
        modelRoot = errorObject;
    }

    @Override
    public JsonValue getModelRoot() {
        return modelRoot;
    }

    @Override
    public boolean isError() {
        return true;
    }
}
