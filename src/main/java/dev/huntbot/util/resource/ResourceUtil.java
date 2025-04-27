package dev.huntbot.util.resource;

import java.io.InputStream;
import java.net.URL;

public class ResourceUtil {
    private static final String assetsPath = "assets/";
    private static final String imagesPath = assetsPath + "images/";

    public static final String backgroundsPath = imagesPath + "backgrounds/";

    public static final String logosPath = imagesPath + "logos/";
    public static final String plainLogoPath = logosPath + "plain.png";

    public static URL getResource(String pathStr) {
        return ResourceUtil.class.getClassLoader().getResource(pathStr);
    }

    public static InputStream getResourceStream(String pathStr) {
        return ResourceUtil.class.getClassLoader().getResourceAsStream(pathStr);
    }
}
