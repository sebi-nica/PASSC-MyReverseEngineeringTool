package reverse.output;

import reverse.inspection.ClassData;
import java.util.List;

public class PlantUmlStrategy implements OutputStrategy {

    @Override
    public String generate(List<ClassData> classes) {
        StringBuilder sb = new StringBuilder("@startuml\n");

        for (ClassData cd : classes) {
            String type = cd.isInterface() ? "interface" : "class";
            sb.append(type).append(" ").append(cd.className()).append(" {\n");

            // Append attributes and methods
            for (String field : cd.fields()) sb.append("  ").append(field).append("\n");
            for (String method : cd.methods()) sb.append("  ").append(method).append("\n");
            sb.append("}\n");

            // Append Inheritance (Extends)
            if (cd.superClassName() != null) {
                sb.append(cd.superClassName()).append(" <|-- ").append(cd.className()).append("\n");
            }

            // Append Realization (Implements)
            for (String intf : cd.interfaces()) {
                sb.append(intf).append(" <|.. ").append(cd.className()).append("\n");
            }
        }

        sb.append("@enduml\n");
        return sb.toString();
    }
}