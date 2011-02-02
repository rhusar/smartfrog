package org.smartfrog.services.hadoop.benchmark.citerank;

/**
 *
 */
public class Utils {
    public static final String HTTP_CITESEER = "http://citeseer.ist.psu.edu/";

    public static String createCitationURL(String base, String entry) {
        if (base.isEmpty()) {
            return "";
        } else {
            return base + entry + ".html";
        }
    }

    public static String anchor(String href, String body) {
        return "<a href=\"" + href + "\">" + body + "</a>";
    }
}
