package io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils;


import eu.openminted.registry.domain.LexicalConceptualResourceTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class OMTDUtilityMapper {

    private static final Pattern LANG_CODE_SEPARATOR = Pattern.compile("[-_]+");
    private static final Logger logger = LoggerFactory.getLogger(OMTDUtilityMapper.class);
    private static final Pattern LEXVO_PAGE_URL_PATTERN = Pattern.compile("/page/");
    private static final Pattern LEXVO_WWW_PATTERN = Pattern.compile("www.");
    public static final String UNKNOWN_LANGUAGE_VALUE = "unknown";

    private OMTDUtilityMapper() {
    }

//    lexvo-iso639-1.tsv

    public static LexicalConceptualResourceTypeEnum mapFromFormalityLevel(final String formalityLevelValue) {
        final LexicalConceptualResourceTypeEnum resourceType;
        switch (formalityLevelValue) {
            case "http://w3id.org/nkos/nkostype#thesaurus":
                resourceType = LexicalConceptualResourceTypeEnum.THESAURUS;
                break;
            case "http://w3id.org/nkos/nkostype#taxonomy":
                resourceType = LexicalConceptualResourceTypeEnum.TYPESYSTEM;
                break;
            case "http://w3id.org/nkos/nkostype#dictionary":
                resourceType = LexicalConceptualResourceTypeEnum.MACHINE_READABLE_DICTIONARY;
                break;
            case "http://w3id.org/nkos/nkostype#semantic_network":
                resourceType = LexicalConceptualResourceTypeEnum.COMPUTATIONAL_LEXICON;
                break;
            case "http://w3id.org/nkos/nkostype#classification_schema":
                resourceType = LexicalConceptualResourceTypeEnum.TERMINOLOGICAL_RESOURCE;
                break;
            case "http://w3id.org/nkos/nkostype#list":
                resourceType = LexicalConceptualResourceTypeEnum.WORD_LIST;
                break;
            case "http://w3id.org/nkos/nkostype#glossary":
                resourceType = LexicalConceptualResourceTypeEnum.WORD_LIST;
                break;
            default:
                resourceType = LexicalConceptualResourceTypeEnum.ONTOLOGY;
                break;
        }
        return resourceType;
    }

    public static String mapLanguage(final String value) {
        final String result;
        if (value.contains("http") && value.contains("lexvo")) {
            final String finalValue = value.contains("/page/") ? LEXVO_WWW_PATTERN
                    .matcher(LEXVO_PAGE_URL_PATTERN
                            .matcher(value)
                            .replaceAll("/id/"))
                    .replaceAll("") : value;
            result = LocaleMapHolder.urlLanguageCodeMapping.get(finalValue);
        } else {
            result = getLanguageCode(value);
        }
        return result;
    }

    /**
     * This method is to get the language code from given language name
     * as locale can't be instantiate from a language name.
     * <p>
     * You can specify which language you are at : Locale loc=new Locale("en") use whatever your language is
     *
     * @param languageName -> given language name eg.: English
     * @return -> will return "eng"
     * <p>
     * Wilson M Penha Jr.
     */
    private static String getLanguageCode(final String languageName) {
        final Locale localeEn = new Locale("en");
        final String[] name = Locale.getISOLanguages(); // list of language codes
        String result = UNKNOWN_LANGUAGE_VALUE;
        for (final String aName : name) {
            final Locale locale = new Locale(aName, "US");
            // get the language name in english for comparison
            final String langLocal = locale
                    .getDisplayLanguage(localeEn)
                    .toLowerCase();
            if (languageName.equals(langLocal)) {
                result = getISO2Language(locale);
                break;
            }
        }
        return result;
    }

    private static Map<String, String> initCountryCodeMapping() {
        final Map<String, String> urlCodeMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(OMTDLicenceMapper.class.getResourceAsStream("/lexvo-iso639-1.tsv")))) {
            reader
                    .lines()
                    .forEach(line -> {
                        final String[] fields = line.split("\t");
                        urlCodeMap.put(fields[1], fields[0]);
                    });
        } catch (final IOException e) {
            logger.error("Cannot read language mappings in classpath:/lexvo-iso639-1.tsv");
        }
        return urlCodeMap;
    }

    private static final class LocaleMapHolder {
        private static final Map<String, String> urlLanguageCodeMapping = initCountryCodeMapping();
    }

    private static String getISO2Language(final Locale language) {
        final String[] localeStrings = (LANG_CODE_SEPARATOR.split(language.getLanguage()));
        return localeStrings[0];
    }

    public static List<String> getDisplayPropertyValue() {
        List<String> value = Collections.singletonList("all");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(OMTDLicenceMapper.class.getResourceAsStream("/omtdshareschema/portalpropertylist.txt")))) {
            value = reader
                    .lines()
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            logger.error("Cannot read display parameter values file classpath:/omtdshareschema/portalpropertylist.txt");
        }
        return value;
    }

}
