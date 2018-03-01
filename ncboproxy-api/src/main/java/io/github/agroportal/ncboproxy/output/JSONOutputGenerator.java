package io.github.agroportal.ncboproxy.output;

import com.eclipsesource.json.WriterConfig;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;

import java.util.Map;
import java.util.Optional;

public class JSONOutputGenerator implements OutputGenerator {
    @Override
    public ProxyOutput apply(final Optional<NCBOOutputModel> outputModel, final Map<String, String> outputParameters) {
        return new NCBOProxyOutput(outputModel
                .orElse(NCBOOutputModel.annotatorError("Empty model received, report bug", ProxyOutput.HTTP_INTERNAL_APPLICATION_ERROR))
                .getModelRoot().toString(WriterConfig.PRETTY_PRINT), MimeTypes.APPLICATION_JSON);
    }
}
