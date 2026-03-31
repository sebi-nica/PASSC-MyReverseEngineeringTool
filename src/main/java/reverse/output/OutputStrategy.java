package reverse.output;

import reverse.inspection.ClassData;
import java.util.List;

public interface OutputStrategy {
    String generate(List<ClassData> classes);
}