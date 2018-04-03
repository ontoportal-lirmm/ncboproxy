package io.github.agroportal.ncboproxy.handlers.omtdsharemeta;

import com.eclipsesource.json.JsonArray;
import io.github.agroportal.ncboproxy.AbstractServletHandler;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.mapping.OMTDShareModelMapper;
import io.github.agroportal.ncboproxy.model.APIContext;
import io.github.agroportal.ncboproxy.model.NCBOCollection;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.model.parser.NCBOOutputParser;
import io.github.agroportal.ncboproxy.model.retrieval.BioportalRESTRequest;
import io.github.agroportal.ncboproxy.model.retrieval.RequestGenerator;
import io.github.agroportal.ncboproxy.model.retrieval.RequestResult;
import io.github.agroportal.ncboproxy.output.OutputGenerator;
import io.github.agroportal.ncboproxy.output.OutputGeneratorDispatcher;
import io.github.agroportal.ncboproxy.output.ProxyOutput;
import io.github.agroportal.ncboproxy.parameters.ParameterHandlerRegistry;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessorRegistry;
import io.github.agroportal.ncboproxy.servlet.PortalType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.github.agroportal.ncboproxy.handlers.omtdsharemeta.OmTDShareSingleServletHandler.OMTDSHARE_FORMAT_OPTION_NAME;

public class OmTDShareMultipleServletHandler extends AbstractServletHandler {
    private static final int HTTP_OK = 300;
    private final List<String> queryStringPattern;

    private final NCBOOutputParser parser;

    @SuppressWarnings("all")
    public OmTDShareMultipleServletHandler(final PortalType portalType) {
        super(ParameterHandlerRegistry.create(),
                ResponsePostProcessorRegistry.create(),
                OutputGeneratorDispatcher.create());
        queryStringPattern = new ArrayList<>();
        queryStringPattern.add("/?[a-z]+/ontologies/?");
        queryStringPattern.add("/?[a-z]+/submissions/?");

        OutputGenerator outputGenerator = new OMTDShareZipOutputGenerator(portalType);

        registerParameterHandler("format", new OMTDShareParameterHandler(outputGenerator), false, "omtd-share");

        parser = NCBOOutputParser.create();
    }


    @Override
    public List<String> getQueryStringPattern() {
        return Collections.unmodifiableList(queryStringPattern);
    }

    @Override
    public NCBOOutputModel handleRequest(final Map<String, List<String>> queryParameters,
                                         final Map<String, String> queryHeaders,
                                         final String queryPath,
                                         final APIContext apiContext, final Map<String, String> outputProperties) {

        queryParameters.remove("format");
        queryParameters.put("display", Collections.singletonList("all"));

        final String endQueryPath = "/submissions/";
        final RequestGenerator requestGenerator = RequestGenerator.createGETRequestGenerator(apiContext, queryParameters, queryHeaders, endQueryPath);
        NCBOOutputModel model;
        try {
            final RequestResult result = BioportalRESTRequest.query(requestGenerator);
            model = isOk(result) ? parser.parse(result, apiContext) : error(result);
            handleDownloadChecks(model, outputProperties, apiContext, queryParameters,queryHeaders);
        } catch (final IOException e) {
            model = error();
        }


        outputProperties.put("apikey", apiContext.getApiKey());

        if (!isErrorModel(model)) {
            queryParameters.put("format", Collections.singletonList(OMTDSHARE_FORMAT_OPTION_NAME));
        }


        return model;
    }

    @SuppressWarnings("LawOfDemeter")
    private void handleDownloadChecks(final NCBOOutputModel outputModel, final Map<String, String> outputProperties,
                                      final APIContext apiContext, final Map<String, List<String>> queryParameters,
                                      final Map<String, String> queryHeaders) {
        if (outputModel.isCollection()) {
            final NCBOCollection ncboCollection = outputModel
                    .asCollection()
                    .orElse(NCBOCollection.create(new JsonArray()));
            for (final NCBOOutputModel childModel : ncboCollection) {
                final String downloadLink = OMTDShareModelMapper.getOntologyLinkValue(childModel, "download");
                final String acronym = OMTDShareModelMapper.getOntologyPropertyValue(childModel, "acronym");
                if(isDownloadable(downloadLink,apiContext,queryParameters,queryHeaders)){
                    outputProperties.put(acronym,"true");
                }
            }
        }
    }

    private boolean isDownloadable(final String link, final APIContext apiContext, final Map<String, List<String>> queryParameters,
                                   final Map<String, String> queryHeaders) {

        final int pathBegins = link.indexOf("/ontologies");
        final String path = link.substring(pathBegins,link.length());
        final RequestGenerator requestGeneratorDownload = RequestGenerator.createHEADRequestGenerator(apiContext, queryParameters, queryHeaders, path);
        boolean downloadable = false;
        try {
            final RequestResult result = BioportalRESTRequest.query(requestGeneratorDownload);

            if (isOk(result)) {
                downloadable =true;
            }
        } catch (final IOException ignored) {
        }
        return downloadable;
    }

    private boolean isErrorModel(final NCBOOutputModel outputModel) {
        return outputModel.isError();
    }


    private static boolean isOk(final RequestResult requestResult) {
        return (requestResult.getCode() <= HTTP_OK) && !requestResult
                .getMessage()
                .contains("errors");
    }

    private NCBOOutputModel error() {
        return NCBOOutputModel.error("Cannot fetch submissions information for all ontologies or ontology submissions", ProxyOutput.HTTP_INTERNAL_APPLICATION_ERROR);
    }

    private NCBOOutputModel error(final RequestResult requestResult) {
        return NCBOOutputModel.error("Error code from server: " + requestResult.getMessage(), requestResult.getCode());
    }
}
