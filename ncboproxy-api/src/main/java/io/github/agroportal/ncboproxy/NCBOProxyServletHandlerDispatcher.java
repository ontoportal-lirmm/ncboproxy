package io.github.agroportal.ncboproxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;

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
    public Optional<ServletHandler> findMatchingHandler(final String queryString) {
        return handlers
                .stream()
                .filter(servletHandler -> doesPathMatchPattern(queryString, servletHandler.getQueryStringPattern()))
                .findFirst();
    }

    private static boolean doesPathMatchPattern(final CharSequence queryString, final String pattern) {
        return Pattern
                .compile(pattern+".*")
                .matcher(queryString)
                .matches();
    }

}
