package io.github.agroportal.ncboproxy.handlers.omtdsharemeta;


import com.eclipsesource.json.JsonArray;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.mapping.OMTDShareModelMapper;
import io.github.agroportal.ncboproxy.model.NCBOCollection;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.output.OutputGenerator;
import io.github.agroportal.ncboproxy.output.ProxyOutput;
import io.github.agroportal.ncboproxy.servlet.PortalType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>Generate an aggregate XML output compatible with the OMTDShare metadata interchange format for a collection of ontology submissions</p>
 */

@SuppressWarnings({"InstanceVariableOfConcreteClass", "OverlyCoupledClass", "OverlyCoupledMethod"})
public class OMTDShareZipOutputGenerator implements OutputGenerator {

    private final OMTDShareOutputGenerator omtdShareOutputGenerator;

    OMTDShareZipOutputGenerator(final PortalType portalType) {
        omtdShareOutputGenerator = new OMTDShareOutputGenerator(portalType);
    }


    @SuppressWarnings({"LawOfDemeter", "FeatureEnvy"})
    @Override
    public ProxyOutput apply(final Optional<NCBOOutputModel> outputModel, final Map<String, String> outputParameters) {
        ProxyOutput proxyOutput;
        if (outputModel.isPresent()) {
            final NCBOCollection ncboCollection = outputModel
                    .get()
                    .asCollection()
                    .orElse(NCBOCollection.create(new JsonArray()));
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
                    for (final NCBOOutputModel childModel : ncboCollection) {
                        final ProxyOutput output = omtdShareOutputGenerator.apply(Optional.of(childModel), outputParameters);
                        final String acronym = OMTDShareModelMapper.getOntologyPropertyValue(childModel, "acronym");
                        zipOutputStream.putNextEntry(new ZipEntry(acronym + ".xml"));
                        zipOutputStream.write(output
                                .getStringContent()
                                .getBytes());
                        zipOutputStream.closeEntry();
                    }
                    zipOutputStream.flush();
                }
                byteArrayOutputStream.flush();

                final DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy-HH_mm");
                final String currentDateString = dateFormat.format(new Date());

                proxyOutput =
                        ProxyOutput.create(byteArrayOutputStream.toByteArray(), "application/zip")
                                   .makeFileTransfer(String.format("ontologies_omtd-share_metadata-%s.zip",currentDateString));
            } catch (final IOException e) {
                proxyOutput = OutputGenerator.errorOutput("OMTDShare Output: Failed to initialize zip generator - " + e.getMessage());
            }

        } else {
            proxyOutput = OutputGenerator.errorOutput("OMTDShare Output: The response received from the REST API is Empty");
        }
        return proxyOutput;
    }

}
