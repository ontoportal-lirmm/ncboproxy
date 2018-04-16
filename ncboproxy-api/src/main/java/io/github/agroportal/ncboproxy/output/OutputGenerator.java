package io.github.agroportal.ncboproxy.output;


import io.github.agroportal.ncboproxy.model.NCBOOutputModel;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Specification of an {@link OutputGenerator} that produces a String output from an annotation model
 */
@FunctionalInterface
public interface OutputGenerator extends BiFunction<Optional<NCBOOutputModel>,Map<String,String>,ProxyOutput> {
    /**
     * Generates the output from the list of annotations as defined by the annotation model API
     *
     * @param outputModel The output of the original API call
     * @return The corresponding output
     */
    @SuppressWarnings("all")
    @Override
    ProxyOutput apply(Optional<NCBOOutputModel> outputModel, Map<String,String> outputParameters);

    static ProxyOutput errorOutput(final String message){
        return ProxyOutput
                .create(prettyError(errorModel(message))
                        , MimeTypes.APPLICATION_JSON);
    }
    static String prettyError(final NCBOOutputModel outputModel) {
        return outputModel.toPrettyString();
    }

    static NCBOOutputModel errorModel(final String message) {
        return NCBOOutputModel.error(message, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
