package io.github.agroportal.ncboproxy;


import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

public interface APIContext {
    String APIKEY_CONFIGURATION_KEY = "apikey";
    String SERVER_ENCODING = "server.encoding";
    String ONTOLOGIES_API_URI = "ontologiesApiURI";
    String DEPLOYMENT_ROOT = "deploymentRoot";

    String getApiKey();
    String getServerEncoding();
    String getRestAPIURL();
    String getMethod();
    String getDeploymentRoot();

    static APIContext create(final Properties proxyProperties, final HttpServletRequest servletRequest){
        return new APIContextImpl(proxyProperties,servletRequest);
    }
}
