package io.github.agroportal.ncboproxy;

import java.util.*;

@SuppressWarnings("FeatureEnvy")
class NCBOProxyServletHandlerDispatcher implements ServletHandlerDispatcher {


    private final Collection<ServletHandler> handlers;

    NCBOProxyServletHandlerDispatcher() {
        handlers = new ArrayList<>();
    }

    @Override
    public ServletHandlerDispatcher registerServletHookHandler(final ServletHandler servletHandler) {
        handlers.add(servletHandler);
        return this;
    }

    @Override
    public Optional<ServletHandler> findMatchingHandler(final String queryString, final Map<String, List<String>> queryParameters) {
        return handlers
                .stream()
                .filter(servletHandler -> !ServletHandlerDispatcher
                        .findMatchingPattern(queryString, servletHandler.getQueryStringPattern())
                        .isEmpty()
                        && servletHandler.areParameterConstraintsMet(queryParameters))
                .findFirst();
    }

}
