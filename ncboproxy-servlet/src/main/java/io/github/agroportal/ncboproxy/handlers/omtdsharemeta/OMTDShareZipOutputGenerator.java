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
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p></>Generate an XML output compatible with the OMTDShare metadata interchange format, see below for an illustration (in ascii)
 * of the structure of the XML document. We are using JAXB to bind the XSD specifications to Java classes, one only needs to instantiate the right objects
 * and to use the setters to assign them to the appropriate parents. Please consult OMTD-SHARE-LexicalConceptualResource.xsd for full details
 * about the full specification.</p>
 * <p>Please consult omtdshareschema/examples in the root project (ncboproxy) for a few complete examples
 * of metadata for several ontologies</p>
 * <ul>
 * Top-level tree structure of the XML document to produce:
 * [ROOT] lcrMetadataRecord
 * <li>metadataHeaderInfo
 * <ul>
 * <li>metadataRecordIdentifier</li>
 * <li>metadataCreationDate</li>
 * <li>metadataCreators</li>
 * <li></li>
 * </ul>
 * <p>
 * </li>
 * <li>lexicalConceptualResourceInfo</li>
 * <ul>
 * <li></li>
 * </ul>
 * </ul>
 */

@SuppressWarnings({"InstanceVariableOfConcreteClass", "OverlyCoupledClass", "OverlyCoupledMethod"})
public class OMTDShareZipOutputGenerator implements OutputGenerator {

    private final OMTDShareOutputGenerator omtdShareOutputGenerator;

    OMTDShareZipOutputGenerator(final PortalType portalType) {
        omtdShareOutputGenerator = new OMTDShareOutputGenerator(portalType);
    }


    @SuppressWarnings("LawOfDemeter")
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
                        zipOutputStream.putNextEntry(new ZipEntry(acronym+ ".xml"));
                        zipOutputStream.write(output.getStringContent().getBytes());
                        zipOutputStream.closeEntry();
                    }
                    zipOutputStream.flush();
                }
                byteArrayOutputStream.flush();
                proxyOutput = ProxyOutput.create(byteArrayOutputStream.toByteArray(),"application/zip");
            } catch (final IOException e) {
                proxyOutput = OutputGenerator.errorOutput("OMTDShare Output: Failed to initialize zip generator - " + e.getMessage());
            }

        } else {
            proxyOutput = OutputGenerator.errorOutput("OMTDShare Output: The response received from the REST API is Empty");
        }
        return proxyOutput;
    }
}
