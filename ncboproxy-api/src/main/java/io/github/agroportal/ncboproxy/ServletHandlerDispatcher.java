package io.github.agroportal.ncboproxy;


import java.util.Optional;

public interface ServletHandlerDispatcher {
    ServletHandlerDispatcher registerServletHookHandler(ServletHandler servletHandler);
    Optional<ServletHandler> findMatchingHandler(final String queryString);

    static ServletHandlerDispatcher create(){
        return new NCBOProxyServletHandlerDispatcher();
    }
}
