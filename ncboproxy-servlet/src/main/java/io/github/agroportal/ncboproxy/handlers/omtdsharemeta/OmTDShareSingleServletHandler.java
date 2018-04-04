package io.github.agroportal.ncboproxy.handlers.omtdsharemeta;

import io.github.agroportal.ncboproxy.AbstractServletHandler;
import io.github.agroportal.ncboproxy.ServletHandlerDispatcher;
import io.github.agroportal.ncboproxy.APIContext;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils.OMTDUtilityMapper;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OmTDShareSingleServletHandler extends AbstractServletHandler {
    static final String OMTDSHARE_FORMAT_OPTION_NAME = "omtd-share";
    private static final int HTTP_OK = 300;
    private final List<String> queryPathPattern;

    private final NCBOOutputParser parser;

    @SuppressWarnings("all")
    public OmTDShareSingleServletHandler(final PortalType portalType) {
        super(ParameterHandlerRegistry.create(),
                ResponsePostProcessorRegistry.create(),
                OutputGeneratorDispatcher.create());
        queryPathPattern = new ArrayList<>();
        queryPathPattern.add(String.format("/?[a-z]*/ontologies/%s/submissions/%s", ACRONYM_PATTERN, SUBMISSION_ID_PATTERN));
        queryPathPattern.add(String.format("/?[a-z]*/ontologies/%s/(latest_submission)", ACRONYM_PATTERN));
        queryPathPattern.add(String.format("/?[a-z]*/ontologies/%s", ACRONYM_PATTERN));

        OutputGenerator outputGenerator = new OMTDShareOutputGenerator(portalType);

        registerParameterHandler("format", new OMTDShareParameterHandler(outputGenerator), false, "omtd-share");

        parser = NCBOOutputParser.create();
    }


    @Override
    public List<String> getQueryPathPattern() {
        return Collections.unmodifiableList(queryPathPattern);
    }

    @Override
    public NCBOOutputModel handleRequest(final Map<String, List<String>> queryParameters,
                                         final Map<String, String> queryHeaders,
                                         final String queryPath,
                                         final APIContext apiContext, final Map<String, String> outputProperties) {

        final String matchingPathPattern = ServletHandlerDispatcher.findMatchingPattern(queryPath, queryPathPattern);

        queryParameters.remove("format");

        final Matcher matcher = Pattern
                .compile(matchingPathPattern + ".*")
                .matcher(queryPath);
        NCBOOutputModel model;
        if (matcher.find()) {
            final String acronym = matcher.group(1);

            final String number = (matcher.groupCount()<2) ? "latest_submission" : matcher.group(2);

            queryParameters.put("display", OMTDUtilityMapper.getDisplayPropertyValue());
            final String submissionsPath = number.contains("latest") ?
                    ("/ontologies/" + acronym + "/latest_submission/") :
                    ("/ontologies/" + acronym + "/submissions/" + Integer.valueOf(number) + "/");
            final RequestGenerator requestGenerator = RequestGenerator.createGETRequestGenerator(apiContext, queryParameters, queryHeaders, submissionsPath);
            try {
                final RequestResult result = BioportalRESTRequest.query(requestGenerator);
                model = isOk(result) ? parser.parse(result, apiContext) : error(result);
            } catch (final IOException e) {
                model = error("Cannot fetch submissions information for " + acronym);
            }
            final String downloadPath = number.contains("latest")
                    ? ("/ontologies/" + acronym + "/latest_submission/download")
                    : ("/ontologies/" + acronym + "/submissions/" + Integer.valueOf(number) + "/download");
            final RequestGenerator requestGeneratorDownload = RequestGenerator.createHEADRequestGenerator(apiContext, queryParameters, queryHeaders, downloadPath);
            try {
                final RequestResult result = BioportalRESTRequest.query(requestGeneratorDownload);

                if (isOk(result)) {
                    outputProperties.put("downloadable", "true");
                }
            } catch (final IOException ignored) {
            }

            outputProperties.put("apikey", apiContext.getApiKey());
        } else {
            model = error("OmTDShare Metadata (handleRequest): Invalid URL, cannot match" +
                    " ontology acronym. Please report the issue on the ncboproxy tracker");
        }

        if(!isErrorModel(model)){
            queryParameters.put("format",Collections.singletonList(OMTDSHARE_FORMAT_OPTION_NAME));
        }


        return model;
    }

    private boolean isErrorModel(final NCBOOutputModel outputModel){
        return outputModel.isError();
    }


    private static boolean isOk(final RequestResult requestResult) {
        return (requestResult.getCode() <= HTTP_OK) && !requestResult
                .getMessage()
                .contains("errors");
    }

    private NCBOOutputModel error(final String message) {
        return NCBOOutputModel.error(message, ProxyOutput.HTTP_INTERNAL_APPLICATION_ERROR);
    }

    private NCBOOutputModel error(final RequestResult requestResult) {
        return NCBOOutputModel.error("Error code from server: " + requestResult.getMessage(), requestResult.getCode());
    }
}
