package logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class SiteStore {
    private final HashSet<String> returnedSites;
    private final LinkedHashSet<String> sitesToReturn;

    public SiteStore(String initialSite) {
        returnedSites = new HashSet<>();
        sitesToReturn = new LinkedHashSet<>();
        sitesToReturn.add(initialSite);
    }

    public String getNextSite() {
        final String site = sitesToReturn.iterator().next();
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
