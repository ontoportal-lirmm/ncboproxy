package io.github.agroportal.ncboproxy.parameters;


import io.github.agroportal.ncboproxy.ServletHandler;

import java.util.*;

public class NCBOProxyParameterHandlerHandlerRegistry implements ParameterHandlerRegistry {


    private final Set<Parameters> parameters;
    private final Map<Parameters, ParameterHandler> parameterHandlers;


    NCBOProxyParameterHandlerHandlerRegistry() {
        parameters = new HashSet<>();
        parameterHandlers = new HashMap<>();
    }

    @SuppressWarnings("LocalVariableOfConcreteClass")
    @Override
    public synchronized ParameterHandlerRegistry registerParameterHandler(final String name, final ParameterHandler parameterHandler, final boolean isOptional) {
        final Parameters currentParameters = new Parameters(name, isOptional);
        if(parameters.contains(currentParameters)){
            parameters.remove(currentParameters);
        }
        parameters.add(currentParameters);
        parameterHandlers.put(currentParameters, parameterHandler);
        return this;
    }

    @Override
    public Map<String, String> processParameters(final Map<String, List<String>> queryParameters,
                                                 final Map<String, String> queryHeaders,
                                                 final String queryPath,
                                                 final ServletHandler servletHandler) throws InvalidParameterException {

        final Map<String, String> outputParameter = new HashMap<>();
        for (final Parameters parameter : parameters) {
            if (!parameter.isOptional() && !queryParameters.containsKey(parameter.getName())) {
                throw new InvalidParameterException(String.format("Mandatory parameter missing -- %s", parameter.getName()));
            } else if (parameter.isAtLeastOneContained(queryParameters)) {
                final Map<String, String> localOutput = parameterHandlers
                        .get(parameter)
                        .processParameter(queryParameters, queryHeaders, queryPath, servletHandler);

                if (localOutput != null) {
                    outputParameter.putAll(localOutput);
                }
            }
        }
        return outputParameter;
    }

    @Override
    public ParameterHandlerRegistry polymorphicOverride(final ParameterHandlerRegistry parameterHandlerRegistry) {
        parameters.forEach(p -> parameterHandlerRegistry.registerParameterHandler(p.getName(), parameterHandlers.get(p), p.isOptional()));
        return this;
    }

    @SuppressWarnings("SuspiciousGetterSetter")
    private static final class Parameters {
        private final List<String> names;
        private final boolean isOptional;

        Parameters(final String names, final boolean isOptional) {
            this.names = new ArrayList<>();
            if (names.contains("|")) {
                Collections.addAll(this.names, names.split("\\|"));
            } else {
                this.names.add(names);
            }
            this.isOptional = isOptional;
        }

        String getName() {
            return names.get(0);
        }

        boolean isAtLeastOneContained(final Map<String, List<String>> parameters) {
            boolean atLeastOne = false;
            for (final String name : names) {
                atLeastOne = parameters.containsKey(name);
                if (atLeastOne) break;
            }
            return atLeastOne;
        }

        boolean isOptional() {
            return isOptional;
        }

        @SuppressWarnings("all")
        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Parameters that = (Parameters) o;
            boolean foundOne= false;
            for(final String thatName: that.names){
                if(names.contains(thatName)){
                    foundOne = true;
                    break;
                }
            }
            return foundOne;
        }

        @Override
        public int hashCode() {

            return Objects.hash(names);
        }
    }
}
