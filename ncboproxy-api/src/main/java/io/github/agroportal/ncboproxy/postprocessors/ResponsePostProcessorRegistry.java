package io.github.agroportal.ncboproxy.postprocessors;


/**
 * Specification of a registry for {@link ResponsePostProcessor} components that allows registering and chain-applying the
 * post processors.
 */
public interface ResponsePostProcessorRegistry extends ResponsePostProcessor{
    /**
     * Register a {@link ResponsePostProcessor}
     *
     * @param responsePostProcessor The post processor instance
     */
    void registerPostProcessor(ResponsePostProcessor responsePostProcessor);

    void polymorphicOverride(ResponsePostProcessorRegistry responsePostProcessorRegistry);

    /**
     * Remove all registered post-processors
     */
    void clear();

    static ResponsePostProcessorRegistry create(){
        return new NCBOProxyResponsePostProcessorRegistry();
    }
}
