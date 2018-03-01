package io.github.agroportal.ncboproxy;


import io.github.agroportal.ncboproxy.model.APIContext;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.output.OutputGenerator;
import io.github.agroportal.ncboproxy.output.OutputGeneratorDispatcher;
import io.github.agroportal.ncboproxy.parameters.ParameterHandlerRegistry;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessor;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessorRegistry;

import java.util.List;
import java.util.Map;

public interface ServletHandler {
    String getQueryStringPattern();

    ServletHandler registerPostProcessor(final ResponsePostProcessor responsePostProcessor);

    ServletHandler registerOutputGenerator(final String format, final OutputGenerator outputGenerator);

    ServletHandler latchToRootParameterHandlerRegistry(ParameterHandlerRegistry parameterHandlerRegistry);

    ServletHandler latchResponsePostProcessorRegistry(ResponsePostProcessorRegistry responsePostProcessorRegistry);

    ServletHandler latchOutputGeneratorDispatcher(OutputGeneratorDispatcher outputGeneratorDispatcher);


    NCBOOutputModel handleRequest(final Map<String, List<String>> queryParameters,
                                  final Map<String, String> queryHeaders,
                                  final String queryPath,
                                  final ServletHandler servletHandler,
                                  final APIContext apiContext);

    static ServletHandler defaultHandler() {
        return new AbstractServletHandler(null, null,null) {
            @Override
            public String getQueryStringPattern() {
                return ".*";
            }
        };
    }


}
