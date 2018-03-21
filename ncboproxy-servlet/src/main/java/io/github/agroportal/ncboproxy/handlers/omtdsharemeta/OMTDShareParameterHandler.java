package io.github.agroportal.ncboproxy.handlers.omtdsharemeta;

import io.github.agroportal.ncboproxy.ServletHandler;
import io.github.agroportal.ncboproxy.output.OutputGenerator;
import io.github.agroportal.ncboproxy.parameters.ParameterHandler;
import io.github.agroportal.ncboproxy.servlet.NCBOProxyServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class OMTDShareParameterHandler implements ParameterHandler {

    private final OutputGenerator outputGenerator;

    private static final Logger logger = LoggerFactory.getLogger(OMTDShareParameterHandler.class);
    private static final String PROPERTY_LIST_PATH = "/omtdshareschema/portalpropertylist.txt";


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
                    if (format.equals(OmTDShareSingleServletHandler.OMTDSHARE_FORMAT_OPTION_NAME)) {
                        servletHandler.registerOutputGenerator(OmTDShareSingleServletHandler.OMTDSHARE_FORMAT_OPTION_NAME, outputGenerator);
                        loadPropertyListToDisplay(queryParameters);
                    }
                });
        return Collections.emptyMap();
    }

    private static void loadPropertyListToDisplay(final Map<String, List<String>> queryParameters) {
        final List<String> display = new ArrayList<>();
        try (final InputStream propertyListStream = OMTDShareParameterHandler.class.getResourceAsStream(PROPERTY_LIST_PATH)) {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(propertyListStream))) {
                display.addAll(reader.lines().collect(Collectors.toList()));
            }
        } catch (final IOException e) {
            logger.error(
                    String.format("OMTDShare Parameter Handler: Cannot retrieve property list to fetch from the class path (%s), reverting to display=all", PROPERTY_LIST_PATH));
        }
        if(display.isEmpty()){
            display.add("all");
        }
        queryParameters.put("display",display);
    }
}
