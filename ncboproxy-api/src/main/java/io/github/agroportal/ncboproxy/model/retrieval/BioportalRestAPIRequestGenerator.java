package io.github.agroportal.ncboproxy.model.retrieval;

import io.github.agroportal.ncboproxy.model.APIContext;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provides static methods to process url used by servlet servlets
 *
 * @author Julien Diener
 */
@SuppressWarnings("HardcodedFileSeparator")
public class BioportalRestAPIRequestGenerator implements RequestGenerator {
    @SuppressWarnings("all")
    private static final long serialVersionUID = 4351112172500760834L;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8 = "application/x-www-form-urlencoded; charset=UTF-8";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String ACCEPT_HEADER = "Accept";
    private static final String ACCEPTED_MIMES = "text/xml, application/json, text/html, text/plain";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String USER_AGENT_HEADER = "User-agent";
    private final Map<String, String> headers;
    private final Map<String, List<String>> queryParameters;
    private final APIContext apiContext;
    private final String queryPath;

    private final boolean isPost;

    BioportalRestAPIRequestGenerator(final APIContext apiContext, final Map<String, List<String>> queryParameters,
                                     final Map<String, String> queryHeaders,
                                     final String queryPath, final boolean isPost) {

        this.queryParameters = Collections.unmodifiableMap(queryParameters);
        headers = Collections.unmodifiableMap(queryHeaders);
        this.apiContext = apiContext;
        this.queryPath = queryPath;
        this.isPost = isPost;
    }

    private String generateFullURL(final String uri) {
        String baseURI = apiContext.getRestAPIURL();
        if (!baseURI.endsWith("/") && !uri.equals("/")) {
            baseURI += "/";
        } else if (baseURI.endsWith("/") && uri.equals("/")) {
            baseURI = baseURI.substring(0, uri.length() - 1);
        }

        final String finalURI;
        if (!uri.endsWith("/")) {
            finalURI = baseURI + uri + "/";
        } else if (uri.endsWith("?")) {
            finalURI = baseURI + uri.substring(0, uri.length() - 1) + "/";
        } else {
            finalURI = baseURI + uri;
        }
        return finalURI;
    }

    @Override
    @SuppressWarnings("HardcodedFileSeparator")
    public HttpURLConnection createRequest() throws IOException {
        String fullURL = generateFullURL(queryPath);
        final String parameterString = createParameterString();

        if(!isPost){
            fullURL+= "?" + parameterString;
        }

        final URL url = new URL(fullURL);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if(isPost) {
            connection.setRequestMethod("POST");
        } else {
            connection.setRequestMethod("GET");
        }
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setRequestProperty(ACCEPT_HEADER, ACCEPTED_MIMES);
        transferHeaders(connection);

        if(isPost) {
            connection.setDoOutput(true);
            connection.setRequestProperty(CONTENT_TYPE,
                    APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
            connection.setRequestProperty(CONTENT_LENGTH,
                    Integer.toString(parameterString.getBytes().length));
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(parameterString);
                wr.flush();
                wr.close();
            }
        }

        return connection;
    }

    private String createParameterString() {
        final StringBuilder parameterString = new StringBuilder();
        boolean first = true;
        for (final Map.Entry<String, List<String>> stringListEntry : queryParameters.entrySet()) {
            if (!first) {
                parameterString.append("&");
            }
            first = false;
            final Optional<String> paramValue = stringListEntry
                    .getValue()
                    .stream()
                    .findFirst();
            paramValue.ifPresent(val -> {
                try {
                    parameterString
                            .append(stringListEntry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(val, apiContext.getServerEncoding()));
                } catch (final UnsupportedEncodingException ignored) {
                }
            });
        }
        return parameterString.toString();
    }

    private void transferHeaders(final HttpURLConnection connection) {
        if (headers.containsKey(AUTHORIZATION_HEADER.toLowerCase())) {
            connection.setRequestProperty(AUTHORIZATION_HEADER, headers.get(AUTHORIZATION_HEADER.toLowerCase()));
        }
        if (headers.containsKey(USER_AGENT_HEADER.toLowerCase())) {
            connection.setRequestProperty(USER_AGENT_HEADER, headers.get(USER_AGENT_HEADER.toLowerCase()));
        }
    }
}
