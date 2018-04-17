package io.github.agroportal.ncboproxy.handlers.omtdsharemeta.mapping;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import eu.openminted.registry.domain.*;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.OmTDShareMultipleServletHandler;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils.OMTDLicenceMapper;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils.OMTDUtilityMapper;
import io.github.agroportal.ncboproxy.model.JSONLDObject;
import io.github.agroportal.ncboproxy.model.NCBOCollection;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.model.retrieval.BioportalRESTRequest;
import io.github.agroportal.ncboproxy.output.NCBOProxyOutput;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@SuppressWarnings({"MethodParameterOfConcreteClass", "InstanceVariableOfConcreteClass", "LocalVariableOfConcreteClass", "FeatureEnvy", "OverlyCoupledClass", "LawOfDemeter"})
public class AgroPortalModelMapper implements OMTDShareModelMapper {

    private static final Pattern LESSER_THAN = Pattern.compile("<");
    private static final Pattern GREATER_THAN = Pattern.compile(">");
    private static final Pattern VALID_EMAIL = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");

    private static final String DESCRIPTION_FIELD_NAME = "description";
    private static final String DOCUMENTATION_FIELD_NAME = "documentation";
    private static final String WAS_GENERATED_BY_FIELD_NAME = "wasGeneratedBy";
    private static final String IS_BACKWARDS_COMPATIBLE_WITH_FIELD_NAME = "isBackwardsCompatibleWith";
    private static final String SIMILAR_TO_FIELD_NAME = "similarTo";
    private static final String HAS_PART_FIELD_NAME = "hasPart";
    private static final String HAS_LICENCE_FIELD_NAME = "hasLicense";
    private static final String HAS_FORMALITY_LEVEL_FIELD_NAME = "hasFormalityLevel";
    private static final String PUBLICATION_FIELD_NAME = "publication";
    private static final String REFERENCE_FIELD_NAME = "reference";
    private static final String HAS_CREATOR_FIELD_NAME = "hasCreator";
    private static final String KEYWORD_FIELD_NAME = "keywords";
    private static final String HAS_DOMAIN_FIELD_NAME = "hasDomain";
    private static final String DOMAIN_OBJECT_NAME_FIELD_NAME = "name";
    private static final String NATURAL_LANGUAGE_FIELD_NAME = "naturalLanguage";
    private static final String HAS_ONTOLOGY_LANGUAGE_FIELD_NAME = "hasOntologyLanguage";
    private static final String NUMBER_OF_INDIVIDUALS_FIELD_NAME = "numberOfIndividuals";
    private static final String NUMBER_OF_CLASSES_FIELD_NAME = "numberOfClasses";
    private static final String NAME_FIELD_NAME = DOMAIN_OBJECT_NAME_FIELD_NAME;
    private static final String HTML_ESCAPED_LESSER_THAN = "&lt;";
    private static final String HTML_ESCAPED_GREATER_THAN = "&gt;";
    private static final String URI_FIELD_NAME = "URI";
    private static final String IDENTIFIER_FIELD_NAME = "identifier";
    private static final String OBJECT_ID_FIELD_NAME = "@id";
    private static final String VIEWING_RESTRICTION_FIELD_NAME = "viewingRestriction";
    private static final String VERSION_FIELD_NAME = "version";
    private static final String CONTACT_FIELD_NAME = "contact";
    private static final String PUBLISHER_FIELD_NAME = "publisher";
    private static final String HOMEPAGE_FIELD_NAME = "homepage";
    private static final String DOI_TRIGGER_VALUE = "doi";
    private static final String HTTP_TRIGGER_VALUE = "http";
    private static final String SKOS_TRIGGER_VALUE = "skos";
    private static final String EMAIL_FIELD_NAME = "email";

    private static final String OMTD_LEXICAL_CONCEPTUAL_RESOURCE_TEXT_INFO_MEDIA_TYPE_VALUE = "text";
    private static final String METADATA_LANGUAGE_CONSTANT = "en";

    private final ObjectFactory objectFactory;
    private final OMTDLicenceMapper licenseMapper;
    private final String apiKey;
    private final String portalLanguage;


    AgroPortalModelMapper(final ObjectFactory objectFactory, final String apiKey, final String portalLanguage) {
        this.objectFactory = objectFactory;
        licenseMapper = OMTDLicenceMapper.create();
        this.apiKey = apiKey;
        this.portalLanguage = portalLanguage;
    }


    @Override
    public void rootResourceProperties(final LexicalConceptualResourceInfo lexicalConceptualResourceInfo, final NCBOOutputModel outputModel) {
        lexicalConceptualResourceInfo.setResourceType(LEXICAL_CONCEPTUAL_RESOURCE_TYPE_VALUE);

        lexicalConceptualResourceInfo.setLexicalConceptualResourceType(
                OMTDUtilityMapper.mapFromFormalityLevel(
                        OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, HAS_FORMALITY_LEVEL_FIELD_NAME)
                )
        );
//        final ResourceDocumentationInfo resourceDocumentations = objectFactory.createResourceDocumentationInfo();
//        lexicalConceptualResourceInfo.setResourceDocumentations(resourceDocumentations);
    }

    @Override
    public void documentationInformation(final LexicalConceptualResourceInfo lexicalConceptualResourceInfo, final NCBOOutputModel outputModel) {
        final String reference = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, REFERENCE_FIELD_NAME);
        final String publication = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, PUBLICATION_FIELD_NAME);
        final String documentation = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, DOCUMENTATION_FIELD_NAME);


        final List<ResourceDocumentationInfo> resourceDocumentationInformation = new ArrayList<>();
        final ResourceDocumentationInfo resourceDocumentationInfoType = objectFactory.createResourceDocumentationInfo();
        if (!reference.equals(MISSING)) {
            resourceDocumentationInfoType.setDocumentationType(DocumentationTypeEnum.PUBLICATION);
            resourceDocumentationInfoType.setDocumentationDescription(reference);
            resourceDocumentationInformation
                    .add(resourceDocumentationInfoType);
        } else if (!publication.equals(MISSING)) {
            resourceDocumentationInfoType.setDocumentationType(DocumentationTypeEnum.PUBLICATION);
            final PublicationIdentifier publicationIdentifierType = objectFactory.createPublicationIdentifier();
            final List<PublicationIdentifier> publicationIdentifiers = new ArrayList<>();
            resourceDocumentationInfoType.setPublicationIdentifiers(publicationIdentifiers);
            publicationIdentifiers
                    .add(publicationIdentifierType);
            resourceDocumentationInfoType.setPublicationIdentifiers(publicationIdentifiers);
            resourceDocumentationInfoType.setDocumentationDescription(publication);

            if (publication
                    .toLowerCase()
                    .contains(DOI_TRIGGER_VALUE)) {
                publicationIdentifierType.setPublicationIdentifierSchemeName(PublicationIdentifierSchemeNameEnum.DOI);
                publicationIdentifierType.setSchemeURI(publication);
            } else if (publication.contains(HTTP_TRIGGER_VALUE)) {
                publicationIdentifierType.setPublicationIdentifierSchemeName(PublicationIdentifierSchemeNameEnum.URL);
                publicationIdentifierType.setSchemeURI(publication);
            } else {
                publicationIdentifierType.setPublicationIdentifierSchemeName(PublicationIdentifierSchemeNameEnum.OTHER);
                publicationIdentifierType.setValue(publication);
            }

        } else if (!documentation.equals(MISSING)) {
            resourceDocumentationInfoType.setDocumentationType(DocumentationTypeEnum.ON_LINE_HELP_URL);
            resourceDocumentationInfoType.setDocumentationDescription(documentation);
        }
        lexicalConceptualResourceInfo.setResourceDocumentations(resourceDocumentationInformation);
    }

    @Override
    public void resourceCreation(final ResourceCreationInfo resourceCreationInfo, final NCBOOutputModel outputModel) {

        final String creator = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, HAS_CREATOR_FIELD_NAME);

        if (!creator.equals(MISSING)) {
            final List<ActorInfo> resourceCreators = new ArrayList<>();
            resourceCreationInfo.setResourceCreators(resourceCreators);

            final ActorInfo actorInfoType = objectFactory.createActorInfo();
            resourceCreators.add(actorInfoType);
            actorInfoType.setActorType(ActorTypeEnum.PERSON);

            final PersonInfo personInfoType = objectFactory.createPersonInfo();
            actorInfoType.setRelatedPerson(personInfoType);
            personInfoType.setSurname(creator);
        }

    }

    @Override
    public void textInformation(final LexicalConceptualResourceTextInfo lexicalConceptualResourceTextInfo, final NCBOOutputModel outputModel, final boolean downloadable) {
        lexicalConceptualResourceTextInfo.setMediaType(OMTD_LEXICAL_CONCEPTUAL_RESOURCE_TEXT_INFO_MEDIA_TYPE_VALUE);

        //Handling languages
        final LingualityInfo lingualityInfoType = objectFactory.createLingualityInfo();
        lexicalConceptualResourceTextInfo.setLingualityInfo(lingualityInfoType);
        languages(lexicalConceptualResourceTextInfo, lingualityInfoType, outputModel);

        //Setting metadata language (always English)
        final LanguageInfo languageInfoType = objectFactory.createLanguageInfo();
        languageInfoType.setLanguage(METADATA_LANGUAGE_CONSTANT);
        lexicalConceptualResourceTextInfo.setMetalanguages(Collections.singletonList(languageInfoType));

        //Handling domains
        domains(lexicalConceptualResourceTextInfo, outputModel);

        final String keywordsValue = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, KEYWORD_FIELD_NAME);
        final String descriptionValue = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, DESCRIPTION_FIELD_NAME);

        if (!keywordsValue.equals(MISSING) || !descriptionValue.equals(MISSING)) {
            final List<String> keywordList;
            if (keywordsValue.equals(MISSING)) {
                keywordList = Collections.emptyList();
            } else {
                final String separator = keywordsValue.contains(",") ? "," : ";";
                keywordList = Arrays.asList(keywordsValue.split(separator));
            }
            if (!keywordList.isEmpty()) {
                lexicalConceptualResourceTextInfo.setKeywords(keywordList);
            }
        }

    }

    private void domains(final LexicalConceptualResourceTextInfo lexicalConceptualResourceTextInfo,
                         final NCBOOutputModel outputModel) {
        final List<String> domainList = OMTDShareModelMapper.getOntologyPropertyCollection(outputModel, HAS_DOMAIN_FIELD_NAME);

        final List<Domain> domains = new ArrayList<>();
        for (final String domainValue : domainList) {
            final Domain domain = objectFactory.createDomain();
            domains.add(domain);
            if (domainValue.contains(HTTP_TRIGGER_VALUE)) {
                final String URL = domainValue + "?apikey=" + apiKey;
                domain.setSchemeURI(URL);
                try {
                    final String output = BioportalRESTRequest.query(URL);
                    final JsonValue value = Json.parse(output);
                    if (value.isObject()) {
                        final JsonObject object = value.asObject();
                        final JsonValue nameVal = object.get(DOMAIN_OBJECT_NAME_FIELD_NAME);
                        if (nameVal.isString()) {
                            domain.setValue(nameVal.asString());
                        }
                    }
                } catch (final IOException ignored) {
                }
            } else {
                domain.setValue(domainValue);
            }
            domain.setClassificationSchemeName(ClassificationSchemeName.OTHER);
        }
        if (!domains.isEmpty()) {
            lexicalConceptualResourceTextInfo.setDomains(domains);
        }
    }


    private void languages(final LexicalConceptualResourceTextInfo lexicalConceptualResourceTextInfo,
                           final LingualityInfo lingualityInfo, final NCBOOutputModel outputModel) {
        final List<String> values = OMTDShareModelMapper.getSubmissionPropertyCollection(outputModel, NATURAL_LANGUAGE_FIELD_NAME);
        int count = 0;
        final List<LanguageInfo> languages = new ArrayList<>();
        for (final String value : values) {
            final LanguageInfo languageInfoType = objectFactory.createLanguageInfo();
            final String langValue = OMTDUtilityMapper.mapLanguage(value);
            if (!langValue.equals(OMTDUtilityMapper.UNKNOWN_LANGUAGE_VALUE)) {
                languageInfoType.setLanguage(langValue);
                languages.add(languageInfoType);
                count++;
            }
        }
        if (values.isEmpty()) {
            final LanguageInfo languageInfoType = objectFactory.createLanguageInfo();
            languageInfoType.setLanguage(DEFAULT_LANG);
            languages.add(languageInfoType);
        }
        if ((count > -1) && (count < 2)) {
            lingualityInfo.setLingualityType(LingualityTypeEnum.MONOLINGUAL);
        } else if (count == 2) {
            lingualityInfo.setLingualityType(LingualityTypeEnum.BILINGUAL);
        } else {
            lingualityInfo.setLingualityType(LingualityTypeEnum.MULTILINGUAL);
        }
        lexicalConceptualResourceTextInfo.setLanguages(languages);
    }

    private void dataFormat(final DataFormatInfo dataFormatInfo, final boolean downloadable) {
        if (downloadable) {
            dataFormatInfo.setDataFormat(DataFormatType.HTTP___W3ID_ORG_META_SHARE_OMTD_SHARE_RDF_XML);
        } else {
            dataFormatInfo.setDataFormat(DataFormatType.HTTP___W3ID_ORG_META_SHARE_OMTD_SHARE_JSON_LD);
        }
    }

    private void sizes(final SizeInfo sizeInfoType, final NCBOOutputModel outputModel) {

        final String language = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, HAS_ONTOLOGY_LANGUAGE_FIELD_NAME);

        final int size;
        if (!language.equals(MISSING) && language
                .toLowerCase()
                .contains(SKOS_TRIGGER_VALUE)) {
            size = OMTDShareModelMapper.getSubmissionPropertyIntValue(outputModel, NUMBER_OF_INDIVIDUALS_FIELD_NAME);
            sizeInfoType.setSizeUnit(SizeUnitEnum.CONCEPTS);
        } else {
            size = OMTDShareModelMapper.getSubmissionPropertyIntValue(outputModel, NUMBER_OF_CLASSES_FIELD_NAME);
            sizeInfoType.setSizeUnit(SizeUnitEnum.CLASSES);
        }
        sizeInfoType.setSize(String.valueOf(size));
    }
    @Override
    public void relations(final LexicalConceptualResourceInfo lexicalConceptualResourceInfos,
                          final NCBOOutputModel outputModel) {
        final List<String> wasGeneratedByList = OMTDShareModelMapper.getSubmissionPropertyCollection(outputModel, WAS_GENERATED_BY_FIELD_NAME);
        final List<String> isBackwardsCompatibleWithList = OMTDShareModelMapper.getSubmissionPropertyCollection(outputModel, IS_BACKWARDS_COMPATIBLE_WITH_FIELD_NAME);
        final List<String> similarToList = OMTDShareModelMapper.getSubmissionPropertyCollection(outputModel, SIMILAR_TO_FIELD_NAME);
        final List<String> hasPartList = OMTDShareModelMapper.getSubmissionPropertyCollection(outputModel, HAS_PART_FIELD_NAME);

        for (final String wasGeneratedBy : wasGeneratedByList) {
            final RelationInfo relationInfo = objectFactory.createRelationInfo();
            relationInfo.setRelationType(RelationTypeEnum.IS_CREATED_BY);
            addRelatedResource(wasGeneratedBy, relationInfo);
        }
        for (final String isBackwardsCompatibleWith : isBackwardsCompatibleWithList) {
            final RelationInfo relationInfo = objectFactory.createRelationInfo();
            relationInfo.setRelationType(RelationTypeEnum.IS_COMPATIBLE_WITH);
            addRelatedResource(isBackwardsCompatibleWith, relationInfo);
        }
        for (final String similarTo : similarToList) {
            final RelationInfo relationInfo = objectFactory.createRelationInfo();
            relationInfo.setRelationType(RelationTypeEnum.IS_SIMILAR_TO);
            addRelatedResource(similarTo, relationInfo);
        }
        for (final String hasPart : hasPartList) {
            final RelationInfo relationInfo = objectFactory.createRelationInfo();
            relationInfo.setRelationType(RelationTypeEnum.HAS_PART);
            addRelatedResource(hasPart, relationInfo);
        }
    }

    private void addRelatedResource(final String name, final RelationInfo relationInfo) {
        final RelatedResource relatedResource = objectFactory.createRelatedResource();
        final List<ResourceName> resourceNames = new ArrayList<>();
        relatedResource.setResourceNames(resourceNames);
        final ResourceName resourceName = objectFactory.createResourceName();
        resourceName.setValue(name);
        resourceNames.add(resourceName);
        relationInfo.setRelatedResource(relatedResource);
    }

    @Override
    public void identificationInformation(final IdentificationInfo identificationInfo,
                                          final NCBOOutputModel outputModel) {

        final List<ResourceName> resourceNames = new ArrayList<>();
        final ResourceName resourceName = objectFactory.createResourceName();
        resourceName(identificationInfo, resourceNames, resourceName, outputModel);

        final Description description = objectFactory.createDescription();
        resourceDescription(description, outputModel);
        identificationInfo.setDescriptions(Collections.singletonList(description));

        final List<ResourceIdentifier> identifiers = new ArrayList<>();
        resourceIdentifier(identifiers, outputModel);
        identificationInfo.setResourceIdentifiers(identifiers);

        publicTag(identificationInfo, outputModel);

    }

    private void resourceName(final IdentificationInfo identificationInfo,
                              final List<ResourceName> resourceNames,
                              final ResourceName resourceName, final NCBOOutputModel outputModel) {

        final String acronym = OMTDShareModelMapper.getOntologyPropertyValue(outputModel, OmTDShareMultipleServletHandler.ACRONYM_FIELD_VALUE);
        final String name = OMTDShareModelMapper.getOntologyPropertyValue(outputModel, NAME_FIELD_NAME);

        //Acronym as a short name
        identificationInfo.setResourceShortName(acronym);

        resourceName.setLang(portalLanguage);
        if (name.equals(MISSING)) {
            resourceName.setValue(acronym);
        } else {
            resourceName.setValue(name);
        }

        resourceNames.add(resourceName);

        identificationInfo.setResourceNames(resourceNames);
    }

    private void resourceDescription(final Description description,
                                     final NCBOOutputModel outputModel) {
        final String descriptionValue = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, DESCRIPTION_FIELD_NAME);
        description.setLang(DEFAULT_LANG);
        if (descriptionValue.equals(MISSING)) {
            description.setValue("");
        } else {
            description.setValue(GREATER_THAN
                    .matcher(LESSER_THAN
                            .matcher(descriptionValue)
                            .replaceAll(HTML_ESCAPED_LESSER_THAN))
                    .replaceAll(HTML_ESCAPED_GREATER_THAN));
        }
    }

    private void resourceIdentifier(final Collection<ResourceIdentifier> resourceIdentifiers,
                                    final NCBOOutputModel outputModel) {
        final String uriValue = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, URI_FIELD_NAME);
        final String identifierValue = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, IDENTIFIER_FIELD_NAME);
        final String id = OMTDShareModelMapper.getOntologyPropertyValue(outputModel, OBJECT_ID_FIELD_NAME);

        resourceIdentifiers
                .add(
                        resourceId(uriValue)
                                .orElse(resourceId(identifierValue)
                                        .orElse(resourceId(id)
                                                .orElse(emptyId())))
                );
    }

    private ResourceIdentifier emptyId() {
        return objectFactory.createResourceIdentifier();
    }

    private Optional<ResourceIdentifier> resourceId(final String identifierValue) {
        final Optional<ResourceIdentifier> result;
        if (!identifierValue.equals(MISSING) && !identifierValue.isEmpty()) {
            final ResourceIdentifier resourceIdentifier = emptyId();
            resourceIdentifier.setResourceIdentifierSchemeName(ResourceIdentifierSchemeNameEnum.URL);
            resourceIdentifier.setValue(identifierValue);
            result = Optional.of(resourceIdentifier);
        } else {
            result = Optional.empty();
        }
        return result;
    }


    private void publicTag(final IdentificationInfo identificationInfo, final NCBOOutputModel outputModel) {
        identificationInfo.setPublic(
                OMTDShareModelMapper
                        .getOntologyPropertyValue(outputModel, VIEWING_RESTRICTION_FIELD_NAME)
                        .equals(NCBOProxyOutput.PUBLIC_VIEWING_RESTRICTIONS_VALUE));
    }

    @Override
    public void version(final VersionInfo versionInfo, final NCBOOutputModel outputModel) {
        final String version = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, VERSION_FIELD_NAME);
        versionInfo.setVersion((version.equals(MISSING) ? "No Versioning" : version));
    }


    /**
     * Set the contact information metadata setting the contactInfo complex type that contains
     * the contact point and the list of contact for the resource. In bioportal there is no way of differentiating between
     * contact persons and contact organizations, therefore, by default, all contacts are treated as contact persons.
     *
     * @param contactInfo     The contact information JAXB complex type
     * @param ncboOutputModel The NCBO Output Model for the ontology submission
     */
    @Override
    @SuppressWarnings("FeatureEnvy")
    public void contactInformation(final ContactInfo contactInfo, final NCBOOutputModel ncboOutputModel) {
        final String contactPoint = OMTDShareModelMapper.getSubmissionPropertyValue(ncboOutputModel, HOMEPAGE_FIELD_NAME, DOCUMENTATION_FIELD_NAME);
        if (!contactPoint.equals(MISSING)) {
            contactInfo.setContactPoint(contactPoint);
            contactInfo.setContactType(ContactTypeEnum.LANDING_PAGE);
        }

        final Optional<JSONLDObject> rootObject = ncboOutputModel.asObject();
        if (rootObject.isPresent()) {
            final Optional<NCBOCollection> contactCollection = rootObject
                    .get()
                    .getCollection(CONTACT_FIELD_NAME);
            contactCollection.ifPresent(new ContactConsumer(contactPoint, contactInfo));

            final String publisher = OMTDShareModelMapper.getSubmissionPropertyValue(ncboOutputModel, PUBLISHER_FIELD_NAME);
            if (!publisher.equals(MISSING)) {
                final GroupInfo groupInfo = objectFactory.createGroupInfo();
                contactInfo.setContactGroups(Collections.singletonList(groupInfo));

                final GroupName groupName = objectFactory.createGroupName();
                groupInfo.setGroupNames(Collections.singletonList(groupName));
                groupName.setValue(publisher);
                groupName.setLang(DEFAULT_LANG);
            }
        }
    }

    private class ContactConsumer implements Consumer<NCBOCollection> {
        private final String contactPoint;
        private final ContactInfo contactInfo;
        private final List<PersonInfo> personInfos;

        ContactConsumer(final String contactPoint, final ContactInfo contactInfo) {
            this.contactPoint = contactPoint;
            this.contactInfo = contactInfo;
            personInfos = new ArrayList<>();
        }

        @Override
        public void accept(final NCBOCollection ncboOutputModels) {
            for (final NCBOOutputModel outputModel : ncboOutputModels) {
                final Optional<JSONLDObject> asObject = outputModel.asObject();
                if (asObject.isPresent()) {
                    final JSONLDObject contact = asObject.get();
                    final String email = contact
                            .getStringValue(EMAIL_FIELD_NAME)
                            .orElse(MISSING);
                    final String name = contact
                            .getStringValue(NAME_FIELD_NAME)
                            .orElse(MISSING);
                    if (contactPoint.equals(MISSING) && VALID_EMAIL
                            .matcher(email)
                            .matches()) {
                        contactInfo.setContactPoint(email);
                        contactInfo.setContactType(ContactTypeEnum.CONTACT_EMAIL);
                    }
                    if (!email.isEmpty() && !name.isEmpty() && VALID_EMAIL
                            .matcher(email)
                            .matches()) {
                        final PersonInfo personInfo = objectFactory.createPersonInfo();
                        personInfo.setSurname(name);
                        final CommunicationInfo communicationInfo = objectFactory.createCommunicationInfo();
                        communicationInfo.setEmails(Collections.singletonList(email));

                        personInfo.setCommunicationInfo(communicationInfo);
                    }
                }
            }
            if (!personInfos.isEmpty()) {
                contactInfo.setContactPersons(personInfos);
            }
        }
    }

    @Override
    public void distribution(final LexicalConceptualResourceInfo lexicalConceptualResourceInfo,
                             final NCBOOutputModel outputModel, final boolean downloadable, final String apikey) {
        final DatasetDistributionInfo distributionInfoType = objectFactory.createDatasetDistributionInfo();
        lexicalConceptualResourceInfo.setDistributionInfos(Collections.singletonList(distributionInfoType));

        if (downloadable) {
            final String downloadLocation = OMTDShareModelMapper.getOntologyLinkValue(outputModel, OmTDShareMultipleServletHandler.DOWNLOAD_FIELD_VALUE);
            distributionInfoType.setDistributionLocation(downloadLocation + "?format=rdf&apikey=" + apikey);
            distributionInfoType.setDistributionMedium(DistributionMediumEnum.DOWNLOADABLE);
        } else {
            final String restEndPoint = OMTDShareModelMapper.getOntologyPropertyValue(outputModel, OBJECT_ID_FIELD_NAME);
            if (!restEndPoint.equals(MISSING)) {
                distributionInfoType.setDistributionLocation(restEndPoint);
                distributionInfoType.setDistributionMedium(DistributionMediumEnum.ACCESSIBLE_THROUGH_INTERFACE);
            }
        }

        //Handling resource sizes

        final SizeInfo sizeInfoType = objectFactory.createSizeInfo();
        distributionInfoType.setSizes(Collections.singletonList(sizeInfoType));
        sizes(sizeInfoType, outputModel);

        final TextFormatInfo textFormatInfo = objectFactory.createTextFormatInfo();
        distributionInfoType.setTextFormats(Collections.singletonList(textFormatInfo));

        final DataFormatInfo dataFormatInfo = objectFactory.createDataFormatInfo();
        textFormatInfo.setDataFormatInfo(dataFormatInfo);
        dataFormat(dataFormatInfo, downloadable);
    }

    @Override
    public void rights(final RightsInfo rightsInfo, final NCBOOutputModel outputModel, final boolean downloadable) {

        final LicenceInfo licenceInfo = objectFactory.createLicenceInfo();
        rightsInfo.setLicenceInfos(Collections.singletonList(licenceInfo));

        final String portalLicense = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, HAS_LICENCE_FIELD_NAME);
        final Optional<LicenceEnum> licenseAcronym = licenseMapper.findLicense(portalLicense);
        if (licenseAcronym.isPresent()) {
            licenceInfo.setLicence(licenseAcronym.get());
        } else if ((portalLicense != null) && !portalLicense.equals(MISSING)) {
            if (OMTDLicenceMapper.HTTPS_URL_PATTERN
                    .matcher(portalLicense)
                    .matches()) {
                licenceInfo.setNonStandardLicenceTermsURL(portalLicense);
            } else {
                licenceInfo.setNonStandardLicenceName(portalLicense);
            }

            licenceInfo.setLicence(LicenceEnum.NON_STANDARD_LICENCE_TERMS);
        } else {
            licenceInfo.setLicence(LicenceEnum.OPEN_ACCESS_UNSPECIFIED);
        }
        if (downloadable) {
            rightsInfo.setRightsStatement(RightsStatementEnum.OPEN_ACCESS);
        } else {
            rightsInfo.setRightsStatement(RightsStatementEnum.RESTRICTED_ACCESS);
        }
    }

}
