import logic.*;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class SuperInfoFinder {
    private static final String SIGINT_NAME = "INT";
    private static boolean stop = false;

    public static void main(String[] argv) {
        addSigIntHook();
        System.out.println("Press CTRL-C to stop the search.");
        final SiteStore siteStore = new SiteStore();
        final String[] parameters = argv;
        final HashMap<String, Integer> sitesAndScores = new HashMap<>();
        while (!stop && siteStore.hasNextSite()) {
            final String url = siteStore.getNextSite();
            try {
                final String html = HttpRequester.get(url);
                final String rendering = Renderer.render(html);
                final HashSet<String> newURLs = HtmlParser.parseURLs(rendering);
                siteStore.addSites(newURLs);
                final int score = InfoChecker.checkHtml(rendering, parameters);
                sitesAndScores.put(url, score);
                //dump(sitesAndScores, filename);
                System.out.println(String.format("%s -> %d", url, score));
            }
            catch (IOException exception){
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
