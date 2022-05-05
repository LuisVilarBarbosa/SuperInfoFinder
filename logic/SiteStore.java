package logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class SiteStore {
    private static final String INITIAL_SITE = "https://www.example.com";
    private final HashSet<String> returnedSites;
    private final LinkedHashSet<String> sitesToReturn;

    public SiteStore() {
        returnedSites = new HashSet<>();
        sitesToReturn = new LinkedHashSet<>();
        sitesToReturn.add(INITIAL_SITE);
    }

    public String getNextSite() {
        String site = sitesToReturn.iterator().next();
        returnedSites.add(site);
        sitesToReturn.remove(site);
        return site;
    }

    public void addSites(Collection<String> newSites) {
        sitesToReturn.addAll(newSites);
        sitesToReturn.removeAll(returnedSites);
    }

    public boolean hasNextSite(){
        return sitesToReturn.size() > 0;
    }
}
