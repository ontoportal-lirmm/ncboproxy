package io.github.agroportal.ncboproxy.postprocessors;

import io.github.agroportal.ncboproxy.model.NCBOOutputModel;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Appends annotations to an existing Bioportal Annotation Model.
 * The annotations in question should be supported by the annotation model
 */
@FunctionalInterface
public interface ResponsePostProcessor extends BiFunction<NCBOOutputModel, Map<String,String>,Void> {
    /**
     * Perform the post-annotation
     *
     * @param outputModel The output from the original REST API
     * @param parameters  Parameters for the post processing
     */
    @SuppressWarnings("all")
    @Override
    Void apply(final NCBOOutputModel outputModel, final Map<String, String> parameters);
}
