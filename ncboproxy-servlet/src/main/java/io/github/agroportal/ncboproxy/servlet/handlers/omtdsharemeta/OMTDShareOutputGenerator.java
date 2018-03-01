package io.github.agroportal.ncboproxy.servlet.handlers.omtdsharemeta;

import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.output.ProxyOutput;
import io.github.agroportal.ncboproxy.output.OutputGenerator;

import java.util.Map;
import java.util.Optional;

public class OMTDShareOutputGenerator implements OutputGenerator {
    @Override
    public ProxyOutput apply(final Optional<NCBOOutputModel> outputModel, final Map<String, String> outputParameters) {
        return null;
    }
}
