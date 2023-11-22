package me.melontini.dark_matter.impl.base;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.PrependingLogger;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public final class DarkMatterLog {

    private static final PrependingLogger BACKING = PrependingLogger.get("Dark Matter", PrependingLogger.NAME_CLASS_MIX_WRAPPED);

    public static void error(String msg) {
        BACKING.error(msg);
    }

    public static void error(String msg, Throwable t) {
        BACKING.error(msg, t);
    }

    public static void error(Object msg) {
        BACKING.error(msg);
    }

    public static void error(String msg, Object... args) {
        BACKING.error(msg, args);
    }

    public static void warn(String msg) {
        BACKING.warn(msg);
    }

    public static void warn(String msg, Throwable t) {
        BACKING.warn(msg, t);
    }

    public static void warn(Object msg) {
        BACKING.warn(msg);
    }

    public static void warn(String msg, Object... args) {
        BACKING.warn(msg, args);
    }

    public static void info(String msg) {
        BACKING.info(msg);
    }

    public static void info(String msg, Throwable t) {
        BACKING.info(msg, t);
    }

    public static void info(Object msg) {
        BACKING.info(msg);
    }

    public static void info(String msg, Object... args) {
        BACKING.info(msg, args);
    }

    public static void debug(String msg) {
        BACKING.debug(msg);
    }

    public static void debug(String msg, Throwable t) {
        BACKING.debug(msg, t);
    }

    public static void debug(Object msg) {
        BACKING.debug(msg);
    }

    public static void debug(String msg, Object... args) {
        BACKING.debug(msg, args);
    }

}
