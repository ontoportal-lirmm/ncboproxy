package io.github.agroportal.ncboproxy.output;


import io.github.agroportal.ncboproxy.model.NCBOOutputModel;

import java.util.Map;
import java.util.Optional;


/**
 * Generates an error output in JSON in the same way as Bioportal Annotator
 */
public class ErrorOutputGenerator implements OutputGenerator {

    @Override
    public ProxyOutput apply(final Optional<NCBOOutputModel> outputModel, final Map<String, String> outputParameters) {
        return outputModel
                .map(ncboOutputModel -> new NCBOProxyOutput(ncboOutputModel
                        .getModelRoot()
                        .toString(), MimeTypes.APPLICATION_JSON))
                .orElseGet(() -> new NCBOProxyOutput("{ \"errors\" : [ \"Empty Output From Server\"], \"status\": 500}", MimeTypes.APPLICATION_JSON));
    }
}
