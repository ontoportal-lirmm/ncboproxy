package io.github.agroportal.ncboproxy.handlers.omtdsharemeta;


import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import eu.openminted.registry.domain.*;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.mapping.OMTDShareModelMapper;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.output.OutputGenerator;
import io.github.agroportal.ncboproxy.output.ProxyOutput;
import io.github.agroportal.ncboproxy.servlet.PortalType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

/**
 * <p>Generate an XML output compatible with the OMTDShare metadata interchange format, see below for an illustration (in ascii)
 * of the structure of the XML document. We are using JAXB to bind the XSD specifications to Java classes, one only needs to instantiate the right objects
 * and to use the setters to assign them to the appropriate parents. Please consult OMTD-SHARE-LexicalConceptualResource.xsd for full details
 * about the full specification.</p>
 * <p>Please consult omtdshareschema/examples in the root project (ncboproxy) for a few complete examples
 * of metadata for several ontologies</p>
 */

@SuppressWarnings({"InstanceVariableOfConcreteClass", "OverlyCoupledClass", "OverlyCoupledMethod"})
public class OMTDShareOutputGenerator implements OutputGenerator {

    private final ObjectFactory objectFactory;
    private final PortalType portalType;

    OMTDShareOutputGenerator(final PortalType portalType) {
        objectFactory = new ObjectFactory();
        this.portalType = portalType;
    }


    @SuppressWarnings({"MethodParameterOfConcreteClass", "FeatureEnvy", "LocalVariableOfConcreteClass", "LawOfDemeter"})
    private void mapMetaData(final LexicalConceptualResourceInfo lexicalConceptualResourceInfo, final NCBOOutputModel outputModel, final boolean downloadable, final String apiKey) {
//                final MetadataHeaderInfoType metadataHeaderInfo = objectFactory.createMetadataHeaderInfoType();
//                metadataRecord.setMetadataHeaderInfo(metadataHeaderInfo);

        final OMTDShareModelMapper modelMapper = OMTDShareModelMapper.create(portalType, objectFactory, apiKey);

        modelMapper.rootResourceProperties(lexicalConceptualResourceInfo, outputModel);


        final LexicalConceptualResourceTextInfo lexicalConceptualResourceTextInfo = objectFactory.createLexicalConceptualResourceTextInfo();
        modelMapper.textInformation(lexicalConceptualResourceTextInfo, outputModel, downloadable);
        lexicalConceptualResourceInfo.setLexicalConceptualResourceTextInfo(lexicalConceptualResourceTextInfo);


        //Setting identification information
        final IdentificationInfo identificationInfo = objectFactory.createIdentificationInfo();
        lexicalConceptualResourceInfo.setIdentificationInfo(identificationInfo);
        modelMapper.identificationInformation(identificationInfo, outputModel);

        //Version Information
        final VersionInfo versionInfoType = objectFactory.createVersionInfo();
        modelMapper.version(versionInfoType, outputModel);
        lexicalConceptualResourceInfo.setVersionInfo(versionInfoType);

        //Contact information
        final ContactInfo contactInfoType = objectFactory.createContactInfo();
        lexicalConceptualResourceInfo.setContactInfo(contactInfoType);
        modelMapper.contactInformation(contactInfoType, outputModel);

        //Resource creation information
        final ResourceCreationInfo resourceCreationInfo = objectFactory.createResourceCreationInfo();
        lexicalConceptualResourceInfo.setResourceCreationInfo(resourceCreationInfo);
        modelMapper.resourceCreation(resourceCreationInfo, outputModel);

        //Distribution information
        modelMapper.distribution(lexicalConceptualResourceInfo, outputModel, downloadable, apiKey);

        //Documentation information
        modelMapper.documentationInformation(lexicalConceptualResourceInfo, outputModel);

        //Rights information
        final RightsInfo rightsInfo = objectFactory.createRightsInfo();
        modelMapper.rights(rightsInfo, outputModel, downloadable);
        lexicalConceptualResourceInfo.setRightsInfo(rightsInfo);

        //Relations information
        modelMapper.relations(lexicalConceptualResourceInfo, outputModel);
    }

    @SuppressWarnings("LocalVariableOfConcreteClass")
    @Override
    public ProxyOutput apply(final Optional<NCBOOutputModel> outputModel, final Map<String, String> outputParameters) {
        ProxyOutput proxyOutput;
        if (outputModel.isPresent()) {
            final String acronym = OMTDShareModelMapper.getOntologyPropertyValue(outputModel.get(), OmTDShareMultipleServletHandler.ACRONYM_FIELD_VALUE);
            try {
                final JAXBContext jaxbContext = JAXBContext.newInstance("eu.openminted.registry.domain");

                //Creating root element instance and initializing main complex types
                final Lexical lexicalMetaData = objectFactory.createLexical();
                final LexicalConceptualResourceInfo lexicalConceptualResourceInfo = generateLexicalConceptualResourceRoot(lexicalMetaData);

                mapMetaData(lexicalConceptualResourceInfo, outputModel.get(), outputParameters.containsKey(acronym), outputParameters.get("apikey"));

                proxyOutput = generateXMLOutput(jaxbContext, lexicalMetaData);

            } catch (final PropertyException e) {
                proxyOutput = OutputGenerator.errorOutput(
                        MessageFormat.format("OMTDShare Output: Invalid property: {0}", e.toString()));
            } catch (final JAXBException e) {
                proxyOutput = OutputGenerator.errorOutput(
                        MessageFormat.format("OMTDShare Output: Cannot instantiate OMTDShare JAXB Model: {0}", e.toString()));
            }
        } else {
            proxyOutput = OutputGenerator.errorOutput("OMTDShare Output: The response received from the REST API is Empty");
        }
        return proxyOutput;
    }

    @SuppressWarnings("MethodParameterOfConcreteClass")
    private ProxyOutput generateXMLOutput(final JAXBContext jaxbContext, final Lexical lexicalMetaData) throws JAXBException {
        ProxyOutput proxyOutput;
        final Marshaller marshaller = jaxbContext.createMarshaller();
        try (StringWriter outputWriter = new StringWriter()) {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.meta-share.org/OMTD-SHARE_XMLSchema http://www.meta-share.org/OMTD-SHARE_XMLSchema/v302/OMTD-SHARE-LexicalConceptualResource.xsd");
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
            marshaller.marshal(lexicalMetaData, outputWriter);
            proxyOutput = ProxyOutput.create(outputWriter.toString(), "text/xml");
        } catch (final IOException e) {
            proxyOutput = OutputGenerator.errorOutput(
                    MessageFormat.format("OMTDShare Output: Cannot generate XML output: {0}", e.getMessage()));
        }
        return proxyOutput;
    }


    /**
     * Create the main Lexical-Conceptual Resource Info container and register it in the metadata record.
     * Fixed-value elements should be set here.
     *
     * @param metadataRecord The metadata record object (root element of the document)
     * @return Returns the new LexicalConceptualResourceInfoType instance and sets it into the metadata record.
     */
    @SuppressWarnings({"LawOfDemeter", "MethodReturnOfConcreteClass", "LocalVariableOfConcreteClass", "MethodParameterOfConcreteClass"})
    private LexicalConceptualResourceInfo generateLexicalConceptualResourceRoot(final Lexical metadataRecord) {
        final LexicalConceptualResourceInfo lexicalConceptualResourceInfoType =
                objectFactory.createLexicalConceptualResourceInfo();
        metadataRecord.setLexicalConceptualResourceInfo(lexicalConceptualResourceInfoType);
        return lexicalConceptualResourceInfoType;
    }

    private final NamespacePrefixMapper mapper = new OMTDSharePrefixMapper();

    private static class OMTDSharePrefixMapper extends NamespacePrefixMapper {
        @Override
        public String getPreferredPrefix(final String namespaceUri, final String suggestion, final boolean requirePrefix) {
            return "ms";
        }

    }
}
