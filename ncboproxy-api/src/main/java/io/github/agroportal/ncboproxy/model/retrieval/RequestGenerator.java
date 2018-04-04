package io.github.agroportal.ncboproxy.model.retrieval;

import io.github.agroportal.ncboproxy.APIContext;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface RequestGenerator {
    HttpURLConnection createRequest() throws IOException;

    static RequestGenerator createPOSTRequestGenerator(final APIContext apiContext, final Map<String, List<String>> queryParameters,
                                                       final Map<String, String> queryHeaders,
                                                       final String queryPath){
        return new BioportalRestAPIRequestGenerator(apiContext,queryParameters,queryHeaders,queryPath, "POST");
    }

    static RequestGenerator createGETRequestGenerator(final APIContext apiContext, final Map<String, List<String>> queryParameters,
                                                      final Map<String, String> queryHeaders,
                                                      final String queryPath){
        return new BioportalRestAPIRequestGenerator(apiContext,queryParameters,queryHeaders,queryPath, "GET");
    }
    static RequestGenerator createHEADRequestGenerator(final APIContext apiContext, final Map<String, List<String>> queryParameters,
                                                      final Map<String, String> queryHeaders,
                                                      final String queryPath){
        return new BioportalRestAPIRequestGenerator(apiContext,queryParameters,queryHeaders,queryPath, "HEAD");
    }
}
