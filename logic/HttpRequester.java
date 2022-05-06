package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequester {
    private static final String USER_AGENT = "Mozilla/5.0";

    public static String get(String url) throws IOException {
        final URL obj = new URL(url);
        final HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
        final int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            final BufferedReader in = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));
            String inputLine;
            final StringBuilder response = new StringBuilder();
            while((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            return response.toString();
        }
        else if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP){
            final String location = httpURLConnection.getHeaderField("Location");
            return get(location);
        }
        else {
            throw new IOException(String.format("The GET request to the URL \"%s\" returned the status code %d.", url, responseCode));
        }
    }
}
