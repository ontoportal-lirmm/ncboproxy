package io.github.agroportal.ncboproxy;


import io.github.agroportal.ncboproxy.model.APIContext;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.output.OutputGenerator;
import io.github.agroportal.ncboproxy.output.OutputGeneratorDispatcher;
import io.github.agroportal.ncboproxy.parameters.ParameterHandlerRegistry;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessorRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface ServletHandler {

    String ACRONYM_PATTERN = "([A-Z_0-9]+)";
    String SUBMISSION_ID_PATTERN = "([0-9]+)";

    List<String> getQueryStringPattern();

    void registerOutputGenerator(final String format, final OutputGenerator outputGenerator);

    ServletHandler latchToParameterHandlerRegistry(ParameterHandlerRegistry parameterHandlerRegistry);

    ServletHandler latchToResponsePostProcessorRegistry(ResponsePostProcessorRegistry responsePostProcessorRegistry);

    ServletHandler latchToOutputGeneratorDispatcher(OutputGeneratorDispatcher outputGeneratorDispatcher);


    NCBOOutputModel handleRequest(final Map<String, List<String>> queryParameters,
                                  final Map<String, String> queryHeaders,
                                  final String queryPath,
                                  final APIContext apiContext, Map<String, String> outputProperties);

    static ServletHandler defaultHandler() {
        return new AbstractServletHandler(null, null, null) {
            @Override
            public List<String> getQueryStringPattern() {
                return Collections.singletonList(".*");
            }
        };
    }


}
