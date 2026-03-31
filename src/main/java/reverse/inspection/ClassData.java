package reverse.inspection;

import java.util.List;

public record ClassData(
        String className,
        boolean isInterface,
        String superClassName,
        List<String> interfaces,
        List<String> fields,
        List<String> methods,
        List<String> dependencies
) {}