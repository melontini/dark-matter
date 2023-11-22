package me.melontini.dark_matter.api.enums;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.enums.EnumInternals;

@UtilityClass
public class EnumUtils {

    /**
     * Attempts to extend an enum by reflecting on its internal fields and adding a new enum constant.
     *
     * <p>
     * This method allows adding new elements to an enum at runtime. It should only be used if absolutely necessary, as extending enums can cause unpredictable behavior and can break code that relies on a fixed set of enum values.
     * In particular, switch statements and maps or lists that use enums as keys or values may fail when new elements are added.
     * </p>
     * <p>
     * This can also break in future java versions.
     *
     * @param enumClass    the class of the enum to extend
     * @param internalName The internal name of the new enum element. This name is used by the {@link java.lang.Enum#valueOf(Class, String)} method to map from a string representation of the enum to its corresponding enum constant.
     *                     Note that some enums may provide their own names (e.g {@link net.minecraft.util.Formatting}), which are different from the internal names.
     * @param params       the parameters to pass to the constructor of the new enum element
     * @return the newly created enum element
     * @throws RuntimeException if an error occurs during the extension process.
     */
    public static synchronized <T extends Enum<?>> T extendByReflecting(boolean reflectOnly, Class<T> enumClass, String internalName, Object... params) {
        return EnumInternals.extendByReflecting(reflectOnly, enumClass, internalName, params);
    }

    public static synchronized <T extends Enum<?>> T extendByReflecting(Class<T> enumClass, String internalName, Object... params) {
        return extendByReflecting(false, enumClass, internalName, params);
    }

    /**
     * Attempts to clear the internal cache of enum constants for the given enum class.
     *
     * @param cls the class of the enum for which to clear the cache
     */
    public static synchronized void clearEnumCache(Class<? extends Enum<?>> cls) {
        EnumInternals.clearEnumCache(cls);
    }

    public static <T extends Enum<?>> T callEnumInvoker(Class<T> cls, String internalName, Object... params) throws Throwable {
        return EnumInternals.callEnumInvoker(cls, internalName, params);
    }
}
