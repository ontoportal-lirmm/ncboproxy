package io.github.agroportal.ncboproxy.model.retrieval;

import io.github.agroportal.ncboproxy.APIContext;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final String method;

    BioportalRestAPIRequestGenerator(final APIContext apiContext, final Map<String, List<String>> queryParameters,
                                     final Map<String, String> queryHeaders,
                                     final String queryPath, final String method) {

        this.queryParameters = Collections.unmodifiableMap(queryParameters);
        headers = Collections.unmodifiableMap(queryHeaders);
        this.apiContext = apiContext;
        this.queryPath = queryPath;
        this.method = method;
    }

    @SuppressWarnings("FeatureEnvy")
    private String generateFullURL(final String path) {
        String baseURI = apiContext.getRestAPIURL();
        final String finalPath =
                path.contains(apiContext.getDeploymentRoot()) ?
                        path.replaceAll("/" + apiContext.getDeploymentRoot(), "")
                        : path;
        if (baseURI.endsWith("/")) {
            baseURI = baseURI.substring(0, baseURI.length() - 1);
        }

        final String finalURI;
        if (!finalPath.endsWith("/")) {
            finalURI = baseURI + finalPath + "/";
        } else if (finalPath.endsWith("?")) {
            finalURI = baseURI + finalPath.substring(0, finalPath.length() - 1) + "/";
        } else {
            finalURI = baseURI + finalPath;
        }
        return finalURI;
    }

    @Override
    @SuppressWarnings("HardcodedFileSeparator")
    public HttpURLConnection createRequest() throws IOException {
        String fullURL = generateFullURL(queryPath);
        final String parameterString = createParameterString();

        if (!method
                .toLowerCase()
                .equals("post")) {
            fullURL += "?" + parameterString;
        }

        final URL url = new URL(fullURL);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        switch (method.toLowerCase()) {
            case "post":
                connection.setRequestMethod("POST");
                break;
            case "head":
                connection.setRequestMethod("HEAD");
                break;
            default:
                connection.setRequestMethod("GET");
                break;
        }
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setRequestProperty(ACCEPT_HEADER, ACCEPTED_MIMES);
        transferHeaders(connection);

        if (method
                .toLowerCase()
                .equals("post")) {
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
            final String paramValue = stringListEntry
                    .getValue()
                    .stream()
                    .map(p -> {
                        try {
                            return (URLEncoder.encode(p, apiContext.getServerEncoding()));
                        } catch (UnsupportedEncodingException ignored) {
                        }
                        return p;
                    })
                    .collect(Collectors.joining(","));

            parameterString
                    .append(stringListEntry.getKey())
                    .append("=")
                    .append(paramValue);

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
