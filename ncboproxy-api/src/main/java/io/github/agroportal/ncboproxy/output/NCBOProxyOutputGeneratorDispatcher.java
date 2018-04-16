package io.github.agroportal.ncboproxy.output;


import io.github.agroportal.ncboproxy.model.NCBOErrorModel;

public class NCBOProxyOutputGeneratorDispatcher extends AbstractOutputGeneratorDispatcher {

    private static final String ERROR_OUTPUT = NCBOErrorModel.ERROR_FORMAT_STRING;

    NCBOProxyOutputGeneratorDispatcher() {
        super();
        registerGenerator("json", new JSONOutputGenerator());
        registerGenerator(ERROR_OUTPUT, new ErrorOutputGenerator());
    }

}
