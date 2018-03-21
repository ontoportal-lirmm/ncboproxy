package io.github.agroportal.ncboproxy;


import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Pattern;

public interface ServletHandlerDispatcher {
    ServletHandlerDispatcher registerServletHookHandler(ServletHandler servletHandler);
    Optional<ServletHandler> findMatchingHandler(final String queryString);

    static ServletHandlerDispatcher create(){
        return new NCBOProxyServletHandlerDispatcher();
    }

    static String findMatchingPattern(final CharSequence queryString, final Iterable<String> patterns) {
        boolean atLeastOneMatch = false;
        String matched = "";
        final Iterator<String> patternIterator = patterns.iterator();
        while (!atLeastOneMatch & patternIterator.hasNext()) {
            final String pattern = patternIterator.next();
            atLeastOneMatch = Pattern
                    .compile(pattern + ".*")
                    .matcher(queryString)
                    .matches();
            if(atLeastOneMatch) {
                matched = pattern;
            }
        }
        return matched;
    }
}
