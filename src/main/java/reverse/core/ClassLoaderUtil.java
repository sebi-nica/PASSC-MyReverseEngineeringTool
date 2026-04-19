package reverse.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderUtil {
    public static URLClassLoader getLoader(String jarFilePath) throws MalformedURLException {
        File file = new File(jarFilePath);
        URL url = file.toURI().toURL();

        return new URLClassLoader(new URL[]{url}, ClassLoaderUtil.class.getClassLoader());
    }
}