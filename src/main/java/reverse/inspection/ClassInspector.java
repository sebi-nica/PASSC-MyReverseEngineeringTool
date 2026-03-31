package reverse.inspection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ClassInspector {

    public static ClassData inspect(Class<?> c) {
        String className = c.getName();
        boolean isInterface = c.isInterface();

        String superClassName = null;
        Class<?> superClass = c.getSuperclass();
        // Ignore java.lang.Object as it clutters the UML diagram unnecessarily
        if (superClass != null && superClass != Object.class) {
            superClassName = superClass.getName();
        }

        List<String> interfaces = new ArrayList<>();
        for (Class<?> intf : c.getInterfaces()) {
            interfaces.add(intf.getName());
        }

        List<String> fields = new ArrayList<>();
        for (Field f : c.getDeclaredFields()) {
            // e.g., "private int count"
            fields.add(Modifier.toString(f.getModifiers()) + " " + f.getType().getSimpleName() + " " + f.getName());
        }

        List<String> methods = new ArrayList<>();
        for (Method m : c.getDeclaredMethods()) {
            // e.g., "public void calculate()"
            methods.add(Modifier.toString(m.getModifiers()) + " " + m.getReturnType().getSimpleName() + " " + m.getName() + "()");
        }

        List<String> dependencies = new ArrayList<>(); // Advanced bytecode analysis required for true associations

        return new ClassData(className, isInterface, superClassName, interfaces, fields, methods, dependencies);
    }
}