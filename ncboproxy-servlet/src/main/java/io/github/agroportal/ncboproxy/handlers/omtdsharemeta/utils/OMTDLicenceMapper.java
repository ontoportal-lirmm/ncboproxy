package io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils;

import java.util.Optional;
import java.util.regex.Pattern;

@FunctionalInterface
public interface OMTDLicenceMapper {

    Pattern HTTPS_URL_PATTERN = Pattern.compile("^((?:https?://)?[^:]+)");

    /**
     * Find the standardized license acronym as specified by OMTDShare from a descriptor (URL, any textual description, etc)
     * @param descriptor The descriptor string
     * @return The standardized license acronym
     */
    Optional<String> findLicense(final String descriptor);

    static OMTDLicenceMapper create(){
        return new OMTDLicenceMapperImpl();
    }
}
