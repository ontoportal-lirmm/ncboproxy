package io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils;

import eu.openminted.registry.domain.LicenceEnum;

import java.util.Optional;
import java.util.regex.Pattern;

@FunctionalInterface
public interface OMTDLicenceMapper {

    Pattern HTTPS_URL_PATTERN = Pattern.compile("_^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)*(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}]{2,})))(?::\\d{2,5})?(?:/[^\\s]*)?$_iuS");

    /**
     * Find the standardized license acronym as specified by OMTDShare from a descriptor (URL, any textual description, etc)
     * @param descriptor The descriptor string
     * @return The standardized license acronym
     */
    Optional<LicenceEnum> findLicense(final String descriptor);

    static OMTDLicenceMapper create(){
        return new OMTDLicenceMapperImpl();
    }
}
