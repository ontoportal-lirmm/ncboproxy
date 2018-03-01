package io.github.agroportal.ncboproxy.servlet;

import io.github.agroportal.ncboproxy.ServletHandler;
import io.github.agroportal.ncboproxy.ServletHandlerDispatcher;
import io.github.agroportal.ncboproxy.model.APIContext;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.output.OutputGeneratorDispatcher;
import io.github.agroportal.ncboproxy.output.ProxyOutput;
import io.github.agroportal.ncboproxy.parameters.InvalidParameterException;
import io.github.agroportal.ncboproxy.parameters.ParameterHandlerRegistry;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessorRegistry;
import io.github.agroportal.ncboproxy.servlet.handlers.omtdsharemeta.OmTDShareDownloadServletHandler;
import io.github.agroportal.ncboproxy.util.ParameterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.BiFunction;


/**
 * Implements the core features of the AnnotatorPlus web services:
 * - It queries the suitable bioportal annotation server (w.r.t implementing subclasses)
 * - adds several new features
 *
 * @authors Julien Diener, Emmanuel Castanier, Andon Tchechmedjiev
 */
@SuppressWarnings({"HardcodedFileSeparator", "LocalVariableOfConcreteClass"})
//@WebServlet(name = "NCBOProxy", displayName = "NCBO REST API Proxy", urlPatterns = "/*", loadOnStartup = 1)
public class NCBOProxyServlet extends HttpServlet {
    private static final long serialVersionUID = -7313493486599524614L;
    private static final Logger logger = LoggerFactory.getLogger(NCBOProxyServlet.class);
    public static final String FORMAT = "format";
    private static final String UTF8_CONTENT_TYPE_FORMAT_STRING = "%s; charset=UTF-8";

    private Properties proxyProperties;

    private final ServletHandlerDispatcher servletHandlerDispatcher;

    @SuppressWarnings({"OverlyCoupledMethod", "FeatureEnvy"})
    public NCBOProxyServlet() {
        try {
            /*
             * Loading configuration properties
             */
            final InputStream proxyPropertiesStream = NCBOProxyServlet.class.getResourceAsStream("/ncboProxy.properties");
            proxyProperties = new Properties();
            proxyProperties.load(proxyPropertiesStream);

        } catch (final IOException e) {
            logger.error("Cannot instantiate servlet: {}", e.getLocalizedMessage());
            System.exit(1);
        }
        servletHandlerDispatcher = ServletHandlerDispatcher.create();
        servletHandlerDispatcher.registerServletHookHandler(new OmTDShareDownloadServletHandler());


    }

    private ParameterHandlerRegistry registerDefaultParameterHandlers(final ParameterHandlerRegistry parameterHandlerRegistry) {
        parameterHandlerRegistry
                .registerParameterHandler(FORMAT,
                        (parameters, responsePostProcessorRegistry, outputGeneratorDispatcher, outputParameters) -> {
                            final Optional<String> format = parameters
                                    .getOrDefault(FORMAT, Collections.emptyList())
                                    .stream()
                                    .findFirst();
                            if (format.isPresent() && format
                                    .get()
                                    .equals("json")) {
                                parameters.remove(FORMAT);
                            }
                            return null;
                        }, true);
        return parameterHandlerRegistry;
    }

    private Map<String, String> handleParameters(final Map<String, List<String>> queryParameters,
                                                 final Map<String, String> headers,
                                                 final ParameterHandlerRegistry parameterHandlerRegistry,
                                                 final ServletHandler handler,
                                                 final String queryPath) throws InvalidParameterException {
        return parameterHandlerRegistry.processParameters(queryParameters, headers, queryPath, handler);
    }

    private void handlePostProcessing(final BiFunction<NCBOOutputModel, Map<String, String>, Void> responsePostProcessorRegistry,
                                      final NCBOOutputModel outputModel, final Map<String, String> outputParameters) {
        responsePostProcessorRegistry.apply(outputModel, outputParameters);
    }

    private String getFormat(final ServletRequest request) {
        final String format = request.getParameter(FORMAT);
        return (format != null) ? (format) : "json";
    }

    private void outputContent(final ProxyOutput proxyOutput, final ServletResponse response, final PrintWriter output) {
        response.setContentType(String.format(UTF8_CONTENT_TYPE_FORMAT_STRING, proxyOutput.getMimeType()));
        output.println(proxyOutput.getContent());
        output.flush();
    }

    // redirect GET to POST
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    // POST
    @SuppressWarnings({"LocalVariableOfConcreteClass", "LawOfDemeter"})
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        /*
         * Initializing the annotator URI, from the properties if present
         * Otherwise the default behaviour is adopted, assuming the proxy runs on the same machine as the ncbo annotator
         * but on different ports (by default 80 for the proxy and 8080 for the ncbo annotator).
         */
        final String format = getFormat(request);
        final APIContext apiContext = APIContext.create(proxyProperties, request);

        final Map<String, List<String>> queryParameters = ParameterMapper.extractQueryParameters(request, apiContext.getServerEncoding());
        final Map<String, String> headers = ParameterMapper.extractHeaders(request);


        final ParameterHandlerRegistry parameterHandlerRegistry =
                registerDefaultParameterHandlers(ParameterHandlerRegistry.create());
        final ResponsePostProcessorRegistry postProcessorRegistry = ResponsePostProcessorRegistry.create();
        final OutputGeneratorDispatcher outputGeneratorDispatcher = OutputGeneratorDispatcher.create();

        /*  Try to find an appropriate handler to handle the query string from the request amongst the registered handlers
         *  If no appropriate handler can be found, we use the default handler that just forwards the request and the response
         *  We then latch the parameter, post-processor and output generator registries of the selected handler to the
         *  local registries.
         */
        final ServletHandler handler = servletHandlerDispatcher
                .findMatchingHandler(request.getPathInfo())
                .orElseGet(ServletHandler::defaultHandler)
                .latchToRootParameterHandlerRegistry(parameterHandlerRegistry)
                .latchResponsePostProcessorRegistry(postProcessorRegistry)
                .latchOutputGeneratorDispatcher(outputGeneratorDispatcher);


        final String queryPath = request.getRequestURI();

        NCBOOutputModel outputModel;
        Map<String, String> outputParameters = Collections.emptyMap();
        try {
            //Pre-processing step, matching and handling parameters
            outputParameters =
                    handleParameters(queryParameters, headers, parameterHandlerRegistry, handler, queryPath);

            //Handling the request to the REST API (the responsibility befalls implementing classes of {@code ServletHandler}
            //the interface includes an appropriate default implementation that forwards the same request after the preprocessing of
            //the parameters
            outputModel = handler.handleRequest(queryParameters, headers, queryPath, handler,apiContext);

            if (!outputModel.isError()) {
                //Handling model post-processing if there was no prior error
                handlePostProcessing(postProcessorRegistry, outputModel, outputParameters);
            }

        } catch (final InvalidParameterException invalidParameter) { // Handling parameter related errors and generating the appropriate error output
            logger.error(invalidParameter.getLocalizedMessage());
            outputModel = NCBOOutputModel.error(invalidParameter.getLocalizedMessage(), ProxyOutput.HTTP_INTERNAL_APPLICATION_ERROR);
        }
        final ProxyOutput proxyOutput = outputGeneratorDispatcher.apply(format, outputModel, outputParameters);
        outputContent(proxyOutput, response, response.getWriter());
    }
}
