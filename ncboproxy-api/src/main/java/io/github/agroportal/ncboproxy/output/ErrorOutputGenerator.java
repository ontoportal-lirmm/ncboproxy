package io.github.agroportal.ncboproxy.output;


import io.github.agroportal.ncboproxy.model.NCBOOutputModel;

import javax.servlet.http.HttpServletResponse;
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
                .orElseGet(() -> new NCBOProxyOutput(String.format("{ \"errors\" : [ \"Empty Output From Server\"], \"status\":%d}", HttpServletResponse.SC_INTERNAL_SERVER_ERROR), MimeTypes.APPLICATION_JSON));
    }
}
