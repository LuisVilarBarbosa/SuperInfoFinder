package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

public class HttpRequester {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final HashSet<Integer> STATUS_CODES_FOR_LOCATION = new HashSet<>(
            Arrays.asList(
                    HttpURLConnection.HTTP_MOVED_PERM,
                    HttpURLConnection.HTTP_MOVED_TEMP));
    private static final HashSet<Integer> STATUS_CODES_TO_IGNORE = new HashSet<>(
            Arrays.asList(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    HttpURLConnection.HTTP_FORBIDDEN,
                    HttpURLConnection.HTTP_NOT_FOUND));

    public static String get(String url) throws IOException {
        final URL obj = new URL(url);
        final HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
        final int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            final BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            final StringBuilder response = new StringBuilder();
            while((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            return response.toString();
        }
        else if (STATUS_CODES_FOR_LOCATION.contains(responseCode)) {
            final String location = httpURLConnection.getHeaderField("Location");
            return get(location);
        }
        else if (STATUS_CODES_TO_IGNORE.contains(responseCode)) {
            return "";
        }
        else {
            throw new IOException(String.format("The GET request to the URL \"%s\" returned the status code %d.", url, responseCode));
        }
    }
}
