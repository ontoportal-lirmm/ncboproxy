package io.github.agroportal.ncboproxy.servlet;

import io.github.agroportal.ncboproxy.ServletHandler;
import io.github.agroportal.ncboproxy.ServletHandlerDispatcher;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.OmTDShareMultipleServletHandler;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.OmTDShareSingleServletHandler;
import io.github.agroportal.ncboproxy.APIContext;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.output.OutputGeneratorDispatcher;
import io.github.agroportal.ncboproxy.output.ProxyOutput;
import io.github.agroportal.ncboproxy.parameters.InvalidParameterException;
import io.github.agroportal.ncboproxy.parameters.ParameterHandlerRegistry;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessorRegistry;
import io.github.agroportal.ncboproxy.util.ParameterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;


/**
 * Implements the core features of the NCBO Proxy Service:
 * See README.md for detailed explanation
 * Authors:  Andon Tchechmedjiev
 */
@SuppressWarnings({"HardcodedFileSeparator", "LocalVariableOfConcreteClass", "SerializableHasSerializationMethods"})
//@WebServlet(name = "NCBOProxy", displayName = "NCBO REST API Proxy", urlPatterns = "/*", loadOnStartup = 1)
public class NCBOProxyServlet extends HttpServlet {
    private static final long serialVersionUID = -7313493486599524614L;
    private static final Logger logger = LoggerFactory.getLogger(NCBOProxyServlet.class);
    public static final String FORMAT = "format";
    private static final String UTF8_CONTENT_TYPE_FORMAT_STRING = "%s; charset=UTF-8";

    private Properties proxyProperties;

    private ServletHandlerDispatcher servletHandlerDispatcher;


    @SuppressWarnings("FeatureEnvy")
    public NCBOProxyServlet() {
        try {
            /*
             * Loading configuration properties
             */
            final InputStream proxyPropertiesStream = NCBOProxyServlet.class.getResourceAsStream("/proxy.properties");
            proxyProperties = new Properties();
            proxyProperties.load(proxyPropertiesStream);

            final String restAPIURL = proxyProperties.getProperty(APIContext.ONTOLOGIES_API_URI);
            final PortalType portalType = inferPortalType(restAPIURL);

            servletHandlerDispatcher = ServletHandlerDispatcher.create();
            servletHandlerDispatcher.registerServletHandler(new OmTDShareSingleServletHandler(portalType));
            servletHandlerDispatcher.registerServletHandler(new OmTDShareMultipleServletHandler(portalType));

        } catch (final IOException e) {
            logger.error("Cannot instantiate servlet: {}", e.getLocalizedMessage());
            System.exit(1);
        }
    }

    @SuppressWarnings("IfStatementWithTooManyBranches")
    private static PortalType inferPortalType(final String URL) {
        final PortalType type;
        if (URL.contains("agroportal")) {
            type = PortalType.AGROPORTAL;
        } else if (URL.contains("bioportal") && URL.contains("lirmm")) {
            type = PortalType.SIFR_BIOPORTAL;
        } else if (URL.contains("stageportal") && URL.contains("lirmm")) {
            type = PortalType.STAGEPORTAL;
        } else {
            type = PortalType.NCBO_BIOPORTAL;
        }
        return type;
    }

    private Map<String, List<String>> extractQueryParameters(final ServletRequest request, final APIContext apiContext) {
        return ParameterMapper.extractQueryParameters(request, apiContext.getServerEncoding());
    }


    private String getFormat(final Map<String, List<String>> queryParameters) {
        return queryParameters
                .getOrDefault(FORMAT, Collections.emptyList())
                .stream()
                .findFirst()
                .orElse("json");
    }

    // redirect GET to POST
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    // POST
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        /*
         * Initializing the annotator URI, from the properties if present
         * Otherwise the default behaviour is adopted, assuming the proxy runs on the same machine as the ncbo annotator
         * but on different ports (by default 80 for the proxy and 8080 for the ncbo annotator).
         */
        final APIContext apiContext = APIContext.create(proxyProperties, request);


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
                .findMatchingHandler(request.getPathInfo(), extractQueryParameters(request, apiContext))
                .orElseGet(ServletHandler::defaultHandler)
                .latchToParameterHandlerRegistry(parameterHandlerRegistry);


        final ProxyOutput proxyOutput =
                handleRequest(request, handler, apiContext,
                        parameterHandlerRegistry,
                        postProcessorRegistry,
                        outputGeneratorDispatcher);
        outputContent(proxyOutput, response);
    }

    @SuppressWarnings("FeatureEnvy")
    private ProxyOutput handleRequest(final HttpServletRequest request, final ServletHandler handler, final APIContext apiContext,
                                      final ParameterHandlerRegistry parameterHandlerRegistry,
                                      final ResponsePostProcessorRegistry postProcessorRegistry,
                                      final OutputGeneratorDispatcher outputGeneratorDispatcher) {

        final Map<String, List<String>> queryParameters = extractQueryParameters(request, apiContext);
        final Map<String, String> headers = ParameterMapper.extractHeaders(request);
        final String queryPath = request.getRequestURI();

        NCBOOutputModel outputModel;
        Map<String, String> outputParameters = Collections.emptyMap();
        try {
            //Pre-processing step, matching and handling parameters
            outputParameters =
                    parameterHandlerRegistry.processParameters(queryParameters, headers, queryPath, handler);


            //Handling the request to the REST API (the responsibility befalls implementing classes of {@code ServletHandler}
            //the interface includes an appropriate default implementation that forwards the same request after the preprocessing of
            //the parameters
            outputModel = handler.handleRequest(queryParameters, headers, queryPath, apiContext, outputParameters);

            handler.latchToResponsePostProcessorRegistry(postProcessorRegistry);
            if (!isError(outputModel)) {
                //Handling model post-processing if there was no prior error
                postProcessorRegistry.apply(outputModel, outputParameters);
            }

        } catch (final InvalidParameterException invalidParameter) { // Handling parameter related errors and generating the appropriate error output
            logger.error(invalidParameter.getLocalizedMessage());
            outputModel = NCBOOutputModel.error(invalidParameter.getLocalizedMessage(), ProxyOutput.HTTP_INTERNAL_APPLICATION_ERROR);
        }

        handler.latchToOutputGeneratorDispatcher(outputGeneratorDispatcher);

        final String format = getFormat(queryParameters);
        return outputGeneratorDispatcher.apply(format, outputModel, outputParameters);
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

    private boolean isError(final NCBOOutputModel outputModel) {
        return outputModel.isError();
    }


    @SuppressWarnings({"FeatureEnvy", "LawOfDemeter"})
    private void outputContent(final ProxyOutput proxyOutput, final HttpServletResponse response) throws IOException {
        response.setContentType(String.format(UTF8_CONTENT_TYPE_FORMAT_STRING, proxyOutput.getMimeType()));
        ;
        if (proxyOutput.isBinary()) {
            try (final OutputStream outputStream = response.getOutputStream()) {
                outputStream.write(proxyOutput
                        .transferCustomHeadersToResponse(response)
                        .getBinaryContent());
            }
        } else {
            try (PrintWriter writer = response.getWriter()) {
                writer.println(proxyOutput
                        .transferCustomHeadersToResponse(response)
                        .getStringContent());
                writer.flush();
            }
        }
    }
}
