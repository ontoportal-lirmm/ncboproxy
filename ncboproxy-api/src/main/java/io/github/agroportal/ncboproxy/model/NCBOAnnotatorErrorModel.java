package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class NCBOAnnotatorErrorModel implements NCBOOutputModel{
    private final JsonValue modelRoot;

    NCBOAnnotatorErrorModel(final JsonArray jsonArray) {
        modelRoot = jsonArray;
    }

    NCBOAnnotatorErrorModel(final String errorMessage, final int status) {
        final JsonObject errorObject = new JsonObject();
        final JsonObject errorDescriptionObject = new JsonObject();
        errorDescriptionObject.set(NCBOErrorModel.ERROR_FORMAT_STRING, errorMessage);
        errorDescriptionObject.set(NCBOErrorModel.ERROR_STATUS, status);
        errorObject.set(NCBOErrorModel.ERROR_FORMAT_STRING,errorDescriptionObject);
        modelRoot = new JsonArray().add(errorObject);
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
