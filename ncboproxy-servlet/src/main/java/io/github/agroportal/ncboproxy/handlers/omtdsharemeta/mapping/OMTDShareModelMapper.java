package io.github.agroportal.ncboproxy.handlers.omtdsharemeta.mapping;

import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.xsdmodel.*;
import io.github.agroportal.ncboproxy.model.JSONLDObject;
import io.github.agroportal.ncboproxy.model.NCBOCollection;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.servlet.PortalType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@SuppressWarnings({"MethodParameterOfConcreteClass", "FeatureEnvy"})
public interface OMTDShareModelMapper {

    String MISSING = "MISSING";
    String ONTOLOGY = "ontology";
    String DEFAULT_LANG = "en";

    /**
     * Sets the resource type as lexicalConceptualResource, given that all ontologies in bioportal are such
     *
     * @param lexicalConceptualResourceInfoType The lexical conceptual resource information complex type JAXB binding
     */
    default void rootResourceProperties(final LexicalConceptualResourceInfoType lexicalConceptualResourceInfoType, final NCBOOutputModel outputModel) {
        //Resource type is fixed as all resources in Bioportal are of this type
        lexicalConceptualResourceInfoType.setResourceType("lexicalConceptualResource");
        lexicalConceptualResourceInfoType.setLexicalConceptualResourceType("ontology");
    }

    void resourceCreation(ResourceCreationInfoType resourceCreationInfoType, NCBOOutputModel outputModel);

    void textInformation(final LexicalConceptualResourceTextInfoType lexicalConceptualResourceTextInfoType, final NCBOOutputModel outputModel, boolean downloadable);

    void relations(LexicalConceptualResourceInfoType.Relations relations, NCBOOutputModel outputModel);

    void identificationInformation(final IdentificationInfoType identificationInfo,
                                   final NCBOOutputModel outputModel);

    /**
     * Set the version of the resource based on the version property from the Bioportal JSONLD output. The resource revisions and revision
     * dates from the OMTDShare specification correspond to different concepts than submissions in bioportal and thus cannot be encoded here.
     *
     * @param versionInfoType The version info complex type JAXB binding
     * @param outputModel     The NCBO Output Model for the ontology submission
     */
    void version(final VersionInfoType versionInfoType, final NCBOOutputModel outputModel);

    /**
     * Set the contact information metadata setting the contactInfo complex type that contains
     * the contact point and the list of contact for the resource. In bioportal there is no way of differentiating between
     * contact persons and contact organizations, therefore, by default, all contacts are treated as contact persons.
     *
     * @param contactInfoType The contact information JAXB complex type
     * @param ncboOutputModel The NCBO Output Model for the ontology submission
     */
    void contactInformation(final ContactInfoType contactInfoType, final NCBOOutputModel ncboOutputModel);

    void distribution(LexicalConceptualResourceInfoType.DistributionInfos distributionInfos, NCBOOutputModel outputModel, boolean downloadable, String apikey);

    void documentationInformation(final LexicalConceptualResourceInfoType.ResourceDocumentations resourceDocumentations, final NCBOOutputModel outputModel);

    void rights(final RightsInfo rightsInfo, final NCBOOutputModel outputModel, boolean downloadable);

    static String getSubmissionPropertyValue(final NCBOOutputModel model, final String propertyName) {
        final Optional<JSONLDObject> optionalObject = model.asObject();
        return optionalObject.isPresent() ? optionalObject
                .get()
                .getStringValue(propertyName)
                .orElse(MISSING) : MISSING;
    }

    static Integer getSubmissionPropertyIntValue(final NCBOOutputModel model, final String propertyName) {
        final Optional<JSONLDObject> optionalObject = model.asObject();
        return optionalObject.isPresent() ? optionalObject
                .get()
                .getIntegerValue(propertyName)
                .orElse(-1) : -1;
    }

    static String getSubmissionPropertyValue(final NCBOOutputModel model, final String... propertyName) {
        final Optional<JSONLDObject> optionalObject = model.asObject();
        return optionalObject.isPresent() ? optionalObject
                .get()
                .getStringValue(propertyName)
                .orElse(MISSING) : MISSING;
    }

    static List<String> getSubmissionPropertyCollection(final NCBOOutputModel model, final String propertyName) {
        final List<String> finalList;
        final Optional<JSONLDObject> optionalObject = model.asObject();
        if (optionalObject.isPresent()) {
            final Optional<NCBOCollection> val = optionalObject
                    .get()
                    .getCollection(propertyName);
            finalList = val.isPresent() ? val
                    .get()
                    .asStringStream()
                    .collect(Collectors.toList()) : Collections.emptyList();
        } else {
            finalList = Collections.emptyList();
        }
        return finalList;
    }

    static List<String> getOntologyPropertyCollection(final NCBOOutputModel model, final String propertyName) {
        final List<String> finalList;
        final Optional<JSONLDObject> optionalObject = model.asObject();
        if (optionalObject.isPresent()) {
            final Optional<JSONLDObject> ontologyOptional = optionalObject
                    .get()
                    .getObject(ONTOLOGY);

            if (ontologyOptional.isPresent()) {
                final Optional<NCBOCollection> val = ontologyOptional
                        .get()
                        .getCollection(propertyName);
                finalList = val.isPresent() ? val
                        .get()
                        .asStringStream()
                        .collect(Collectors.toList()) : Collections.emptyList();
            } else {
                finalList = Collections.emptyList();
            }
        } else {
            finalList = Collections.emptyList();
        }

        return finalList;
    }

    static String getOntologyLinkValue(final NCBOOutputModel model, final String propertyName) {
        final Optional<JSONLDObject> optionalObject = model.asObject();
        final String result;
        if (optionalObject.isPresent()) {
            final JSONLDObject jsonldObject = optionalObject.get();
            final Optional<JSONLDObject> ontologyOptional = jsonldObject.getObject(ONTOLOGY);
            if (ontologyOptional.isPresent()) {
                final Optional<JSONLDObject> linksObject = ontologyOptional
                        .get()
                        .getObject("links");
                result = linksObject.isPresent() ? linksObject
                        .get()
                        .getStringValue(propertyName)
                        .orElse(MISSING) : MISSING;
            } else {
                result = MISSING;
            }
        } else {
            result = MISSING;
        }
        return result;
    }

    static String getOntologyPropertyValue(final NCBOOutputModel model, final String propertyName) {
        final Optional<JSONLDObject> optionalObject = model.asObject();
        final String result;
        if (optionalObject.isPresent()) {
            final JSONLDObject jsonldObject = optionalObject.get();
            final Optional<JSONLDObject> ontologyOptional = jsonldObject.getObject(ONTOLOGY);
            result = ontologyOptional.isPresent() ? ontologyOptional
                    .get()
                    .getStringValue(propertyName)
                    .orElse(MISSING) : MISSING;
        } else {
            result = MISSING;
        }
        return result;
    }


    static String getOntologyPropertyValue(final NCBOOutputModel model, final String... propertyName) {
        final Optional<JSONLDObject> optionalObject = model.asObject();
        final String result;
        if (optionalObject.isPresent()) {
            final JSONLDObject jsonldObject = optionalObject.get();
            final Optional<JSONLDObject> ontologyOptional = jsonldObject.getObject(ONTOLOGY);
            result = ontologyOptional.isPresent() ? ontologyOptional
                    .get()
                    .getStringValue(propertyName)
                    .orElse(MISSING) : MISSING;
        } else {
            result = MISSING;
        }
        return result;
    }

    @SuppressWarnings("MethodParameterOfConcreteClass")
    static OMTDShareModelMapper create(final PortalType portalType, final ObjectFactory objectFactory, final String apiKey) {
        OMTDShareModelMapper mapper = null;
        switch (portalType) {
            case AGROPORTAL:
                mapper = new AgroPortalModelMapper(objectFactory,apiKey, "en");
                break;
            case SIFR_BIOPORTAL:
                mapper = new AgroPortalModelMapper(objectFactory,apiKey, "fr");
                break;
            case STAGEPORTAL:
                mapper = new AgroPortalModelMapper(objectFactory,apiKey, "en");
                break;
            case NCBO_BIOPORTAL:
                mapper = new AgroPortalModelMapper(objectFactory,apiKey, "en");
                break;
            default:
                break;
        }
        return mapper;
    }
}
