package io.github.agroportal.ncboproxy.model.retrieval;


import io.github.agroportal.ncboproxy.model.APIContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

public final class BioportalRESTRequest {
    private static final Logger logger = LoggerFactory.getLogger(BioportalRESTRequest.class);
    private static final int HTTP_OK = 200;
    private static final int HTTP_OK_THRESHOLD = 400;

    private BioportalRESTRequest() {
    }

    public static String query(final String selfURL, final APIContext apiContext) throws IOException {
        final String apiKey = apiContext.getApiKey();
        final String QS = ((apiKey == null) || apiKey.isEmpty()) ?
                selfURL :
                ((selfURL.contains("?")) ?
                        String.format("%s&apikey=%s", selfURL, apiKey) :
                        String.format("%s?apikey=%s", selfURL, apiKey));
        return query(QS);
    }

    @SuppressWarnings("MethodParameterOfConcreteClass")
    public static RequestResult query(final RequestGenerator requestGenerator) throws IOException {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        final HttpURLConnection httpURLConnection = requestGenerator.createRequest();
        logger.debug("Request to Annotator: {}", httpURLConnection.getURL());
        final int code = httpURLConnection.getResponseCode();
        final String message = httpURLConnection.getResponseMessage();
        logger.info("{} - {} {}", code, message, httpURLConnection.getURL());
        final String response = streamAsString((code == HTTP_OK) ? httpURLConnection.getInputStream() : (httpURLConnection.getErrorStream()));
        final String outputMessage = ((code == HTTP_OK) || response.contains("error")) ? response : (message + response);
        logger.debug(response);
        return new RequestResult() {
            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return outputMessage;
            }
        };
    }

    @SuppressWarnings("MethodParameterOfConcreteClass")
    private static String query(final String selfURL) throws IOException {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        final URL url = new URL(selfURL);
        final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        final String ACCEPT_HEADER = "Accept";
        final String ACCEPTED_MIMES = "application/json";
        httpURLConnection.setRequestProperty(ACCEPT_HEADER, ACCEPTED_MIMES);
        httpURLConnection.setRequestProperty("Content-Length", Integer.toString(0));


        logger.debug("Requesting class info: {}", httpURLConnection.getURL());
        final int code = httpURLConnection.getResponseCode();
        final String message = httpURLConnection.getResponseMessage();
        logger.info("{} - {} {}", code, message, httpURLConnection.getURL());
        String response = streamAsString((code == HTTP_OK) ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream());
        if (response.isEmpty() && (code >= HTTP_OK_THRESHOLD)) {
            response = message;
        }
        logger.debug(response);
        return response;
    }

    @SuppressWarnings("HardcodedLineSeparator")
    private static String streamAsString(final InputStream stream) {
        String result;
        if (stream != null) {
            final StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
                String line = bufferedReader.readLine();
                while ((line != null) && !line.isEmpty()) {
                    stringBuilder
                            .append(line)
                            .append("\n");
                    line = bufferedReader.readLine();
                }

            } catch (final UnsupportedEncodingException e) {
                result = e.getLocalizedMessage();
            } catch (final IOException e) {
                stringBuilder.append(e.getLocalizedMessage());
                result = e.getLocalizedMessage();
            }
            result = stringBuilder.toString();
        } else {
            result = "";
        }
        return result;
    }

    static final class EmptyRequestResult implements RequestResult {
        @Override
        public int getCode() {
            return 0;
        }

        @Override
        public String getMessage() {
            return "";
        }
    }

}
