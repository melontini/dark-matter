package me.melontini.dark_matter.impl.base.reflect;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass">source</a>
 */
@ApiStatus.Internal
public class ReflectionInternals {
    private ReflectionInternals() {
        throw new UnsupportedOperationException();
    }
    private static int offset = -1;

    public static @Nullable <T> Constructor<T> findConstructor(@NotNull Class<T> clazz, List<Object> args) {
        Constructor<T> c = null;

        Constructor<T>[] ctxs = (Constructor<T>[]) clazz.getDeclaredConstructors();
        if (clazz.getDeclaredConstructors().length == 1) {
            c = ctxs[0];// we can skip loops if there's only 1 constructor in a class.
        } else {
            Class<?>[] classes = args.stream().map(Object::getClass).toArray(Class[]::new);

            try {
                c = clazz.getDeclaredConstructor(classes);
            } catch (Exception e) {
                for (Constructor<T> declaredConstructor : ctxs) {
                    if (declaredConstructor.getParameterCount() != args.size()) continue;

                    boolean bool = true;
                    Class<?>[] pt = declaredConstructor.getParameterTypes();
                    for (int i = 0; i < declaredConstructor.getParameterCount(); i++) {
                        if (!ClassUtils.isAssignable(classes[i], pt[i])) {
                            bool = false;
                            break;
                        }
                    }
                    if (bool) {
                        c = declaredConstructor;
                        break;
                    }
                }
            }
        }
        return c;
    }

    public static @Nullable <T> Method findMethod(@NotNull Class<T> clazz, String name, Object... args) {
        return findMethod(clazz, name, Arrays.stream(args).toList());
    }

    public static @Nullable <T> Method findMethod(@NotNull Class<T> clazz, String name, List<Object> args) {
        Method m = null;

        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length == 1) {
            m = methods[0];
        } else {
            Class<?>[] classes = args.stream().map(Object::getClass).toArray(Class[]::new);

            try {
                m = clazz.getDeclaredMethod(name, classes);
            } catch (Throwable e) {
                for (Method method : methods) {
                    if (!method.getName().equals(name)) continue;
                    if (method.getParameterCount() != args.size()) continue;

                    boolean bool = true;
                    Class<?>[] pt = method.getParameterTypes();
                    for (int i = 0; i < method.getParameterCount(); i++) {
                        if (!ClassUtils.isAssignable(classes[i], pt[i])) {
                            bool = false;
                            break;
                        }
                    }
                    if (bool) {
                        m = method;
                        break;
                    }
                }
            }
        }
        return m;
    }

    public static <T> Field findField(Class<T> clazz, String name) {
        Field f = null;

        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 1) {
            f = fields[0];
        } else {
            for (Field field : fields) {
                if (field.getName().equals(name)) {
                    f = field;
                    break;
                }
            }
        }
        return f;
    }

    public static <T extends AccessibleObject> T setAccessible(T member, boolean set) {
        MakeSure.notNull(member, "Tried to setAccessible a null constructor");
        try {
            member.setAccessible(set);
        } catch (Exception e) {
            int i = getOverrideOffset();
            UnsafeInternals.getUnsafe().putBoolean(member, i, set);
        }
        return member;
    }

    public static Field tryRemoveFinal(Field f) {
        MakeSure.notNull(f, "Tried to remove final from a null field");

        if (Modifier.isFinal(f.getModifiers())) {
            Unsafe unsafe = UnsafeInternals.getUnsafe();
            long offset;

            try {
                offset = UnsafeInternals.getObjectFieldOffset(Field.class, "modifiers");
            } catch (Exception e) {
                for (offset = 0; ; offset++) {
                    if (unsafe.getInt(f, offset) == f.getModifiers()) {
                        break;
                    }
                }
            }

            int modifiers = unsafe.getInt(f, offset);

            if (f.getModifiers() == modifiers) {
                unsafe.putInt(f, offset, f.getModifiers() & -17);
            } else {
                throw new UnsupportedOperationException("couldn't remove final");
            }
        }
        return f;
    }

    private static Method addOpensOrExports;

    public static void addOpensOrExports(Module module, String pn, Module other, boolean open, boolean syncVM) {
        if (addOpensOrExports == null) {
            try {
                addOpensOrExports = ReflectionInternals.setAccessible(ReflectionInternals.class.getModule().getClass().getDeclaredMethod("implAddExportsOrOpens", String.class, Module.class, boolean.class, boolean.class), true);
            } catch (NoSuchMethodException e) {
                DarkMatterLog.error("Couldn't add new {}. Expect errors", open ? "opens" : "exports");
                return;
            }
        }
        try {
            addOpensOrExports.invoke(module, pn, other, open, syncVM);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    //https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass
    public static int getOverrideOffset() {
        if (offset == -1) {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe"), f1 = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                f1.setAccessible(false);
                Unsafe unsafe = (Unsafe) f.get(null);
                int i;//override boolean byte offset. should result in 12 for java 17
                for (i = 0; unsafe.getBoolean(f, i) == unsafe.getBoolean(f1, i); i++) ;
                offset = i;
            } catch (Exception ignored) {
                offset = 12; //fallback to 12 just in case
            }
        }
        MakeSure.isTrue(offset != -1);
        return offset;
    }

    private static Constructor<?> handlesMockConstructor;

    public static @NotNull MethodHandles.Lookup mockLookupClass(Class<?> clazz) {
        try {
            if (handlesMockConstructor == null) {
                Constructor<?> c = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
                handlesMockConstructor = ReflectionInternals.setAccessible(c, true);
            }
            return ((MethodHandles.Lookup) handlesMockConstructor.newInstance(clazz));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method forName0;

    public static Class<?> accessRestrictedClass(String name, @Nullable ClassLoader loader) {
        if (forName0 == null) {
            try {
                Method m = Class.class.getDeclaredMethod("forName0", String.class, boolean.class, ClassLoader.class, Class.class);
                forName0 = ReflectionInternals.setAccessible(m, true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (Class<?>) forName0.invoke(null, name, false, loader, Class.class);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getField(Class<?> clazz, String name, boolean accessible) {
        try {
            var f = clazz.getDeclaredField(name);
            return accessible ? setAccessible(f, true) : f;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getField(Field field, Object o) {
        try {
            return setAccessible(field, true).get(o);
        } catch (IllegalAccessException e) {
            return UnsafeInternals.getObject(field, o);
        }
    }

    public static void setField(Field field, Object o, Object value) {
        try {
            ReflectionInternals.tryRemoveFinal(setAccessible(field, true)).set(o, value);
        } catch (IllegalAccessException e) {
            UnsafeInternals.putObject(field, o, value);
        }
    }
}
