package hexlet.code.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class NormalizeUrl {
    public static String normalize(String url) throws URISyntaxException, MalformedURLException {
        var u = new URI(url).toURL();
        return u.getProtocol() + "://" + u.getHost() + (u.getPort() != -1 ? (":" + u.getPort()) : "");
    }
}
