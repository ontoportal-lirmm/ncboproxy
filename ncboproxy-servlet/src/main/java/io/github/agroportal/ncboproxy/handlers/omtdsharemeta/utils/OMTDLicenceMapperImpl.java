package io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class OMTDLicenceMapperImpl implements OMTDLicenceMapper {

    private static final Logger logger = LoggerFactory.getLogger(OMTDLicenceMapperImpl.class);
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("\\p{Punct}");
    private static final Pattern HTTP_COLON_PATTERN = Pattern.compile("http:");

    private final List<String> acoronyms;
    private final List<String> descriptions;
    private final List<String> urls;

    OMTDLicenceMapperImpl() {
        acoronyms = new ArrayList<>();
        descriptions = new ArrayList<>();
        urls = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(OMTDLicenceMapper.class.getResourceAsStream("/omtdshareschema/license_URI-Name_Mapping.csv")))) {
            reader
                    .lines()
                    .forEach(line -> {
                        final String[] fields = line.split(",");
                        acoronyms.add(fields[0]);
                        urls.add(fields[1]);
                        descriptions.add(PUNCTUATION_PATTERN
                                .matcher(fields[2]
                                        .toLowerCase()
                                        .trim())
                                .replaceAll(""));
                    });
        } catch (final IOException e) {
            logger.error("Cannot read license mapping for OMTDShare metadata output generation: classpath:/omtdshareschema/license_URI-Name_Mapping.csv");
        }
    }

    @Override
    public Optional<String> findLicense(final String descriptor) {
        final Matcher urlMatcher = HTTPS_URL_PATTERN.matcher(descriptor);
        int index = -1;
        if (urlMatcher.matches()) {

            index = urls.indexOf(HTTP_COLON_PATTERN
                    .matcher(descriptor)
                    .replaceAll("https:"));
        }
        if (index < 0) {
            index = descriptions.indexOf(PUNCTUATION_PATTERN
                    .matcher(descriptor
                            .toLowerCase()
                            .trim())
                    .replaceAll(""));
        }
        return (index < 0) ? Optional.empty() : Optional.of(acoronyms.get(index));
    }
}
