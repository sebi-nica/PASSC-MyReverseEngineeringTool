package reverse.output;

import reverse.inspection.ClassData;
import java.util.ArrayList;
import java.util.List;

public class YumlStrategy implements OutputStrategy {

    @Override
    public String generate(List<ClassData> classes) {
        StringBuilder sb = new StringBuilder();

        for (ClassData cd : classes) {
            String className = cd.isInterface() ? "<<" + cd.className() + ">>" : cd.className();
            sb.append("[").append(className);

            List<String> safeFields = sanitizeList(cd.fields());
            List<String> safeMethods = sanitizeList(cd.methods());

            boolean hasFields = !safeFields.isEmpty();
            boolean hasMethods = !safeMethods.isEmpty();

            if (hasFields || hasMethods) {
                sb.append("|");
                sb.append(String.join(";", safeFields));

                if (hasMethods) {
                    sb.append("|");
                    sb.append(String.join(";", safeMethods));
                }
            }
            sb.append("]\n");

            if (cd.superClassName() != null) {
                sb.append("[").append(cd.superClassName()).append("]^[").append(cd.className()).append("]\n");
            }

            for (String intf : cd.interfaces()) {
                sb.append("[<<").append(intf).append(">>]^-.-[").append(cd.className()).append("]\n");
            }

            for (String targetName : cd.dependencies()) {
                sb.append("[").append(cd.className()).append("]->[").append(targetName).append("]\n");
            }
        }

        return sb.toString();
    }

    private List<String> sanitizeList(List<String> elements) {
        List<String> safeList = new ArrayList<>();
        for (String el : elements) {
            safeList.add(el.replace("[", "［")
                    .replace("]", "］")
                    .replace("<", "＜")
                    .replace(">", "＞"));
        }
        return safeList;
    }
}