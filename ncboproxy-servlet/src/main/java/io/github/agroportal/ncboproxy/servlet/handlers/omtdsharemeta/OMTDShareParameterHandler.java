package io.github.agroportal.ncboproxy.servlet.handlers.omtdsharemeta;

import io.github.agroportal.ncboproxy.ServletHandler;
import io.github.agroportal.ncboproxy.output.OutputGenerator;
import io.github.agroportal.ncboproxy.parameters.ParameterHandler;
import io.github.agroportal.ncboproxy.servlet.NCBOProxyServlet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OMTDShareParameterHandler implements ParameterHandler {

    private final OutputGenerator outputGenerator;


    OMTDShareParameterHandler(final OutputGenerator outputGenerator) {
        this.outputGenerator = outputGenerator;
    }

    @Override
    public Map<String, String> processParameter(final Map<String, List<String>> queryParameters,
                                                final Map<String, String> queryHeaders,
                                                final String queryPath,
                                                final ServletHandler servletHandler) {

        queryParameters
                .getOrDefault(NCBOProxyServlet.FORMAT, Collections.emptyList())
                .stream()
                .findFirst()
                .ifPresent(format -> {
                    if (format.equals(OmTDShareDownloadServletHandler.OMTDSHARE_FORMAT_OPTION_NAME)) {
                        servletHandler.registerOutputGenerator(OmTDShareDownloadServletHandler.OMTDSHARE_FORMAT_OPTION_NAME, outputGenerator);
                    }
                });
        queryParameters.remove("format");
        return Collections.emptyMap();
    }
}
