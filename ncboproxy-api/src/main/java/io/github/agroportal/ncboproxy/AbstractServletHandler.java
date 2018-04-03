package io.github.agroportal.ncboproxy;

import io.github.agroportal.ncboproxy.model.APIContext;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.model.parser.NCBOOutputParser;
import io.github.agroportal.ncboproxy.model.retrieval.BioportalRESTRequest;
import io.github.agroportal.ncboproxy.model.retrieval.RequestGenerator;
import io.github.agroportal.ncboproxy.model.retrieval.RequestResult;
import io.github.agroportal.ncboproxy.output.OutputGenerator;
import io.github.agroportal.ncboproxy.output.OutputGeneratorDispatcher;
import io.github.agroportal.ncboproxy.parameters.ParameterHandler;
import io.github.agroportal.ncboproxy.parameters.ParameterHandlerRegistry;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessor;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessorRegistry;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles handler registrations and overrides for {@code ServletHandler} implementing classes, please use
 * the following three private methods: registerParameterHandler, registerResponsePostProcessor, registerOutputGenerator
 */
public abstract class AbstractServletHandler implements ServletHandler {

    private static final int HTTP_OK = 300;
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
    public ServletHandler latchToParameterHandlerRegistry(final ParameterHandlerRegistry parameterHandlerRegistry) {
        if (this.parameterHandlerRegistry != null) {
            this.parameterHandlerRegistry.polymorphicOverride(parameterHandlerRegistry);
        }
        return this;
    }

    @Override
    public ServletHandler latchToResponsePostProcessorRegistry(final ResponsePostProcessorRegistry responsePostProcessorRegistry) {
        if (this.responsePostProcessorRegistry != null) {
            this.responsePostProcessorRegistry.polymorphicOverride(responsePostProcessorRegistry);
        }
        return this;
    }

    @Override
    public ServletHandler latchToOutputGeneratorDispatcher(final OutputGeneratorDispatcher outputGeneratorDispatcher) {
        if (this.outputGeneratorDispatcher != null) {
            this.outputGeneratorDispatcher.polymorphicOverride(outputGeneratorDispatcher);
        }
        return this;
    }

    @Override
    public boolean areParameterConstraintsMet(final Map<String, List<String>> queryParameters) {
        return parameterHandlerRegistry.areMandatoryConstraintsSatisfied(queryParameters);
    }

    @SuppressWarnings("all")
    protected void registerParameterHandler(final String pattern,
                                            final ParameterHandler parameterHandler,
                                            final boolean isOptional, String... constrainedValues) {
        if (outputGeneratorDispatcher != null) {
            parameterHandlerRegistry.registerParameterHandler(pattern, parameterHandler, isOptional, constrainedValues);
        }
    }

    @SuppressWarnings("all")
    protected void registerParameterHandler(final String pattern,
                                            final ParameterHandler parameterHandler,
                                            final boolean isOptional) {
        if (outputGeneratorDispatcher != null) {
            parameterHandlerRegistry.registerParameterHandler(pattern, parameterHandler, isOptional);
        }
    }

    protected ServletHandler registerPostProcessor(final ResponsePostProcessor responsePostProcessor) {
        if (responsePostProcessor != null) {
            responsePostProcessorRegistry.registerPostProcessor(responsePostProcessor);
        }
        return this;
    }

    @Override
    public void registerOutputGenerator(final String format, final OutputGenerator outputGenerator) {
        if (outputGeneratorDispatcher != null) {
            outputGeneratorDispatcher.registerGenerator(format, outputGenerator);
        }
    }


    @Override
    public NCBOOutputModel handleRequest(final Map<String, List<String>> queryParameters,
                                         final Map<String, String> queryHeaders,
                                         final String queryPath,
                                         final APIContext apiContext, final Map<String, String> outputProperties) {

        final String matchingPathPattern = ServletHandlerDispatcher.findMatchingPattern(queryPath,getQueryStringPattern());

        queryParameters.remove("format");

        final Matcher matcher = Pattern
                .compile(matchingPathPattern + ".*")
                .matcher(queryPath);

        NCBOOutputModel outputModel;
        if (matcher.find()) {

            final String finalQueryPath = (matcher.groupCount()<2)?queryPath:matcher.group(2);

            final RequestGenerator requestGenerator = isPOSTRequest(apiContext) ?
                    RequestGenerator.createPOSTRequestGenerator(apiContext, queryParameters, queryHeaders, finalQueryPath) :
                    RequestGenerator.createGETRequestGenerator(apiContext, queryParameters, queryHeaders, finalQueryPath);

            RequestResult queryOutput = RequestResult.empty();
            try {
                queryOutput = BioportalRESTRequest.query(requestGenerator);
                outputModel = (isValidJSONOutput(queryOutput)) ? parser.parse(queryOutput, apiContext)
                        : error(queryOutput);
            } catch (final com.eclipsesource.json.ParseException e) {
                outputModel = error(MessageFormat.format("Parse error ({0}) : {1}", e.getMessage(), queryOutput));
            } catch (final UnsupportedOperationException e) {
                outputModel = error(MessageFormat.format("Parse error ({0}) : {1}", e.getMessage()));
            } catch (final IOException e) {
                outputModel =
                        error(MessageFormat.format("Query to REST API failed: {0}", e.getMessage()));
            }
        } else {
            outputModel = error("NCBOProxy: Invalid Query Path. Please report the issue on the ncboproxy tracker");
        }
        return outputModel;
    }

    private NCBOOutputModel error(final String message) {
        return NCBOOutputModel.error(message, HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    private NCBOOutputModel error(final RequestResult requestResult) {
        return NCBOOutputModel.error("Error returned by REST API:" + requestResult.getMessage(), requestResult.getCode());
    }

    private boolean isValidJSONOutput(final RequestResult requestResult) {
        return (requestResult
                .getCode() <= HTTP_OK) || requestResult
                .getMessage()
                .contains("error");
    }

    private boolean isPOSTRequest(final APIContext apiContext) {
        return (apiContext
                .getMethod()
                .toLowerCase()
                .equals("POST"));
    }
}
