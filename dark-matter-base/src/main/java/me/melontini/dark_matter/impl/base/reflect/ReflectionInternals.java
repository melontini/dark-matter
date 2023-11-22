package me.melontini.dark_matter.impl.base.reflect;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.reflect.UnsafeAccess;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.classes.Lazy;
import org.apache.commons.lang3.ClassUtils;
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
@UtilityClass
public class ReflectionInternals {

    public static @Nullable <T> Constructor<T> findConstructor(@NotNull Class<T> clazz, List<Object> args) {
        Constructor<T>[] ctxs = (Constructor<T>[]) clazz.getDeclaredConstructors();
        Class<?>[] classes = args.stream().map(Object::getClass).toArray(Class[]::new);
        if (clazz.getDeclaredConstructors().length == 1) {
            return checkCtx(ctxs[0], classes) ? ctxs[0] : null;
        } else {
            try {
                return clazz.getDeclaredConstructor(classes);
            } catch (Exception e) {
                for (Constructor<T> ctx : ctxs) {
                    if (checkCtx(ctx, classes)) return ctx;
                }
            }
        }
        return null;
    }

    private static boolean checkCtx(Constructor<?> ctx, Class<?>[] classes) {
        if (ctx.getParameterCount() != classes.length) return false;

        Class<?>[] pt = ctx.getParameterTypes();
        for (int i = 0; i < ctx.getParameterCount(); i++) {
            if (!ClassUtils.isAssignable(classes[i], pt[i])) {
                return false;
            }
        }
        return true;
    }

    public static @Nullable <T> Method findMethod(@NotNull Class<T> clazz, String name, List<Object> args) {
        Method[] methods = clazz.getDeclaredMethods();
        Class<?>[] classes = args.stream().map(Object::getClass).toArray(Class[]::new);
        if (methods.length == 1) {
            return checkMethod(methods[0], name, classes) ? methods[0] : null;
        } else {
            try {
                return clazz.getDeclaredMethod(name, classes);
            } catch (Throwable e) {
                for (Method method : methods) {
                    if (checkMethod(method, name, classes)) return method;
                }
            }
        }
        return null;
    }

    private static boolean checkMethod(Method method, String name, Class<?>[] classes) {
        if (!method.getName().equals(name)) return false;
        if (method.getParameterCount() != classes.length) return false;

        Class<?>[] pt = method.getParameterTypes();
        for (int i = 0; i < method.getParameterCount(); i++) {
            if (!ClassUtils.isAssignable(classes[i], pt[i])) {
                return false;
            }
        }
        return true;
    }

    public static <T> Field findField(Class<T> clazz, String name) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 1) {
            return fields[0].getName().equals(name) ? fields[0] : null;
        } else {
            for (Field field : fields) {
                if (field.getName().equals(name)) {
                    return field;
                }
            }
        }
        return null;
    }

    private static final Lazy<VarHandle> override = Lazy.of(() -> () -> trustedLookup().findVarHandle(AccessibleObject.class, "override", boolean.class));

    public static <T extends AccessibleObject> T setAccessible(T member, boolean set) {
        MakeSure.notNull(member, "Tried to setAccessible a null constructor");
        try {
            member.setAccessible(set);
        } catch (Exception e) {
            override.get().set(member, set);
        }
        return member;
    }

    private static final Lazy<MethodHandles.Lookup> trustedLookup = Lazy.of(() -> () -> (MethodHandles.Lookup) UnsafeAccess.getReference(MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP"), null));

    public static MethodHandles.Lookup trustedLookup() throws Exception {
        return trustedLookup.getExc();
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
            return trustedLookup().unreflectGetter(field).invoke(o);
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
