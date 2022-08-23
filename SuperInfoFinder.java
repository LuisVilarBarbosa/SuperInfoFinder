import logic.*;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;
import java.util.HashSet;

public class SuperInfoFinder {
    private static final String SIGINT_NAME = "INT";
    private static boolean stop = false;

    public static void main(String[] argv) {
        if (argv.length != 3) {
            System.out.printf("Usage: %s <regex to match> <output file> <interval in milliseconds>\n", SuperInfoFinder.class.getName());
            System.out.println("The regex to match should consider that all characters are in lower case.");
            return;
        }
        addSigIntHook();
        final String regexToMatch = argv[0];
        final String outputFileName = argv[1];
        final long intervalInMilliseconds = Long.parseLong(argv[2]);
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
        final SiteStore siteStore = new SiteStore();
        while (!stop && siteStore.hasNextSite()) {
            final String url = siteStore.getNextSite();
            try {
                final String html = HttpRequester.get(url);
                final String rendering = Renderer.render(html);
                final HashSet<String> newURLs = HtmlParser.parseURLs(rendering);
                siteStore.addSites(newURLs);
                final int score = InfoChecker.checkHtml(rendering, regexToMatch);
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
