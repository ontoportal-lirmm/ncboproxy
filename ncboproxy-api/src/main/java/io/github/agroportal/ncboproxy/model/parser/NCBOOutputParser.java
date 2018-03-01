package io.github.agroportal.ncboproxy.model.parser;

import io.github.agroportal.ncboproxy.model.APIContext;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;

@FunctionalInterface
public interface NCBOOutputParser {
    /**
     * Parse the output JSON-LD from the response of the original REST API and generate a corresponding NCBOOutputModel
     * @param queryResponse The output from the original REST API call
     * @param apiContext An ApiContext object containing essential API information (base URI, encoding, etc)
     * @return The parsed NCBOOutputModel
     */
    NCBOOutputModel parse(String queryResponse, APIContext apiContext) throws com.eclipsesource.json.ParseException;


    /**
     * Create an instance of the default parser implementation, relies on a Json
     * @return An instance of the default parser implementation
     */
    static NCBOOutputParser create() {
        return new DefaultNCBOOutputParser();
    }


}
