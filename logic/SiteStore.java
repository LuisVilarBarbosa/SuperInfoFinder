package logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class SiteStore {
    private static final String INITIAL_SITE = "https://www.example.com";
    private final HashSet<String> sites;

    public SiteStore() {
        sites = new HashSet<>();
        sites.add(INITIAL_SITE);
    }

    public Iterator<String> getSites() {
        return sites.iterator();
    }

    public void addSites(Collection<String> newSites) {
        sites.addAll(newSites);
    }
}
