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
        SiteStore siteStore = new SiteStore();
        String[] parameters = argv;
        HashMap<String, Integer> sitesAndScores = new HashMap<>();
        while (!stop && siteStore.hasNextSite()) {
            String url = siteStore.getNextSite();
            try {
                String html = HttpRequester.get(url);
                String rendering = Renderer.render(html);
                HashSet<String> newURLs = HtmlParser.parseURLs(rendering);
                siteStore.addSites(newURLs);
                int score = InfoChecker.checkHtml(rendering, parameters);
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
        SignalHandler handler = signal -> {
            stop = true;
            System.out.println("Interrupted by Ctrl+C");
        };
        Signal.handle(new Signal(SIGINT_NAME), handler);
    }
}
