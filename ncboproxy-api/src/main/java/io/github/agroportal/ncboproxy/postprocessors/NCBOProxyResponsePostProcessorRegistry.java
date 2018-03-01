package io.github.agroportal.ncboproxy.postprocessors;

import io.github.agroportal.ncboproxy.model.NCBOOutputModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NCBOProxyResponsePostProcessorRegistry implements ResponsePostProcessorRegistry {

    private final List<ResponsePostProcessor> postProcessors;

    NCBOProxyResponsePostProcessorRegistry() {
        postProcessors = new ArrayList<>();
    }

    @Override
    public void registerPostProcessor(final ResponsePostProcessor responsePostProcessor) {
        if(!postProcessors.contains(responsePostProcessor)){
            postProcessors.add(responsePostProcessor);
        }
    }

    @Override
    public void polymorphicOverride(final ResponsePostProcessorRegistry responsePostProcessorRegistry) {
        postProcessors.forEach(responsePostProcessorRegistry::registerPostProcessor);
    }

    @Override
    public void clear() {
        postProcessors.clear();
    }

    @Override
    public Void apply(final NCBOOutputModel outputModel, final Map<String, String> parameters) {
        for(final ResponsePostProcessor responsePostProcessor: postProcessors){
            responsePostProcessor.apply(outputModel,parameters);
        }
        return null;
    }
}
