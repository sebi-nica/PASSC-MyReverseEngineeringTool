package reverse.inspection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassInspector {

    public static ClassData inspect(Class<?> clazz) {
        String className = clazz.getSimpleName();
        boolean isInterface = clazz.isInterface();

        String superClassName = null;
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            superClassName = superClass.getSimpleName();
        }

        List<String> interfaces = new ArrayList<>();
        for (Class<?> intf : clazz.getInterfaces()) {
            interfaces.add(intf.getSimpleName());
        }

        List<String> dependencies = new ArrayList<>();
        List<String> fields = new ArrayList<>();

        for (Field f : clazz.getDeclaredFields()) {
            String typeName = f.getType().getSimpleName();
            Type genericType = f.getGenericType();

            if (genericType instanceof ParameterizedType pt) {
                Type[] typeArgs = pt.getActualTypeArguments();
                if (typeArgs.length > 0) {
                    String targetName = typeArgs[0].getTypeName();
                    targetName = targetName.substring(targetName.lastIndexOf('.') + 1);
                    typeName += "<" + targetName + ">";

                    if (!targetName.startsWith("java.")) {
                        dependencies.add(targetName);
                    }
                }
            } else if (!f.getType().isPrimitive() && !f.getType().getName().startsWith("java.")) {
                dependencies.add(typeName);
            }

            fields.add(getVisibilitySymbol(f.getModifiers()) + " " + typeName + " " + f.getName());
        }

        List<String> methods = new ArrayList<>();

        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            String paramStr = Arrays.stream(c.getParameterTypes())
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", "));
            methods.add(getVisibilitySymbol(c.getModifiers()) + " " + className + "(" + paramStr + ")");
        }

        for (Method m : clazz.getDeclaredMethods()) {
            String paramStr = Arrays.stream(m.getParameterTypes())
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", "));
            methods.add(getVisibilitySymbol(m.getModifiers()) + " " + m.getReturnType().getSimpleName() + " " + m.getName() + "(" + paramStr + ")");
        }

        return new ClassData(className, isInterface, superClassName, interfaces, fields, methods, dependencies);
    }

    private static String getVisibilitySymbol(int modifiers) {
        if (Modifier.isPublic(modifiers)) return "+";
        if (Modifier.isPrivate(modifiers)) return "-";
        if (Modifier.isProtected(modifiers)) return "#";
        return "~";
    }
}