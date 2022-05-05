package logic;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {
    // Based on https://stackoverflow.com/questions/3809401/what-is-a-good-regular-expression-to-match-a-url
    private static final String URL_REGEX = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public static HashSet<String> parseURLs(String html) {
        HashSet<String> urls = new HashSet<>();
        Matcher matcher = URL_PATTERN.matcher(html);
        while (matcher.find())
            urls.add(matcher.group());
        return urls;
    }
}
