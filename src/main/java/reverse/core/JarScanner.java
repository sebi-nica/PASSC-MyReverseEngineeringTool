package reverse.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarScanner {

    public static List<String> getClassNames(String jarFilePath) throws IOException {
        List<String> classNames = new ArrayList<>();

        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.endsWith(".class") && !entryName.contains("module-info")) {
                    String className = entryName.replace('/', '.');
                    className = className.substring(0, className.length() - 6);
                    classNames.add(className);
                }
            }
        }
        return classNames;
    }
}