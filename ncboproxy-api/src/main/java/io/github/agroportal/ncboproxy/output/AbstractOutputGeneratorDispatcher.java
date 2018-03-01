package io.github.agroportal.ncboproxy.output;


import io.github.agroportal.ncboproxy.model.NCBOOutputModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This abstract class implements all the operations for output generator dispatchers, the only responsibility of subclasses
 * is to register generators with their respective trigger terms in their constructors
 */
public class AbstractOutputGeneratorDispatcher implements OutputGeneratorDispatcher {

    private final Map<String, OutputGenerator> dispatcherAssociation = new HashMap<>();

    @Override
    public final void registerGenerator(final String generatorTrigger, final OutputGenerator outputGenerator) {
        dispatcherAssociation.put(generatorTrigger, outputGenerator);
    }

    private boolean isSupported(final String generatorTrigger) {
        return dispatcherAssociation.containsKey(generatorTrigger);
    }

    private OutputGenerator retrieveGenerator(final String generatorTrigger) {
        return dispatcherAssociation.get(generatorTrigger);
    }

    @SuppressWarnings("LawOfDemeter")
    @Override
    public ProxyOutput apply(final String generatorTrigger, final NCBOOutputModel ncboOutputModel, final Map<String, String> outputParameters) {
        final OutputGenerator outputGenerator = retrieveGenerator(generatorTrigger);
        return isSupported(generatorTrigger) ? outputGenerator.apply(Optional.of(ncboOutputModel), outputParameters) : apply(Optional.of(ncboOutputModel), outputParameters);
    }

    @Override
    public void polymorphicOverride(final OutputGeneratorDispatcher outputGeneratorDispatcher) {
        dispatcherAssociation.forEach(outputGeneratorDispatcher::registerGenerator);
    }

    @SuppressWarnings("LawOfDemeter")
    @Override
    public final ProxyOutput apply(final Optional<NCBOOutputModel> outputModel, final Map<String, String> outputParameters) {
        final OutputGenerator json = retrieveGenerator("json");
        return json.apply(outputModel, outputParameters);
    }

}
