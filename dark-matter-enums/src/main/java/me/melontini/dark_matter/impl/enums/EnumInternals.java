package me.melontini.dark_matter.impl.enums;

import me.melontini.dark_matter.api.base.reflect.ReflectionUtil;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static me.melontini.dark_matter.api.base.util.Utilities.cast;

@ApiStatus.Internal
public class EnumInternals {

    private static final Map<Class<?>, Field> ENUM_TO_FIELD = new HashMap<>();//Store the field in case someone tries to call this method a bunch of times

    /*probably a good idea to make this synchronized*/
    public static synchronized <T extends Enum<?>> T extendByReflecting(boolean reflectOnly, Class<T> enumClass, String internalName, Object... params) {
        if (!reflectOnly && ExtendableEnum.class.isAssignableFrom(enumClass)) {
            try {
                return callEnumInvoker(enumClass, internalName, params);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Class<?> enumArrayClass = enumClass.arrayType();

            Field enumArray = ENUM_TO_FIELD.getOrDefault(enumClass, null); //we can't just request $VALUES directly because the field uses internal field_[some number] name format.
            if (enumArray == null) {
                int mod = Modifier.PRIVATE | Modifier.STATIC | Opcodes.ACC_SYNTHETIC;
                enumArray = Arrays.stream(enumClass.getDeclaredFields()).filter(field -> (field.getModifiers() & mod) == mod && field.getType() == enumArrayClass)
                        .findFirst().orElseThrow();
            }
            ENUM_TO_FIELD.putIfAbsent(enumClass, MakeSure.notNull(enumArray, "(reflection) couldn't find enum's $VALUES"));
            enumClass.getMethod("values").invoke(enumClass);//we need to init enumClass to access its fields, duh.

            T[] entries = cast(ReflectionUtil.getField(enumArray, enumClass));
            T last = entries[entries.length - 1];

            Object[] list = ArrayUtils.addAll(new Object[]{internalName, last.ordinal() + 1}, params);

            T entry;
            try {
                Constructor<T> c = ReflectionUtil.findConstructor(enumClass, list);
                MakeSure.notNull(c, "(reflection) Couldn't find enum constructor, possible parameter mismatch?");
                entry = cast(MethodHandles.lookup().unreflectConstructor(ReflectionUtil.setAccessible(c)).invokeWithArguments(list));//thankfully, for some reason MethodHandles can invoke enum constructors.
            } catch (Exception e) {
                throw new ReflectiveOperationException("(reflection) Couldn't create new enum instance", e);
            }
            MakeSure.notNull(entry, "(reflection) Couldn't create new enum instance");
            T[] tempArray = ArrayUtils.add(cast(ReflectionUtil.getField(enumArray, enumClass)), entry);

            ReflectionUtil.setField(enumArray, enumClass, tempArray);//We assume that $VALUES are always private and final. Although this isn't always true.
            clearEnumCache(enumClass);
            return entry;
        } catch (Throwable e) {
            throw new RuntimeException("(reflection) Enum not extended", e);
        }
    }

    public static synchronized void clearEnumCache(Class<? extends Enum<?>> cls) {
        try {
            ReflectionUtil.setField(Class.class.getDeclaredField("enumConstants"), cls, null);
        } catch (Exception e) {
            DarkMatterLog.error("Couldn't clear enumConstants. This shouldn't really happen", e);
        }

        try {
            ReflectionUtil.setField(Class.class.getDeclaredField("enumConstantDirectory"), cls, null);
        } catch (Exception e) {
            DarkMatterLog.error("Couldn't clear enumConstantDirectory. This shouldn't really happen", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T callEnumInvoker(Class<T> cls, String internalName, Object... params) throws Throwable {
        MakeSure.notEmpty(internalName, String.format("Tried to extend %s with an empty name", cls.getSimpleName()));
        List<Object> list = new ArrayList<>(List.of(internalName));
        list.addAll(List.of(params));

        return (T) ReflectionUtil.setAccessible(ReflectionUtil.findMethod(cls, "dark_matter$extendEnum", list))
                .invoke(cls, list.toArray());
    }

}
