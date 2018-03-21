package io.github.agroportal.ncboproxy.handlers.omtdsharemeta;


import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.mapping.OMTDShareModelMapper;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.xsdmodel.*;
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
public class OMTDShareOutputGenerator implements OutputGenerator {

    private final ObjectFactory objectFactory;
    private final OMTDShareModelMapper modelMapper;

    OMTDShareOutputGenerator(final PortalType portalType) {
        objectFactory = new ObjectFactory();
        modelMapper = OMTDShareModelMapper.create(portalType, objectFactory);
    }


    @SuppressWarnings({"MethodParameterOfConcreteClass", "FeatureEnvy", "LocalVariableOfConcreteClass"})
    private void mapMetaData(final LexicalConceptualResourceInfoType lexicalConceptualResourceInfo, final NCBOOutputModel outputModel, final boolean downloadable, final String apiKey) {
//                final MetadataHeaderInfoType metadataHeaderInfo = objectFactory.createMetadataHeaderInfoType();
//                metadataRecord.setMetadataHeaderInfo(metadataHeaderInfo);

        modelMapper.rootResourceProperties(lexicalConceptualResourceInfo, outputModel);


        final LexicalConceptualResourceTextInfoType lexicalConceptualResourceTextInfoType = objectFactory.createLexicalConceptualResourceTextInfoType();
        modelMapper.textInformation(lexicalConceptualResourceTextInfoType, outputModel, downloadable);
        lexicalConceptualResourceInfo.setLexicalConceptualResourceTextInfo(lexicalConceptualResourceTextInfoType);


        //Setting identification information
        final IdentificationInfoType identificationInfo = objectFactory.createIdentificationInfoType();
        lexicalConceptualResourceInfo.setIdentificationInfo(identificationInfo);
        modelMapper.identificationInformation(identificationInfo, outputModel);

        //Version Information
        final VersionInfoType versionInfoType = objectFactory.createVersionInfoType();
        modelMapper.version(versionInfoType, outputModel);
        lexicalConceptualResourceInfo.setVersionInfo(versionInfoType);

        //Contact information
        final ContactInfoType contactInfoType = objectFactory.createContactInfoType();
        lexicalConceptualResourceInfo.setContactInfo(contactInfoType);
        modelMapper.contactInformation(contactInfoType, outputModel);

        //Resource creation information
        final ResourceCreationInfoType resourceCreationInfoType  = objectFactory.createResourceCreationInfoType();
        lexicalConceptualResourceInfo.setResourceCreationInfo(resourceCreationInfoType);
        modelMapper.resourceCreation(resourceCreationInfoType,outputModel);

        //Distribution information
        final LexicalConceptualResourceInfoType.DistributionInfos distributionInfos = objectFactory.createLexicalConceptualResourceInfoTypeDistributionInfos();
        modelMapper.distribution(distributionInfos, outputModel, downloadable, apiKey);
        lexicalConceptualResourceInfo.setDistributionInfos(distributionInfos);

        //Documentation information
        final LexicalConceptualResourceInfoType.ResourceDocumentations resourceDocumentations = objectFactory.createLexicalConceptualResourceInfoTypeResourceDocumentations();
        lexicalConceptualResourceInfo.setResourceDocumentations(resourceDocumentations);
        modelMapper.documentationInformation(resourceDocumentations,outputModel);

        //Rights information
        final RightsInfo rightsInfo = objectFactory.createRightsInfo();
        modelMapper.rights(rightsInfo, outputModel, downloadable);
        lexicalConceptualResourceInfo.setRightsInfo(rightsInfo);

        //Relations information
        final LexicalConceptualResourceInfoType.Relations relations = objectFactory.createLexicalConceptualResourceInfoTypeRelations();
        modelMapper.relations(relations,outputModel);
        lexicalConceptualResourceInfo.setRelations(relations);
    }

    @SuppressWarnings("LocalVariableOfConcreteClass")
    @Override
    public ProxyOutput apply(final Optional<NCBOOutputModel> outputModel, final Map<String, String> outputParameters) {
        ProxyOutput proxyOutput;
        if (outputModel.isPresent()) {
            final String acronym = OMTDShareModelMapper.getOntologyPropertyValue(outputModel.get(),"acronym");
            try {
                final JAXBContext jaxbContext = JAXBContext.newInstance("io.github.agroportal.ncboproxy.handlers.omtdsharemeta.xsdmodel");

                //Creating root element instance and initializing main complex types
                final LcrMetadataRecord metadataRecord = objectFactory.createLcrMetadataRecord();
                final LexicalConceptualResourceInfoType lexicalConceptualResourceInfo = generateLexicalConceptualResourceRoot(metadataRecord);

                mapMetaData(lexicalConceptualResourceInfo, outputModel.get(), outputParameters.containsKey(acronym),outputParameters.get("apikey"));

                proxyOutput = generateXMLOutput(jaxbContext, metadataRecord);

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
    private ProxyOutput generateXMLOutput(final JAXBContext jaxbContext, final LcrMetadataRecord metadataRecord) throws JAXBException {
        ProxyOutput proxyOutput;
        final Marshaller marshaller = jaxbContext.createMarshaller();
        try (StringWriter outputWriter = new StringWriter()) {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.meta-share.org/OMTD-SHARE_XMLSchema http://www.meta-share.org/OMTD-SHARE_XMLSchema/v302/OMTD-SHARE-LexicalConceptualResource.xsd");
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
            marshaller.marshal(metadataRecord, outputWriter);
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
    private LexicalConceptualResourceInfoType generateLexicalConceptualResourceRoot(final LcrMetadataRecord metadataRecord) {
        final LexicalConceptualResourceInfoType lexicalConceptualResourceInfoType =
                objectFactory.createLexicalConceptualResourceInfoType();
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
