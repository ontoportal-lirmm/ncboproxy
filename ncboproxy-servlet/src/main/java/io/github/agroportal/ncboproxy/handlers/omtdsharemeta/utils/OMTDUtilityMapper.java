package io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils.OMTDLicenceMapper.HTTPS_URL_PATTERN;

public final class OMTDUtilityMapper {

    private static final Pattern LANG_CODE_SEPARATOR = Pattern.compile("[-_]+");
    private static final Logger logger = LoggerFactory.getLogger(OMTDUtilityMapper.class);

    private OMTDUtilityMapper() {
    }

//    lexvo-iso639-1.tsv

    public static String mapFromFormalityLevel(final String formalityLevelValue) {
        final String resourceType;
        switch (formalityLevelValue) {
            case "http://w3id.org/nkos/nkostype#thesaurus":
                resourceType = "thesaurus";
                break;
            case "http://w3id.org/nkos/nkostype#taxonomy":
                resourceType = "typesystem";
                break;
            case "http://w3id.org/nkos/nkostype#dictionary":
                resourceType = "machineReadableDictionary";
                break;
            case "http://w3id.org/nkos/nkostype#semantic_network":
                resourceType = "computationalLexicon";
                break;
            case "http://w3id.org/nkos/nkostype#classification_schema":
                resourceType = "terminologicalResource";
                break;
            case "http://w3id.org/nkos/nkostype#list":
                resourceType = "wordList";
                break;
            case "http://w3id.org/nkos/nkostype#glossary":
                resourceType = "wordList";
                break;
            default:
                resourceType = "ontology";
                break;
        }
        return resourceType;
    }

    public static String mapLanguage(final String value) {
        final String result;
        if (HTTPS_URL_PATTERN
                .matcher(value)
                .matches() && value.contains("lexvo")) { ;

            result = LocaleMapHolder.urlLanguageCodeMapping.get(value);
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
        String result = "unknown";
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
        final Map<String,String> urlCodeMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(OMTDLicenceMapper.class.getResourceAsStream("/lexvo-iso639-1.tsv")))) {
            reader
                    .lines()
                    .forEach(line -> {
                        final String[] fields = line.split("\t");
                        urlCodeMap.put(fields[1],fields[0]);
                    });
        } catch (final IOException e) {
            logger.error("Cannot read language mappings in classpath:/lexvo-iso639-1.tsv");
        }
        return  urlCodeMap;
    }

    private static final class LocaleMapHolder {
        private static final Map<String, String> urlLanguageCodeMapping = initCountryCodeMapping();
    }

    private static String getISO2Language(final Locale language) {
        final String[] localeStrings = (LANG_CODE_SEPARATOR.split(language.getLanguage()));
        return localeStrings[0];
    }

}
