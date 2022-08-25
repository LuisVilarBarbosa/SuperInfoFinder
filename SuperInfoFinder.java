import logic.*;
import sun.misc.Signal;
import sun.misc.SignalHandler;

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
        if (onlyInSiteScope)
            System.out.println("Searching only inside initial site scope.");
        else
            System.out.println("Searching both inside and outside initial site scope.");
        final SiteStore siteStore = new SiteStore(initialSite);
        while (!stop && siteStore.hasNextSite()) {
            final String site = siteStore.getNextSite();
            try {
                final String html = HttpRequester.get(site);
                final HashSet<String> newURLs = HtmlParser.parseURLs(html);
                if (onlyInSiteScope)
                    newURLs.removeIf(url -> !url.startsWith(initialSite));
                siteStore.addSites(newURLs);
                final int score = InfoChecker.checkHtml(html, regexToMatch);
                final String scoreAndSite = String.format("|%10d| %s\n", score, site);
                FileStorage.append(outputFileName, scoreAndSite);
                System.out.printf(scoreAndSite);
                if (siteStore.hasNextSite())
                    Thread.sleep(intervalInMilliseconds);
            }
            catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            catch (Exception exception) {
                System.err.printf("Failed to process the following URL: %s\n", site);
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
