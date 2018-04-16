package io.github.agroportal.ncboproxy.parameters;


import io.github.agroportal.ncboproxy.ServletHandler;

import java.util.*;
import java.util.stream.Collectors;

public class NCBOProxyParameterHandlerRegistry implements ParameterHandlerRegistry {


    private final Set<Parameters> parameters;
    private final Map<Parameters, ParameterHandler> parameterHandlers;


    NCBOProxyParameterHandlerRegistry() {
        parameters = new HashSet<>();
        parameterHandlers = new HashMap<>();
    }


    @Override
    public synchronized ParameterHandlerRegistry registerParameterHandler(final String name, final ParameterHandler parameterHandler, final boolean isOptional) {
        return registerParameterHandler(name, parameterHandler, isOptional, new String[0]);
    }

    @SuppressWarnings("LocalVariableOfConcreteClass")
    @Override
    public synchronized ParameterHandlerRegistry registerParameterHandler(final String name, final ParameterHandler parameterHandler, final boolean isOptional, final String... constrainedValues) {
        final Parameters currentParameters = new Parameters(name, isOptional, constrainedValues);
        if (parameters.contains(currentParameters)) {
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
            } else if (parameter.matchesQueryParameters(queryParameters)) {
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
    public ParameterHandlerRegistry polymorphicOverride(final ParameterHandlerRegistry otherParameterHandlerRegistry) {
        parameters.forEach(p ->
                otherParameterHandlerRegistry.registerParameterHandler(
                        p.getName(), parameterHandlers.get(p), p.isOptional())
        );
        return this;
    }

    @Override
    public boolean areMandatoryConstraintsSatisfied(final Map<String, List<String>> queryParameters) {
        boolean endCondition = true;

        for (final Parameters parameter : parameters) {
            if (!parameter.isOptional() &&
                    (!queryParameters.containsKey(parameter.getName())
                            || !parameter.matchesQueryParameters(queryParameters))) {
                endCondition = false;
                break;
            }
        }
        return endCondition;
    }

    @SuppressWarnings("SuspiciousGetterSetter")
    private static final class Parameters {
        private final List<String> names;
        private final List<String> constrainedValues;
        private final boolean isOptional;

        Parameters(final String names, final boolean isOptional, final String... constrainedValues) {
            this.names = new ArrayList<>();
            if (names.contains("|")) {
                Collections.addAll(this.names, names.split("\\|"));
            } else {
                this.names.add(names);
            }
            this.isOptional = isOptional;
            this.constrainedValues = Arrays
                    .stream(constrainedValues)
                    .collect(Collectors.toList());
        }

        String getName() {
            return names.get(0);
        }

        boolean matchesQueryParameters(final Map<String, List<String>> queryParameters) {
            boolean atLeastOne = false;
            for (final String name : names) {
                if (queryParameters.containsKey(name) && !constrainedValues.isEmpty()) {
                    for (final String value : constrainedValues) {
                        atLeastOne = queryParameters
                                .get(name)
                                .contains(value);
                    }
                } else {
                    atLeastOne = true;
                }
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
            boolean foundOne = false;
            for (final String thatName : that.names) {
                if (names.contains(thatName)) {
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
