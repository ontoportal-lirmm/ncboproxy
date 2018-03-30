package io.github.agroportal.ncboproxy.handlers.omtdsharemeta.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class KeyWordExtractor {
    private static final Pattern PUCTUATION_PATTERN = Pattern.compile("\\p{Punct}");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile(" ");
    private static final Logger logger = LoggerFactory.getLogger(KeyWordExtractor.class);

    private static final List<String> frenchStopList = loadKeywords("/french_stoplist.txt");
    private static final List<String> englishStopList = loadKeywords("/english_stoplist.txt");

    private KeyWordExtractor() {
    }

    private static List<String> loadKeywords(final String classpathPath) {
        List<String> result = Collections.emptyList();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(OMTDLicenceMapper.class.getResourceAsStream(classpathPath)))) {
            result = reader
                    .lines()
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            logger.error("Cannot read license mapping for OMTDShare metadata output generation: classpath:/omtdshareschema/license_URI-Name_Mapping.csv");
        }
        return result;
    }

    public static List<String> extractKeyWordFrench(final CharSequence text, final int k) {
        return extractKeyWords(text, frenchStopList, k);
    }

    public static List<String> extractKeyWordsEnglish(final CharSequence text, final int k) {
        return extractKeyWords(text, englishStopList, k);
    }

    private static List<String> extractKeyWords(final CharSequence text, final Collection<String> stopList, final int k) {
        final String[] words = WHITESPACE_PATTERN.split(PUCTUATION_PATTERN
                .matcher(text)
                .replaceAll("")
                .trim().toLowerCase());
        //Extract the 4 most frequent words
        return Arrays
                .stream(words)
                .filter(word -> !stopList.contains(word))
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
