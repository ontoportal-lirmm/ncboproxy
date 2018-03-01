package io.github.agroportal.ncboproxy;

import io.github.agroportal.ncboproxy.model.APIContext;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.model.parser.NCBOOutputParser;
import io.github.agroportal.ncboproxy.model.retrieval.BioportalRESTRequest;
import io.github.agroportal.ncboproxy.model.retrieval.RequestGenerator;
import io.github.agroportal.ncboproxy.output.OutputGenerator;
import io.github.agroportal.ncboproxy.output.OutputGeneratorDispatcher;
import io.github.agroportal.ncboproxy.output.ProxyOutput;
import io.github.agroportal.ncboproxy.parameters.ParameterHandler;
import io.github.agroportal.ncboproxy.parameters.ParameterHandlerRegistry;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessor;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessorRegistry;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Handles handler registrations and overrides for {@code ServletHandler} implementing classes, please use
 * the following three private methods: registerParameterHandler, registerResponsePostProcessor, registerOutputGenerator
 */
public abstract class AbstractServletHandler implements ServletHandler {

    private final ParameterHandlerRegistry parameterHandlerRegistry;
    private final ResponsePostProcessorRegistry responsePostProcessorRegistry;
    private final OutputGeneratorDispatcher outputGeneratorDispatcher;
    private final NCBOOutputParser parser;

    protected AbstractServletHandler(final ParameterHandlerRegistry parameterHandlerRegistry,
                                     final ResponsePostProcessorRegistry responsePostProcessorRegistry,
                                     final OutputGeneratorDispatcher outputGeneratorDispatcher) {
        this.parameterHandlerRegistry = parameterHandlerRegistry;
        this.responsePostProcessorRegistry = responsePostProcessorRegistry;
        this.outputGeneratorDispatcher = outputGeneratorDispatcher;
        parser = NCBOOutputParser.create();
    }

    @Override
    public ServletHandler latchToRootParameterHandlerRegistry(final ParameterHandlerRegistry parameterHandlerRegistry) {
        if (this.parameterHandlerRegistry != null) {
            this.parameterHandlerRegistry.polymorphicOverride(parameterHandlerRegistry);
        }
        return this;
    }

    @Override
    public ServletHandler latchResponsePostProcessorRegistry(final ResponsePostProcessorRegistry responsePostProcessorRegistry) {
        if (this.responsePostProcessorRegistry != null) {
            this.responsePostProcessorRegistry.polymorphicOverride(responsePostProcessorRegistry);
        }
        return this;
    }

    @Override
    public ServletHandler latchOutputGeneratorDispatcher(final OutputGeneratorDispatcher outputGeneratorDispatcher) {
        if (this.outputGeneratorDispatcher != null) {
            this.outputGeneratorDispatcher.polymorphicOverride(outputGeneratorDispatcher);
        }
        return this;
    }

    @SuppressWarnings("all")
    protected void registerParameterHandler(final String pattern,
                                            final ParameterHandler parameterHandler,
                                            final boolean isOptional) {
        if (outputGeneratorDispatcher != null) {
            parameterHandlerRegistry.registerParameterHandler(pattern, parameterHandler, isOptional);
        }
    }

    @Override
    public ServletHandler registerPostProcessor(final ResponsePostProcessor responsePostProcessor) {
        if (responsePostProcessor != null) {
            responsePostProcessorRegistry.registerPostProcessor(responsePostProcessor);
        }
        return this;
    }

    @Override
    public ServletHandler registerOutputGenerator(final String format, final OutputGenerator outputGenerator) {
        if (outputGeneratorDispatcher != null) {
            outputGeneratorDispatcher.registerGenerator(format, outputGenerator);
        }
        return this;
    }


    @Override
    public NCBOOutputModel handleRequest(final Map<String, List<String>> queryParameters,
                                         final Map<String, String> queryHeaders,
                                         final String queryPath,
                                         final ServletHandler servletHandler,
                                         final APIContext apiContext) {
        final RequestGenerator requestGenerator = (apiContext
                .getMethod()
                .toLowerCase()
                .equals("POST")) ?
                RequestGenerator.createPOSTRequestGenerator(apiContext, queryParameters, queryHeaders, queryPath) :
                RequestGenerator.createGETRequestGenerator(apiContext, queryParameters, queryHeaders, queryPath);
        NCBOOutputModel outputModel;
        String queryOutput = "";
        try {
            queryOutput = BioportalRESTRequest.query(requestGenerator);
        } catch (final IOException e) {
            outputModel =
                    error(MessageFormat.format("Query to REST API failed: {0}", e.getMessage()));
        }

        try {
            outputModel = parser.parse(queryOutput, apiContext);
        } catch (final com.eclipsesource.json.ParseException e) {
            outputModel = error(MessageFormat.format("Parse error ({0}) : {1}", e.getMessage(), queryOutput));
        } catch (final UnsupportedOperationException e) {
            outputModel = error(MessageFormat.format("Parse error ({0}) : {1}", e.getMessage()));
        }
        return outputModel;
    }

    private NCBOOutputModel error(final String message) {
        return NCBOOutputModel.error(message, ProxyOutput.HTTP_INTERNAL_APPLICATION_ERROR);
    }
}
