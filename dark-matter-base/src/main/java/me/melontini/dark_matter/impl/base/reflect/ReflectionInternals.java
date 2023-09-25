package me.melontini.dark_matter.impl.base.reflect;

import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.reflect.UnsafeAccess;
import me.melontini.dark_matter.api.base.util.MakeSure;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass">source</a>
 */
@ApiStatus.Internal
public class ReflectionInternals {
    private ReflectionInternals() {
        throw new UnsupportedOperationException();
    }

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

    private static VarHandle override;

    public static <T extends AccessibleObject> T setAccessible(T member, boolean set) {
        MakeSure.notNull(member, "Tried to setAccessible a null constructor");
        try {
            member.setAccessible(set);
        } catch (Exception e) {
            try {
                if (override == null) {
                    MethodHandles.Lookup lookup = stealTrustedLookup();
                    override = lookup.findVarHandle(AccessibleObject.class, "override", boolean.class);
                }
                override.set(member, set);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return member;
    }

    private static MethodHandles.Lookup trustedLookup;

    public static MethodHandles.Lookup stealTrustedLookup() throws NoSuchFieldException {
        if (trustedLookup == null) {
            Field f = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            trustedLookup = (MethodHandles.Lookup) UnsafeAccess.getReference(f, null);
        }
        return trustedLookup;
    }

    public static Field getField(Class<?> clazz, String name, boolean accessible) {
        try {
            return Reflect.findField(clazz, name).map(field -> accessible ? setAccessible(field, true) : field)
                    .orElseThrow(() -> new NoSuchFieldException(name));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getField(Field field, Object o) {
        try {
            return stealTrustedLookup().unreflectGetter(field).invoke(o);
        } catch (Throwable e) {
            return UnsafeAccess.getReference(field, o);
        }
    }

    public static void setField(Field field, Object o, Object value) {
        try {
            MiscReflectionInternals.tryRemoveFinal(setAccessible(field, true)).set(o, value);
        } catch (Throwable e) {
            UnsafeAccess.putReference(field, o, value);
        }
    }
}
