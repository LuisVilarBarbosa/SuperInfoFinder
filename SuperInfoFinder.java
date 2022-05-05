import logic.*;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SuperInfoFinder {
    private static final String SIGINT_NAME = "INT";
    private static boolean stop = false;

    public static void main(String[] argv) {
        addSigIntHook();
        System.out.println("Press CTRL-C to stop the search.");
        SiteStore siteStore = new SiteStore();
        Iterator<String> sites = siteStore.getSites();
        String[] parameters = argv;
        HashMap<Integer, String> scoresOfSites = new HashMap<>();
        while (!stop && sites.hasNext()) {
            String url = sites.next();
            String html = RestAdapter.get(url);
            String rendering = Renderer.render(html);
            HashSet<String> newURLs = HtmlParser.parseURLs(rendering);
            siteStore.addSites(newURLs);
            int score = InfoChecker.checkHtml(rendering, parameters);
            scoresOfSites.put(score, url);
            //dump(scoresOfSites, filename);
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
