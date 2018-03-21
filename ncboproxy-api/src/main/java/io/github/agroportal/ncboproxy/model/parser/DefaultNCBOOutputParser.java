package io.github.agroportal.ncboproxy.model.parser;

import com.eclipsesource.json.*;
import io.github.agroportal.ncboproxy.model.*;
import io.github.agroportal.ncboproxy.model.retrieval.RequestResult;
import io.github.agroportal.ncboproxy.output.ProxyOutput;

public class DefaultNCBOOutputParser implements NCBOOutputParser {


    DefaultNCBOOutputParser() {
    }

    @Override
    public NCBOOutputModel parse(final RequestResult queryResponse, final APIContext apiContext) throws com.eclipsesource.json.ParseException, UnsupportedOperationException {
        final NCBOOutputModel outputModel;

        final JsonValue rootNode = Json.parse(queryResponse.getMessage());
        if (rootNode.isObject()) {
            final JsonObject rootObject = rootNode.asObject();
            //Json object with an "errors" array containing one or more error messages, this is the default error
            //output for the Bioportal REST API
            if (rootObject.get(NCBOErrorModel.ERRORS_FORMAT_STRING) != null) {
                outputModel = NCBOOutputModel.error(rootObject);
            } else {
                //If we have a page attribute, we have a paginated collection and instanciate the appropriate output model,
                //Otherwise it's a standard JSONLD object
                outputModel = (rootObject.getInt("page", -1) > -1) ?
                        NCBOPaginatedCollection.create(rootObject, apiContext)
                        : JSONLDObject.create(rootObject);
            }
        } else if (rootNode.isArray()) {
            //If we get in array at the root, it's either a standard array of JSONLD Objects (typically the annotator output)
            //or an error (in the format of error messages for the annotator
            final JsonArray rootArray = rootNode.asArray();
            if ((!rootArray.isEmpty()) && rootArray
                    .get(0)
                    .isObject()) {
                final JsonObject first = rootArray
                        .get(0)
                        .asObject();
                outputModel = (first.get(NCBOErrorModel.ERROR_FORMAT_STRING) != null)
                        ? NCBOOutputModel.annotatorError(rootArray)
                        : NCBOCollection.create(rootArray);
            } else { //BioPortal never returns an array of flat values, so this must be an error
                outputModel = incorrectFormatErrorOutput(rootNode);
            }
        } else {
            //Bioportal never returns an a single JsonValue outside of an object or an array, so it must be an error
            outputModel = incorrectFormatErrorOutput(rootNode);
        }

        return outputModel;
    }

    private NCBOOutputModel incorrectFormatErrorOutput(final JsonValue rootValue) {
        return NCBOOutputModel.error(
                String.format("Unrecognized JSONLD construct received from NCBO API: \"%s\"", rootValue.toString()),
                ProxyOutput.HTTP_INTERNAL_APPLICATION_ERROR);
    }

}
