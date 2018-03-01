package io.github.agroportal.ncboproxy.parameters;


//import org.sifrproject.postannotation.api.PostAnnotationRegistry;

import io.github.agroportal.ncboproxy.ServletHandler;
import io.github.agroportal.ncboproxy.postprocessors.ResponsePostProcessorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Handler for a parameter passed to the Proxy Servlet. A parameter handler should be registered in a {@link ParameterHandlerRegistry}.
 * Naming conversion ParameterNameParameterHandler, where ParameterName is the getName of the parameter in Java style.
 * For example, for a parameter names semantic_groups the hander getName should be SemanticGroupsParameterHandler.
 * For a parameter named 'context', the handler getName should be ContextParameterHandler and so forth...
 */
@FunctionalInterface
public interface ParameterHandler {

    /**
     * Callback that handles the parameter. Parameter handlers should directly register post-annotation components in
     * the {@link ResponsePostProcessorRegistry} passed as a parameter of the callback.
     *
     * @param queryParameters             The query parameters as a map
     * @param queryHeaders                 Headers of the query as a map
     * @param servletHandler The servlet proxy handler
     * @throws InvalidParameterException This exception must be thrown, if the format of the parameter or its options
     *                                   are incorrect or ill-formed.
     */
    @SuppressWarnings("all")
    Map<String,String> processParameter(final Map<String,List<String>> queryParameters,
                                        final Map<String,String> queryHeaders,
                                        final String queryPath,
                                        final ServletHandler servletHandler) throws InvalidParameterException;
}
