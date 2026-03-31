package reverse;

import reverse.core.ClassLoaderUtil;
import reverse.core.JarScanner;
import reverse.inspection.ClassData;
import reverse.inspection.ClassInspector;
import reverse.output.OutputStrategy;
import reverse.output.PlantUmlStrategy;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <path-to-jar> [format]");
            return;
        }

        String jarPath = args[0];
        String format = args.length > 1 ? args[1].toLowerCase() : "plantuml";

        try {
            // 1. Extract class names from the archive
            List<String> classNames = JarScanner.getClassNames(jarPath);

            // 2. Point the custom class loader at the jar
            URLClassLoader loader = ClassLoaderUtil.getLoader(jarPath);

            // 3. Load and inspect every class
            List<ClassData> analyzedClasses = new ArrayList<>();
            for (String className : classNames) {
                try {
                    Class<?> clazz = loader.loadClass(className);
                    analyzedClasses.add(ClassInspector.inspect(clazz));
                } catch (NoClassDefFoundError | ClassNotFoundException e) {
                    // Crucial: Real-world jars are messy. If a class relies on an external
                    // library not in this jar, loading it fails. Catch it and skip gracefully.
                    System.err.println("Skipping class due to missing dependency: " + className);
                }
            }

            // 4. Inject the requested formatting strategy
            OutputStrategy strategy;
            if (format.equals("plantuml")) {
                strategy = new PlantUmlStrategy();
            } else {
                System.out.println("Format '" + format + "' not yet implemented. Defaulting to PlantUML.");
                strategy = new PlantUmlStrategy();
            }

            // 5. Generate the final diagram text and print to console
            String finalOutput = strategy.generate(analyzedClasses);
            System.out.println("\n--- GENERATED UML ---\n");
            System.out.println(finalOutput);

            loader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}