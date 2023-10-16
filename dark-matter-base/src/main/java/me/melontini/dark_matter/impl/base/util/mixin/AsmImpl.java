package me.melontini.dark_matter.impl.base.util.mixin;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.*;

import static me.melontini.dark_matter.api.base.util.Utilities.cast;

public class AsmImpl {

    private static final List<Map<String, Object>> EMPTY_ANN_LIST = Collections.unmodifiableList(new ArrayList<>());

    public static <T> T getAnnotationValue(AnnotationNode node, String name, T defaultValue) {
        if (node == null || node.values == null) return defaultValue;

        for (int i = 0; i < node.values.size(); i += 2) {
            if (name.equals(node.values.get(i))) {
                return cast(mapObjectFromAnnotation(node.values.get(i + 1)));
            }
        }

        return defaultValue;
    }

    public static Map<String, Object> mapAnnotationNode(AnnotationNode node) {
        Map<String, Object> values = new HashMap<>();

        if (node == null || node.values == null) return values;

        for (int i = 0; i < node.values.size(); i += 2) {
            String name = (String) node.values.get(i);
            Object value = mapObjectFromAnnotation(node.values.get(i + 1));
            if (name != null && value != null) values.putIfAbsent(name, value);
        }

        return values;
    }

    public static Object mapObjectFromAnnotation(Object value) {
        return mapObjectFromAnnotation(value, true, false);
    }

    public static Object mapObjectFromAnnotation(Object value, boolean loadEnums, boolean loadClasses) {
        if (value instanceof List<?> list) {
            List<Object> process = new ArrayList<>(list.size());
            for (Object o : list) {
                process.add(mapObjectFromAnnotation(o));
            }
            return process;
        } else if (value instanceof AnnotationNode node) {
            return mapAnnotationNode(node);
        } else if (value instanceof Type type && loadClasses) {
            try {
                return Class.forName(type.getClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if (value instanceof String[] enum0 && loadEnums) {
            try {
                Class<?> cls = Class.forName(enum0[0].replace("/", ".").substring(1, enum0[0].length() - 1));
                if (Enum.class.isAssignableFrom(cls)) {
                    value = Enum.valueOf(cast(cls), enum0[1]);
                    return value;
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            return value;
        }
        return value;
    }

    public static List<Map<String, Object>> emptyAnnotationList() {
        return EMPTY_ANN_LIST;
    }
}
