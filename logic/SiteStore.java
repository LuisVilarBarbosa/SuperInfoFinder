package logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class SiteStore {
    private final HashSet<String> sites = new HashSet<>();

    public Iterator<String> getSites() {
        return sites.iterator();
    }

    public void addSites(Collection<String> newSites) {
        sites.addAll(newSites);
    }
}
