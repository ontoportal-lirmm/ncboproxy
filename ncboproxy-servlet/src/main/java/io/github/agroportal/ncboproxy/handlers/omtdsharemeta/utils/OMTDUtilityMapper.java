package io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils;


import java.util.Locale;

import static io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils.OMTDLicenceMapper.HTTPS_URL_PATTERN;

public final class OMTDUtilityMapper {

    private OMTDUtilityMapper() {
    }

    public static String mapFromFormalityLevel(final String formalityLevelValue) {
        final String resourceType;
        switch (formalityLevelValue) {
            case "http://w3id.org/nkos/nkostype#thesaurus":
                resourceType = "thesaurus";
                break;
            case "http://w3id.org/nkos/nkostype#taxonomy":
                resourceType = "typeSystem";
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

    public static String mapLanguage(final String value){
        final String result;
        if(HTTPS_URL_PATTERN.matcher(value).matches() && value.contains("lexvo")){
            final String[] parts = value.split("/");
            result = parts[parts.length-1];
        } else {
            result = getLanguageCode(value);
        }
        return result;
    }

    /**
     * This method is to get the language code from given language name
     * as locale can't be instantiate from a language name.
     *
     * You can specify which language you are at : Locale loc=new Locale("en") use whatever your language is
     *
     * @param languageName -> given language name eg.: English
     * @return -> will return "eng"
     *
     * Wilson M Penha Jr.
     */
    private static String getLanguageCode(final String languageName){
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
                result = locale.getISO3Language();
                break;
            }
        }
        return result;
    }
}
