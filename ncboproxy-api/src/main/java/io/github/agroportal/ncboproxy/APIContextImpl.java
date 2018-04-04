package io.github.agroportal.ncboproxy;

import io.github.agroportal.ncboproxy.util.ParameterMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIContextImpl implements APIContext {


    @SuppressWarnings("HardcodedFileSeparator")
    private static final String HTTPS_URL_PATTERN = "^((?:https?://)?[^:]+)";

    private final String apiKey;
    private final String serverEncoding;
    private final String restAPIURL;
    private final String method;
    private final String deploymentRoot;

    @Override
    public String getMethod() {
        return method;
    }


    @Override
    public String getServerEncoding() {
        return serverEncoding;
    }

    @Override
    public String getDeploymentRoot() {
        return deploymentRoot;
    }

    @Override
    public String getRestAPIURL() {
        return restAPIURL;
    }

    APIContextImpl(final Properties proxyProperties, final HttpServletRequest servletRequest) {
        serverEncoding = findServerEncoding(proxyProperties);
        final Map<String,String> headers = ParameterMapper.extractHeaders(servletRequest);
        final Map<String,List<String>> queryParameters = ParameterMapper.extractQueryParameters(servletRequest, serverEncoding);

        apiKey = findAPIKey(proxyProperties, queryParameters, headers);
        restAPIURL = getOntologiesApiURI(proxyProperties,servletRequest);
        method = servletRequest.getMethod();

        deploymentRoot = findDeploymentRoot(proxyProperties);
    }

    public APIContextImpl(final String apiKey, final String serverEncoding, final String restAPIURL) {
        this.apiKey = apiKey;
        this.serverEncoding = serverEncoding;
        this.restAPIURL = restAPIURL;
        method = "GET";
        deploymentRoot = "/";
    }

    private static String findDeploymentRoot(final Properties proxyProperties){

        return proxyProperties.containsKey(DEPLOYMENT_ROOT) ? proxyProperties.getProperty(DEPLOYMENT_ROOT) : "/";
    }

    private static String findAPIKey(final Properties proxyProperties,
                                     final Map<String, List<String>> queryParameters,
                                     final Map<String, String> headers) {
        String apikey = "";
        if (queryParameters.containsKey(APIKEY_CONFIGURATION_KEY)) {
            apikey = queryParameters
                    .get(APIKEY_CONFIGURATION_KEY)
                    .stream()
                    .findFirst()
                    .orElse("");
        } else if (headers.containsKey("Authorization") && apikey.isEmpty()) {
            apikey = headers
                    .get("Authorization")
                    .split("=")[1];
        } else if (proxyProperties.containsKey(APIKEY_CONFIGURATION_KEY) && apikey.isEmpty()) {
            apikey = proxyProperties.getProperty(APIKEY_CONFIGURATION_KEY);
        }
        return apikey;
    }

    private static String findServerEncoding(final Properties proxyProperties){
        String encoding = "iso-8859-1";
        if (proxyProperties.containsKey(SERVER_ENCODING)) {
            encoding = proxyProperties.getProperty(SERVER_ENCODING);
        }
        return encoding;
    }

    private static String getOntologiesApiURI(final Properties proxyProperties, final HttpServletRequest request) {
        String ontologiesApiURI = "";
        if (proxyProperties.containsKey(ONTOLOGIES_API_URI)) {
            ontologiesApiURI = proxyProperties.getProperty(ONTOLOGIES_API_URI);
        } else {
            // Extract the base url of the tomcat server and generate the servlet URL from it (the servlet have to be
            // deployed on the same server as the servlet used)
            final Pattern pattern = Pattern.compile(HTTPS_URL_PATTERN);
            final Matcher matcher = pattern.matcher(request
                    .getRequestURL()
                    .toString());
            if (matcher.find()) {
                ontologiesApiURI = matcher.group(1) + ":8080";
            }
        }
        return ontologiesApiURI.trim();
    }


    @Override
    public String getApiKey() {
        return apiKey;
    }
}
