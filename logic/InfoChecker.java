package logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoChecker {
    public static int checkHtml(String html, String regex) {
        final String lowerCaseHtml = html.toLowerCase();
        return countRegexMatches(lowerCaseHtml, regex);
    }

    private static int countRegexMatches(String text, String regex)
    {
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }
}
