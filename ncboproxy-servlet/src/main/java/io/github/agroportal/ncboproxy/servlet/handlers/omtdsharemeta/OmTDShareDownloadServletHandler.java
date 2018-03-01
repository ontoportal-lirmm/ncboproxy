package io.github.agroportal.ncboproxy.servlet.handlers.omtdsharemeta;

import io.github.agroportal.ncboproxy.AbstractServletHandler;
import io.github.agroportal.ncboproxy.ServletHandler;
import io.github.agroportal.ncboproxy.model.APIContext;
import io.github.agroportal.ncboproxy.model.NCBOCollection;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.model.parser.NCBOOutputParser;
import io.github.agroportal.ncboproxy.model.retrieval.BioportalRESTRequest;
import io.github.agroportal.ncboproxy.model.retrieval.RequestGenerator;
import io.github.agroportal.ncboproxy.output.OutputGenerator;
import io.github.agroportal.ncboproxy.output.OutputGeneratorDispatcher;
import io.github.agroportal.ncboproxy.output.ProxyOutput;
import io.github.agroportal.ncboproxy.parameters.ParameterHandlerRegistry;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessorRegistry;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OmTDShareDownloadServletHandler extends AbstractServletHandler {
    static final String OMTDSHARE_FORMAT_OPTION_NAME = "omtdshare";
    private final String queryStringPattern;

    private final NCBOOutputParser parser;

    @SuppressWarnings("all")
    public OmTDShareDownloadServletHandler() {
        super(ParameterHandlerRegistry.create(),
                ResponsePostProcessorRegistry.create(),
                OutputGeneratorDispatcher.create());
        queryStringPattern = "/ontologies/([A-Z]+)/submissions/([0-9]+)/download";

        OutputGenerator outputGenerator = new OMTDShareOutputGenerator();

        registerParameterHandler("format", new OMTDShareParameterHandler(outputGenerator), true);

        parser = NCBOOutputParser.create();
    }


    @Override
    public String getQueryStringPattern() {
        return queryStringPattern;
    }

    @Override
    public NCBOOutputModel handleRequest(final Map<String, List<String>> queryParameters,
                                         final Map<String, String> queryHeaders,
                                         final String queryPath,
                                         final ServletHandler servletHandler,
                                         final APIContext apiContext) {
        NCBOOutputModel model;

        final Matcher matcher = Pattern
                .compile(queryStringPattern + ".*")
                .matcher(queryPath);
        if (matcher.find()) {
            final String acronym = matcher.group(1);

            queryParameters.put("display", Collections.singletonList("all"));
            final String submissionsPath = "/ontologies/" + acronym + "/submissions/";
            final RequestGenerator requestGenerator = RequestGenerator.createGETRequestGenerator(apiContext, queryParameters, queryHeaders, submissionsPath);
            try {
                final String result = BioportalRESTRequest.query(requestGenerator);
                model = firstSubmission(parser.parse(result, apiContext));
            } catch (final IOException e) {
                model = error("Cannot fetch submissions information for " + acronym);
            }
        } else {
            model = error("OmTDShare Metadata (handleRequest): Invalid URL, cannot match" +
                    " ontology acronym. Please report the issue on the ncboproxy tracker");
        }


        return model;
    }

    @SuppressWarnings("all")
    private NCBOOutputModel firstSubmission(final NCBOOutputModel ncboOutputModel) {
        return ncboOutputModel.isCollection()
                ? (
                ((NCBOCollection) ncboOutputModel)
                        .stream()
                        .findFirst()
                        .orElse(error("No submissions for target ontology")))
                : error("Invalid API response");
    }

    private NCBOOutputModel error(final String message) {
        return NCBOOutputModel.error(message, ProxyOutput.HTTP_INTERNAL_APPLICATION_ERROR);
    }
}
