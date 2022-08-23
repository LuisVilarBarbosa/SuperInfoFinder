import logic.*;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;
import java.util.HashSet;

public class SuperInfoFinder {
    private static final String SEARCH_OUTSIDE_SITE_SCOPE_OPTION = "--search-outside-site-scope";
    private static final String SIGINT_NAME = "INT";
    private static boolean stop = false;

    public static void main(String[] argv) {
        if (argv.length < 4 || argv.length > 5) {
            final String programName = SuperInfoFinder.class.getName();
            System.out.printf("Usage: %s <initial site> <regex to match> <output file> <interval in milliseconds> [%s]\n", programName, SEARCH_OUTSIDE_SITE_SCOPE_OPTION);
            System.out.println("The regex to match should consider that all characters are in lower case.");
            System.out.printf("Example usage 1: %s \"https://www.example.com\" \"for use in illustrative examples in documents\" output.txt 0\n", programName);
            System.out.printf("Example usage 2: %s \"https://www.example.com\" \"for use in illustrative examples in documents\" output.txt 1000 %s\n", programName, SEARCH_OUTSIDE_SITE_SCOPE_OPTION);
            return;
        }
        addSigIntHook();
        final String initialSite = argv[0].endsWith("/") ? argv[0] : argv[0] + "/";
        final String regexToMatch = argv[1];
        final String outputFileName = argv[2];
        final long intervalInMilliseconds = Long.parseLong(argv[3]);
        final boolean onlyInSiteScope = !(argv.length == 5 && argv[4].equalsIgnoreCase(SEARCH_OUTSIDE_SITE_SCOPE_OPTION));
        if (FileStorage.exists(outputFileName)) {
            System.out.printf("The file '%s' already exists.\n", outputFileName);
            return;
        }
        if (intervalInMilliseconds < 0) {
            System.out.println("The interval provided must be equal to or greater than 0.");
            return;
        }
        System.out.println("Press CTRL-C to stop the search.");
        System.out.printf("Trying to match the following regex: %s\n", regexToMatch);
        System.out.printf("The amount of matches will be stored in the following file: %s\n", outputFileName);
        final SiteStore siteStore = new SiteStore(initialSite);
        while (!stop && siteStore.hasNextSite()) {
            final String url = siteStore.getNextSite();
            try {
                final String html = HttpRequester.get(url);
                final HashSet<String> newURLs = HtmlParser.parseURLs(html);
                if (onlyInSiteScope)
                    newURLs.removeIf(site -> !site.startsWith(initialSite));
                siteStore.addSites(newURLs);
                final int score = InfoChecker.checkHtml(html, regexToMatch);
                final String scoreAndSite = String.format("|%10d| %s\n", score, url);
                FileStorage.append(outputFileName, scoreAndSite);
                System.out.printf(scoreAndSite);
                Thread.sleep(intervalInMilliseconds);
            }
            catch (IOException | InterruptedException exception){
                exception.printStackTrace();
            }
        }
        System.out.println("Search finished.");
    }

    private static void addSigIntHook() {
        final SignalHandler handler = signal -> {
            stop = true;
            System.out.println("Interrupted by Ctrl+C");
        };
        Signal.handle(new Signal(SIGINT_NAME), handler);
    }
}
