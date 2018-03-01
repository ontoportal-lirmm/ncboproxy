package io.github.agroportal.ncboproxy.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ParameterMapper {

    private ParameterMapper() {
    }


    /**
     * Produce key/value list maps for input query parameters (query string or form data) from a {@link ServletRequest}
     * @param request The servlet request
     * @param serverEncoding The encoding configured on the servlet server, should be retrieved as an external parameter of
     *                       the servlet
     * @return A map of key/value lists for {@code request}
     */
    public static Map<String, List<String>> extractQueryParameters(final ServletRequest request, final String serverEncoding) {
        return request
                .getParameterMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Arrays.stream(e.getValue()).map(value -> {
                            try {
                                return new String(e.getValue()[0].getBytes(Charset.forName(serverEncoding.toUpperCase())), "utf-8");
                            } catch (UnsupportedEncodingException ignored) {
                                return "";
                            }
                        }).collect(Collectors.toList())
                ));
    }


    /**
     * Extract request headers from an {@link HttpServletRequest} object
     * @param request The HttpServletRequest object
     * @return The key/value map containing the headers
     */
    public static Map<String, String> extractHeaders(final HttpServletRequest request) {
        return Collections
                .list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        e -> e,
                        request::getHeader
                ));
    }
}
