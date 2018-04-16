package io.github.agroportal.ncboproxy.output;

import io.github.agroportal.ncboproxy.model.NCBOOutputModel;

import java.util.Map;

/**
 * Dispatches the output generation to the right output generator depending on the generator trigger
 */
public interface OutputGeneratorDispatcher extends OutputGenerator {
    /**
     * Generate the output with the appropriate generator associated with the generatorTrigger.
     * @param generatorTrigger The generator trigger
     * @param ncboOutputModel The output model
     * @param outputParameters A set of parameters necessary for the output generation
     * @return The generated servlet output
     */
    ProxyOutput apply(String generatorTrigger, NCBOOutputModel ncboOutputModel, Map<String,String> outputParameters);

    /**
     * This OutputGeneratorDispatcher registers all its output generators in {@code outputGeneratorDispatcher},
     * output generators for already registered output formats are overridden.
     * @param outputGeneratorDispatcher The output generator dispatcher on which to register this OutputGeneratorDispatcher's
     *                                  output generators
     */
    void polymorphicOverride(OutputGeneratorDispatcher outputGeneratorDispatcher);


    /**
     * Register an {@link OutputGenerator} for a given generatorTrigger
     * @param generatorTrigger The trigger format value that troggers the given {@code outputGenerator}
     * @param outputGenerator The output generator to register
     */
    void registerGenerator(final String generatorTrigger, final OutputGenerator outputGenerator);

    static OutputGeneratorDispatcher create(){
        return new NCBOProxyOutputGeneratorDispatcher();
    }

}
