package io.github.agroportal.ncboproxy.output;


import io.github.agroportal.ncboproxy.model.NCBOOutputModel;

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
                        , "application/json");
    }
    static String prettyError(final NCBOOutputModel outputModel) {
        return outputModel.toPrettyString();
    }

    static NCBOOutputModel errorModel(final String message) {
        return NCBOOutputModel.error(message, ProxyOutput.HTTP_INTERNAL_APPLICATION_ERROR);
    }
}
