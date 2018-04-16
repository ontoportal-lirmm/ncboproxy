package io.github.agroportal.ncboproxy.handlers.omtdsharemeta.mapping;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils.KeyWordExtractor;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils.OMTDLicenceMapper;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils.OMTDUtilityMapper;
import io.github.agroportal.ncboproxy.handlers.omtdsharemeta.xsdmodel.*;
import io.github.agroportal.ncboproxy.model.JSONLDObject;
import io.github.agroportal.ncboproxy.model.NCBOCollection;
import io.github.agroportal.ncboproxy.model.NCBOOutputModel;
import io.github.agroportal.ncboproxy.model.retrieval.BioportalRESTRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@SuppressWarnings({"MethodParameterOfConcreteClass", "InstanceVariableOfConcreteClass", "LocalVariableOfConcreteClass", "FeatureEnvy", "OverlyCoupledClass", "LawOfDemeter"})
public class AgroPortalModelMapper implements OMTDShareModelMapper {

    private static final Pattern LESSER_THAN = Pattern.compile("<");
    private static final Pattern GREATER_THAN = Pattern.compile(">");
    //    private static final Pattern TEST_ENDPOINT_ENDING = Pattern.compile("^(.*)/test$");
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
    public void rootResourceProperties(final LexicalConceptualResourceInfoType lexicalConceptualResourceInfoType, final NCBOOutputModel outputModel) {
        lexicalConceptualResourceInfoType.setResourceType("lexicalConceptualResource");

        lexicalConceptualResourceInfoType.setLexicalConceptualResourceType(
                OMTDUtilityMapper.mapFromFormalityLevel(
                        OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "hasFormalityLevel")
                )
        );
        final LexicalConceptualResourceInfoType.ResourceDocumentations resourceDocumentations = objectFactory.createLexicalConceptualResourceInfoTypeResourceDocumentations();
        lexicalConceptualResourceInfoType.setResourceDocumentations(resourceDocumentations);
    }

    @Override
    public void documentationInformation(final LexicalConceptualResourceInfoType.ResourceDocumentations resourceDocumentations, final NCBOOutputModel outputModel) {
        final String reference = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "reference");
        final String publication = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "publication");
        final String documentation = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "documentation");


        final ResourceDocumentationInfoType resourceDocumentationInfoType = objectFactory.createResourceDocumentationInfoType();
        if (!reference.equals(MISSING)) {
            resourceDocumentationInfoType.setDocumentationType("publication");
            resourceDocumentationInfoType.setDocumentationDescription(reference);
            resourceDocumentations
                    .getResourceDocumentationInfo()
                    .add(resourceDocumentationInfoType);
        } else if (!publication.equals(MISSING)) {
            resourceDocumentationInfoType.setDocumentationType("publication");
            final ResourceDocumentationInfoType.PublicationIdentifiers publicationIdentifiers = objectFactory.createResourceDocumentationInfoTypePublicationIdentifiers();
            final PublicationIdentifierType publicationIdentifierType = objectFactory.createPublicationIdentifierType();
            publicationIdentifiers
                    .getPublicationIdentifier()
                    .add(publicationIdentifierType);
            resourceDocumentationInfoType.setPublicationIdentifiers(publicationIdentifiers);

            resourceDocumentationInfoType.setDocumentationDescription(publication);

            if (publication
                    .toLowerCase()
                    .contains("doi")) {
                publicationIdentifierType.setPublicationIdentifierSchemeName("DOI");
                publicationIdentifierType.setSchemeURI(publication);
            } else if (publication.contains("http")) {
                publicationIdentifierType.setPublicationIdentifierSchemeName("URL");
                publicationIdentifierType.setSchemeURI(publication);
            } else {
                publicationIdentifierType.setPublicationIdentifierSchemeName("other");
                publicationIdentifierType.setValue(publication);
            }
            resourceDocumentations
                    .getResourceDocumentationInfo()
                    .add(resourceDocumentationInfoType);
        } else if (!documentation.equals(MISSING)) {
            resourceDocumentationInfoType.setDocumentationType("onLineHelpURL");
            resourceDocumentationInfoType.setDocumentationDescription(documentation);
            resourceDocumentations
                    .getResourceDocumentationInfo()
                    .add(resourceDocumentationInfoType);
        }
    }

    @Override
    public void resourceCreation(final ResourceCreationInfoType resourceCreationInfoType, final NCBOOutputModel outputModel) {

        final String creator = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "hasCreator");

        if (!creator.equals(MISSING)) {
            final ResourceCreationInfoType.ResourceCreators resourceCreators = objectFactory.createResourceCreationInfoTypeResourceCreators();
            resourceCreationInfoType.setResourceCreators(resourceCreators);

            final ActorInfoType actorInfoType = objectFactory.createActorInfoType();
            resourceCreators
                    .getResourceCreator()
                    .add(actorInfoType);
            actorInfoType.setActorType("person");

            final PersonInfoType personInfoType = objectFactory.createPersonInfoType();
            actorInfoType.setRelatedPerson(personInfoType);
            personInfoType.setSurname(creator);
        }

    }

    @SuppressWarnings({"OverlyCoupledMethod", "MethodWithMoreThanThreeNegations"})
    @Override
    public void textInformation(final LexicalConceptualResourceTextInfoType lexicalConceptualResourceTextInfoType, final NCBOOutputModel outputModel, final boolean downloadable) {

        lexicalConceptualResourceTextInfoType.setMediaType("text");

        //Handling data format properties
//        final DataFormatInfo dataFormatInfo = objectFactory.createDataFormatInfo();
//        lexicalConceptualResourceTextInfoType.setDataFormatInfo(dataFormatInfo);
//        dataFormat(dataFormatInfo, downloadable);

        //Handling languages
        final LexicalConceptualResourceTextInfoType.Languages languages = objectFactory.createLexicalConceptualResourceTextInfoTypeLanguages();
        lexicalConceptualResourceTextInfoType.setLanguages(languages);
        final LingualityInfoType lingualityInfoType = objectFactory.createLingualityInfoType();
        lexicalConceptualResourceTextInfoType.setLingualityInfo(lingualityInfoType);
        languages(languages, lingualityInfoType, outputModel);

        //Setting metadata language, always "eng"
        final LexicalConceptualResourceTextInfoType.Metalanguages metalanguages = objectFactory.createLexicalConceptualResourceTextInfoTypeMetalanguages();
        lexicalConceptualResourceTextInfoType.setMetalanguages(metalanguages);
        final LanguageInfoType languageInfoType = objectFactory.createLanguageInfoType();
        metalanguages
                .getMetalanguageInfo()
                .add(languageInfoType);
        languageInfoType.setLanguage("en");

        //Handling resource sizes
        final LexicalConceptualResourceTextInfoType.Sizes sizes = objectFactory.createLexicalConceptualResourceTextInfoTypeSizes();

        //Handling domains
        final LexicalConceptualResourceTextInfoType.Domains domains = objectFactory.createLexicalConceptualResourceTextInfoTypeDomains();
        domains(domains, outputModel);
        if (!domains
                .getDomain()
                .isEmpty()) {
            lexicalConceptualResourceTextInfoType.setDomains(domains);
        }

        final String keywordsValue = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "keywords");
        final String descriptionValue = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "description");

        if (!keywordsValue.equals(MISSING) || !descriptionValue.equals(MISSING)) {
            final LexicalConceptualResourceTextInfoType.Keywords keywords = objectFactory.createLexicalConceptualResourceTextInfoTypeKeywords();
            final List<String> keywordList;
            if (keywordsValue.equals(MISSING)) {
                keywordList = (portalLanguage.equals("en") ? KeyWordExtractor.extractKeyWordsEnglish(descriptionValue, 4) : KeyWordExtractor.extractKeyWordFrench(descriptionValue, 4));
            } else {
                keywordList = Arrays.asList(keywordsValue.split(","));
            }
            for (final String keyWord : keywordList) {
                keywords
                        .getKeyword()
                        .add(keyWord);
            }
            if (!keywords
                    .getKeyword()
                    .isEmpty()) {
                lexicalConceptualResourceTextInfoType.setKeywords(keywords);
            }
        }

    }

    private void domains(final LexicalConceptualResourceTextInfoType.Domains domains,
                         final NCBOOutputModel outputModel) {
        final List<String> domainList = OMTDShareModelMapper.getOntologyPropertyCollection(outputModel, "hasDomain");

        for (final String domainValue : domainList) {
            final Domain domain = objectFactory.createDomain();
            domains
                    .getDomain()
                    .add(domain);
            if (domainValue.contains("http")) {
                final String URL = domainValue + "?apikey=" + apiKey;
                domain.setSchemeURI(URL);
                try {
                    final String output = BioportalRESTRequest.query(URL);
                    final JsonValue value = Json.parse(output);
                    if(value.isObject()){
                        final JsonObject object = value.asObject();
                        final JsonValue nameVal = object.get("name");
                        if(nameVal.isString()){
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
    }


    private void languages(final LexicalConceptualResourceTextInfoType.Languages languages,
                           final LingualityInfoType lingualityInfoType, final NCBOOutputModel outputModel) {
        final List<String> values = OMTDShareModelMapper.getSubmissionPropertyCollection(outputModel, "naturalLanguage");
        int count = 0;
        for (final String value : values) {
            final LanguageInfoType languageInfoType = objectFactory.createLanguageInfoType();
            final String langValue = OMTDUtilityMapper.mapLanguage(value);
            if (!langValue.equals("unknown")) {
                languageInfoType.setLanguage(langValue);
                languages
                        .getLanguageInfo()
                        .add(languageInfoType);
                count++;
            }
        }
        if (values.isEmpty()) {
            final LanguageInfoType languageInfoType = objectFactory.createLanguageInfoType();
            languageInfoType.setLanguage(DEFAULT_LANG);
            languages
                    .getLanguageInfo()
                    .add(languageInfoType);
        }

        if ((count > -1) && (count < 2)) {
            lingualityInfoType.setLingualityType("monolingual");
        } else if (count == 2) {
            lingualityInfoType.setLingualityType("bilingual");
        } else {
            lingualityInfoType.setLingualityType("multilingual");
        }
    }

    private void dataFormat(final DataFormatInfo dataFormatInfo, final boolean downloadable) {
        if (downloadable) {
            dataFormatInfo.setDataFormat("http://w3id.org/meta-share/omtd-share/Rdf_xml");
        } else {
            dataFormatInfo.setDataFormat("http://w3id.org/meta-share/omtd-share/Json_ld");
        }
    }

    private void sizes(final SizeInfoType sizeInfoType, final NCBOOutputModel outputModel) {

        final String language = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "hasOntologyLanguage");

        final int size;
        if (!language.equals(MISSING) && language
                .toLowerCase()
                .contains("skos")) {
            size = OMTDShareModelMapper.getSubmissionPropertyIntValue(outputModel, "numberOfIndividuals");
            sizeInfoType.setSizeUnit("concepts");
        } else {
            size = OMTDShareModelMapper.getSubmissionPropertyIntValue(outputModel, "numberOfClasses");
            sizeInfoType.setSizeUnit("classes");
        }
        sizeInfoType.setSize(String.valueOf(size));
    }

    @SuppressWarnings("MethodWithMoreThanThreeNegations")
    @Override
    public void relations(final LexicalConceptualResourceInfoType.Relations relations,
                          final NCBOOutputModel outputModel) {
        final List<String> wasGeneratedByList = OMTDShareModelMapper.getSubmissionPropertyCollection(outputModel, "wasGeneratedBy");
        final List<String> isBackwardsCompatibleWithList = OMTDShareModelMapper.getSubmissionPropertyCollection(outputModel, "isBackwardsCompatibleWith");
        final List<String> similarToList = OMTDShareModelMapper.getSubmissionPropertyCollection(outputModel, "similarTo");
        final List<String> hasPartList = OMTDShareModelMapper.getSubmissionPropertyCollection(outputModel, "hasPart");

        for (final String wasGeneratedBy : wasGeneratedByList) {
            final RelationInfoType relationInfoType = objectFactory.createRelationInfoType();
            relationInfoType.setRelationType("wasGeneratedBy");
            addRelatedResource(wasGeneratedBy, relationInfoType);
        }
        for (final String isBackwardsCompatibleWith : isBackwardsCompatibleWithList) {
            final RelationInfoType relationInfoType = objectFactory.createRelationInfoType();
            relationInfoType.setRelationType("isBackwardsCompatibleWith");
            addRelatedResource(isBackwardsCompatibleWith, relationInfoType);
        }
        for (final String similarTo : similarToList) {
            final RelationInfoType relationInfoType = objectFactory.createRelationInfoType();
            relationInfoType.setRelationType("similarTo");
            addRelatedResource(similarTo, relationInfoType);
        }
        for (final String hasPart : hasPartList) {
            final RelationInfoType relationInfoType = objectFactory.createRelationInfoType();
            relationInfoType.setRelationType("hasPart");
            addRelatedResource(hasPart, relationInfoType);
        }
    }

    private void addRelatedResource(final String name, final RelationInfoType relationInfoType) {
        final RelatedResourceType relatedResourceType = objectFactory.createRelatedResourceType();
        final RelatedResourceType.ResourceNames resourceNames = objectFactory.createRelatedResourceTypeResourceNames();
        relatedResourceType.setResourceNames(resourceNames);
        final ResourceName resourceName = objectFactory.createResourceName();
        resourceName.setValue(name);
        resourceNames
                .getResourceName()
                .add(resourceName);
        relationInfoType.setRelatedResource(relatedResourceType);
    }

    @Override
    public void identificationInformation(final IdentificationInfoType identificationInfo,
                                          final NCBOOutputModel outputModel) {

        final IdentificationInfoType.ResourceNames resourceNames = objectFactory.createIdentificationInfoTypeResourceNames();
        final ResourceName resourceName = objectFactory.createResourceName();
        resourceName(identificationInfo, resourceNames, resourceName, outputModel);


        final IdentificationInfoType.Descriptions descriptions = objectFactory.createIdentificationInfoTypeDescriptions();
        final IdentificationInfoType.Descriptions.Description description = objectFactory.createIdentificationInfoTypeDescriptionsDescription();
        resourceDescription(descriptions, description, outputModel);
        identificationInfo.setDescriptions(descriptions);


        final IdentificationInfoType.ResourceIdentifiers identifiers = objectFactory.createIdentificationInfoTypeResourceIdentifiers();
        resourceIdentifier(identifiers, outputModel);
        identificationInfo.setResourceIdentifiers(identifiers);

        publicTag(identificationInfo, outputModel);

    }

    private void resourceName(final IdentificationInfoType identificationInfoType,
                              final IdentificationInfoType.ResourceNames resourceNames,
                              final ResourceName resourceName, final NCBOOutputModel outputModel) {
        final String acronym = OMTDShareModelMapper.getOntologyPropertyValue(outputModel, "acronym");

        final String name = OMTDShareModelMapper.getOntologyPropertyValue(outputModel, "name");

        //Acronym as a short name
        identificationInfoType.setResourceShortName(acronym);

        resourceName.setLang(portalLanguage);
        if (name.equals(MISSING)) {
            resourceName.setValue(acronym);
        } else {
            resourceName.setValue(name);
        }

        resourceNames
                .getResourceName()
                .add(resourceName);

        identificationInfoType.setResourceNames(resourceNames);
    }

    private void resourceDescription(final IdentificationInfoType.Descriptions descriptions,
                                     final IdentificationInfoType.Descriptions.Description description,
                                     final NCBOOutputModel outputModel) {
        final String descriptionValue = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "description");
        description.setLang(DEFAULT_LANG);
        if (descriptionValue.equals(MISSING)) {
            description.setValue("");
        } else {
            description.setValue(GREATER_THAN
                    .matcher(LESSER_THAN
                            .matcher(descriptionValue)
                            .replaceAll("&lt;"))
                    .replaceAll("&gt;"));
        }
        descriptions
                .getDescription()
                .add(description);
    }

    @SuppressWarnings("LawOfDemeter")
    private void resourceIdentifier(final IdentificationInfoType.ResourceIdentifiers identifiers,
                                    final NCBOOutputModel outputModel) {
        final String uriValue = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "URI");
        final String identifierValue = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "identifier");
        final String id = OMTDShareModelMapper.getOntologyPropertyValue(outputModel, "@id");

        identifiers
                .getResourceIdentifier()
                .add(
                        resourceId(uriValue)
                                .orElse(resourceId(identifierValue)
                                        .orElse(resourceId(id)
                                                .orElse(emptyId())))
                );
    }

    @SuppressWarnings("MethodReturnOfConcreteClass")
    private ResourceIdentifierType emptyId() {
        return objectFactory.createResourceIdentifierType();
    }

    private Optional<ResourceIdentifierType> resourceId(final String identifierValue) {
        final Optional<ResourceIdentifierType> result;
        if (!identifierValue.equals(MISSING) && !identifierValue.isEmpty()) {
            final ResourceIdentifierType resourceIdentifier = emptyId();
            resourceIdentifier.setResourceIdentifierSchemeName("URL");
            resourceIdentifier.setValue(identifierValue);
            result = Optional.of(resourceIdentifier);
        } else {
            result = Optional.empty();
        }
        return result;
    }


    private void publicTag(final IdentificationInfoType identificationInfoType, final NCBOOutputModel outputModel) {
        identificationInfoType.setPublic(
                OMTDShareModelMapper
                        .getOntologyPropertyValue(outputModel, "viewingRestriction")
                        .equals("public"));
    }

    @Override
    public void version(final VersionInfoType versionInfoType, final NCBOOutputModel outputModel) {
        final String version = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "version");
        versionInfoType.setVersion((version.equals(MISSING) ? "No Versioning" : version));
    }


    /**
     * Set the contact information metadata setting the contactInfo complex type that contains
     * the contact point and the list of contact for the resource. In bioportal there is no way of differentiating between
     * contact persons and contact organizations, therefore, by default, all contacts are treated as contact persons.
     *
     * @param contactInfoType The contact information JAXB complex type
     * @param ncboOutputModel The NCBO Output Model for the ontology submission
     */
    @Override
    @SuppressWarnings("FeatureEnvy")
    public void contactInformation(final ContactInfoType contactInfoType, final NCBOOutputModel ncboOutputModel) {
        final String contactPoint = OMTDShareModelMapper.getSubmissionPropertyValue(ncboOutputModel, "homepage", "documentation");
        if (!contactPoint.equals(MISSING)) {
            contactInfoType.setContactPoint(contactPoint);
            contactInfoType.setContactType("landingPage");
        }

        final Optional<JSONLDObject> rootObject = ncboOutputModel.asObject();
        if (rootObject.isPresent()) {
            final Optional<NCBOCollection> contactCollection = rootObject
                    .get()
                    .getCollection("contact");
            contactCollection.ifPresent(new ContactConsumer(contactPoint, contactInfoType));

            final String publisher = OMTDShareModelMapper.getSubmissionPropertyValue(ncboOutputModel, "publisher");
            if (!publisher.equals(MISSING)) {
                final ContactInfoType.ContactGroups contactGroups = objectFactory.createContactInfoTypeContactGroups();
                contactInfoType.setContactGroups(contactGroups);

                final GroupInfoType groupInfoType = objectFactory.createGroupInfoType();
                contactGroups
                        .getContactGroup()
                        .add(groupInfoType);

                final GroupInfoType.GroupNames groupNames = objectFactory.createGroupInfoTypeGroupNames();
                groupInfoType.setGroupNames(groupNames);

                final GroupInfoType.GroupNames.GroupName groupName = objectFactory.createGroupInfoTypeGroupNamesGroupName();
                groupName.setValue(publisher);
                groupName.setLang("en");
                groupNames
                        .getGroupName()
                        .add(groupName);
            }
        }
    }

    private class ContactConsumer implements Consumer<NCBOCollection> {
        private final String contactPoint;
        private final ContactInfoType contactInfoType;

        ContactConsumer(final String contactPoint, final ContactInfoType contactInfoType) {
            this.contactPoint = contactPoint;
            this.contactInfoType = contactInfoType;
        }

        @Override
        public void accept(final NCBOCollection ncboOutputModels) {
            final ContactInfoType.ContactPersons contactPersons = objectFactory.createContactInfoTypeContactPersons();
            for (final NCBOOutputModel outputModel : ncboOutputModels) {
                final Optional<JSONLDObject> asObject = outputModel.asObject();
                if (asObject.isPresent()) {
                    final JSONLDObject contact = asObject.get();
                    final String email = contact
                            .getStringValue("email")
                            .orElse(MISSING);
                    final String name = contact
                            .getStringValue("name")
                            .orElse(MISSING);
                    if (contactPoint.equals(MISSING)) {
                        contactInfoType.setContactPoint(email);
                        contactInfoType.setContactType("contactEmail");
                    }
                    if (!email.isEmpty() && !name.isEmpty()) {
                        final PersonInfoType personInfoType = objectFactory.createPersonInfoType();
                        personInfoType.setSurname(name);
                        final CommunicationInfoType communicationInfo = objectFactory.createCommunicationInfoType();

                        final CommunicationInfoType.Emails emails = objectFactory.createCommunicationInfoTypeEmails();
                        emails
                                .getEmail()
                                .add(email);
                        communicationInfo.setEmails(emails);

                        personInfoType.setCommunicationInfo(communicationInfo);
                        contactPersons
                                .getContactPerson()
                                .add(personInfoType);
                    }
                }
            }
            contactInfoType.setContactPersons(contactPersons);
        }
    }

    @Override
    public void distribution(final LexicalConceptualResourceInfoType.DistributionInfos distributionInfos,
                             final NCBOOutputModel outputModel, final boolean downloadable, final String apikey) {
        final DatasetDistributionInfoType distributionInfoType = objectFactory.createDatasetDistributionInfoType();
        distributionInfos
                .getDatasetDistributionInfo()
                .add(distributionInfoType);

        if (downloadable) {
            final String downloadLocation = OMTDShareModelMapper.getOntologyLinkValue(outputModel, "download");
            distributionInfoType.setDistributionLocation(downloadLocation + "?format=rdf&apikey=" + apikey);
            distributionInfoType.setDistributionMedium("downloadable");
        } else {
            final String restEndPoint = OMTDShareModelMapper.getOntologyPropertyValue(outputModel, "@id");
            if (!restEndPoint.equals(MISSING)) {
                distributionInfoType.setDistributionLocation(restEndPoint);
                distributionInfoType.setDistributionMedium("accessibleThroughInterface");
            }
//            final Matcher matcher = TEST_ENDPOINT_ENDING
//                    .matcher(OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "endpoint"));
//            if (matcher.find()) {
//                final String endpoint = matcher.group(1);
//                distributionInfoType.setDistributionLocation(endpoint);
//                distributionInfoType.setDistributionMedium("webExecutable");
//            }
        }

        //Handling resource sizes
        final DatasetDistributionInfoType.Sizes sizes = objectFactory.createDatasetDistributionInfoTypeSizes();
        final SizeInfoType sizeInfoType = objectFactory.createSizeInfoType();
        sizes
                .getSizeInfo()
                .add(sizeInfoType);
        sizes(sizeInfoType, outputModel);
        distributionInfoType.setSizes(sizes);

        final DatasetDistributionInfoType.TextFormats textFormats = objectFactory.createDatasetDistributionInfoTypeTextFormats();
        final TextFormatInfoType textFormatInfoType = objectFactory.createTextFormatInfoType();
        textFormats
                .getTextFormatInfo()
                .add(textFormatInfoType);
        distributionInfoType.setTextFormats(textFormats);

        final DataFormatInfo dataFormatInfo = objectFactory.createDataFormatInfo();
        textFormatInfoType.setDataFormatInfo(dataFormatInfo);
        dataFormat(dataFormatInfo, downloadable);


    }

    @SuppressWarnings({"LawOfDemeter", "IfStatementWithTooManyBranches"})
    @Override
    public void rights(final RightsInfo rightsInfo, final NCBOOutputModel outputModel, final boolean downloadable) {
        final RightsInfo.LicenceInfos licenceInfos = objectFactory.createRightsInfoLicenceInfos();
        rightsInfo.setLicenceInfos(licenceInfos);
        final LicenceInfoType licenceInfo = objectFactory.createLicenceInfoType();
        licenceInfos
                .getLicenceInfo()
                .add(licenceInfo);
        final String portalLicense = OMTDShareModelMapper.getSubmissionPropertyValue(outputModel, "hasLicense");
        final Optional<String> licenseAcronym = licenseMapper.findLicense(portalLicense);
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

            licenceInfo.setLicence("nonStandardLicenceTerms");
        } else {
            licenceInfo.setLicence("openAccessUnspecified");
        }
        if (downloadable) {
            rightsInfo.setRightsStatement("openAccess");
        } else {
            rightsInfo.setRightsStatement("restrictedAccess");
        }
    }

}
