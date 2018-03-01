package io.github.agroportal.ncboproxy.parameters;


import io.github.agroportal.ncboproxy.ServletHandler;

import java.util.List;
import java.util.Map;

public interface ParameterHandlerRegistry {
    ParameterHandlerRegistry registerParameterHandler(String name, ParameterHandler parameterHandler, boolean isOptional);
    Map<String,String> processParameters(final Map<String,List<String>> queryParameters,
                                               final Map<String,String> queryHeaders,
                                               final String queryPath,
                                               final ServletHandler servletHandler) throws InvalidParameterException;

    ParameterHandlerRegistry polymorphicOverride(ParameterHandlerRegistry parameterHandlerRegistry);

    static ParameterHandlerRegistry create(){
        return new NCBOProxyParameterHandlerHandlerRegistry();
    }
}
