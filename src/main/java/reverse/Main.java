package reverse;

import reverse.core.ClassLoaderUtil;
import reverse.core.JarScanner;
import reverse.inspection.ClassData;
import reverse.inspection.ClassInspector;
import reverse.output.OutputStrategy;
import reverse.output.PlantUmlStrategy;
import reverse.output.YumlStrategy;

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
            // extract class name
            List<String> classNames = JarScanner.getClassNames(jarPath);
            URLClassLoader loader = ClassLoaderUtil.getLoader(jarPath);

            // inspect every class
            List<ClassData> analyzedClasses = new ArrayList<>();
            for (String className : classNames) {
                try {
                    Class<?> clazz = loader.loadClass(className);
                    analyzedClasses.add(ClassInspector.inspect(clazz));
                } catch (NoClassDefFoundError | ClassNotFoundException e) {
                    // if class relies on outside dependency, skip it
                    System.err.println("Skipping class due to missing dependency: " + className);
                }
            }

            // inject formatting strategy
            OutputStrategy strategy;
            if (format.equals("plantuml")) {
                strategy = new PlantUmlStrategy();
            } else if (format.equals("yuml")) {
                strategy = new YumlStrategy();
            } else {
                System.out.println("Format '" + format + "' not yet implemented. Defaulting to PlantUML.");
                strategy = new PlantUmlStrategy();
            }

            // generate the text
            String finalOutput = strategy.generate(analyzedClasses);
            System.out.println("\n--- GENERATED UML ---\n");
            System.out.println(finalOutput);

            loader.close();
        } catch (Exception e) {
            System.err.println("Execution failed: " + e.getMessage());
        }
    }
}