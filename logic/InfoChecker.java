package logic;

public class InfoChecker {
    public static int checkHtml(String html, String[] parameters) {
        final String lowerCaseHtml = html.toLowerCase();
        final int countAcceptedTokens = countTokenOccurrences(lowerCaseHtml, "example");
        final int countNotAcceptedTokens = countTokenOccurrences(lowerCaseHtml, "icann");
        return countAcceptedTokens - countNotAcceptedTokens;
    }

    private static int countTokenOccurrences(String text, String token)
    {
        final int tokenLength = token.length();
        final int numIterations = text.length() - tokenLength + 1;
        int count = 0;
        for (int i = 0; i < numIterations; i++) {
            String substring = text.substring(i, i + tokenLength);
            if(token.equals(substring))
                count++;
        }
        return count;
    }
}
