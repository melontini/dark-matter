package me.melontini.dark_matter.api.base.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

@SuppressWarnings("unused")
public class PrependingLogger {

    public static final Function<Logger, String> LOGGER_NAME = PrependingLogger::getNameOrEmpty;

    public static final Function<Logger, String> METHOD_CALLER = logger -> {
        String[] caller = Utilities.getCallerName(3).split("\\.");
        return "[" + caller[caller.length - 1] + "] ";
    };
    public static final Function<Logger, String> METHOD_CALLER_WRAPPED = logger -> {
        String[] caller = Utilities.getCallerName(4).split("\\.");
        return "[" + caller[caller.length - 1] + "] ";
    };

    public static final Function<Logger, String> NAME_METHOD_MIX = logger -> {
        String[] caller = Utilities.getCallerName(3).split("\\.");
        return getNameOrEmpty(logger) + "[" + caller[caller.length - 1] + "] ";
    };
    public static final Function<Logger, String> NAME_METHOD_MIX_WRAPPED = logger -> {
        String[] caller = Utilities.getCallerName(4).split("\\.");
        return getNameOrEmpty(logger) + "[" + caller[caller.length - 1] + "] ";
    };

    public static final Function<Logger, String> CALLING_CLASS = logger -> {
        String[] cls = Utilities.getCallerClass(3).getName().split("\\.");
        return "[" + cls[cls.length - 1] + "] ";
    };
    public static final Function<Logger, String> CALLING_CLASS_WRAPPED = logger -> {
        String[] cls = Utilities.getCallerClass(4).getName().split("\\.");
        return "[" + cls[cls.length - 1] + "] ";
    };

    public static final Function<Logger, String> NAME_CLASS_MIX = logger -> {
        String[] cls = Utilities.getCallerClass(3).getName().split("\\.");
        return getNameOrEmpty(logger) + "[" + cls[cls.length - 1] + "] ";
    };
    public static final Function<Logger, String> NAME_CLASS_MIX_WRAPPED = logger -> {
        String[] cls = Utilities.getCallerClass(4).getName().split("\\.");
        return getNameOrEmpty(logger) +  "[" + cls[cls.length - 1] + "] ";
    };

    private static final Function<Logger, String> DEFAULT = logger -> "";
    private final Logger backing;
    protected Function<Logger, String> prefixGetter = DEFAULT;

    public PrependingLogger(Logger backing, Function<Logger, String> prefixGetter) {
        this.backing = backing;
        this.prefixGetter = prefixGetter;
    }

    public PrependingLogger(Logger backing) {
        this.backing = backing;
    }

    public static PrependingLogger get() {
        String[] cls = Utilities.getCallerClass(2).getName().split("\\.");
        return new PrependingLogger(LogManager.getLogger(cls[cls.length - 1]), LOGGER_NAME);
    }

    public static PrependingLogger get(String name) {
        return new PrependingLogger(LogManager.getLogger(name), LOGGER_NAME);
    }

    public static PrependingLogger get(String name, Function<Logger, String> prefixGetter) {
        return new PrependingLogger(LogManager.getLogger(name), prefixGetter);
    }

    private static String getNameOrEmpty(Logger logger) {
        return !Utilities.isDev() ? "(" + logger.getName() + ") " : "";
    }

    public void fatal(String msg) {
        backing.fatal(prefixGetter.apply(backing) + msg);
    }

    public void fatal(String msg, Throwable t) {
        backing.fatal(prefixGetter.apply(backing) + msg, t);
    }

    public void fatal(Throwable msg) {
        backing.fatal(prefixGetter.apply(backing), msg);
    }

    public void fatal(Object msg) {
        backing.fatal(prefixGetter.apply(backing) + "{}", msg);
    }

    public void fatal(String msg, Object... args) {
        backing.fatal(prefixGetter.apply(backing) + msg, args);
    }

    public void error(String msg) {
        backing.error(prefixGetter.apply(backing) + msg);
    }

    public void error(String msg, Throwable t) {
        backing.error(prefixGetter.apply(backing) + msg, t);
    }

    public void error(Throwable msg) {
        backing.error(prefixGetter.apply(backing), msg);
    }

    public void error(Object msg) {
        backing.error(prefixGetter.apply(backing) + "{}", msg);
    }

    public void error(String msg, Object... args) {
        backing.error(prefixGetter.apply(backing) + msg, args);
    }

    public void warn(String msg) {
        backing.warn(prefixGetter.apply(backing) + msg);
    }

    public void warn(String msg, Throwable t) {
        backing.warn(prefixGetter.apply(backing) + msg, t);
    }

    public void warn(Throwable msg) {
        backing.warn(prefixGetter.apply(backing), msg);
    }

    public void warn(Object msg) {
        backing.warn(prefixGetter.apply(backing) + "{}", msg);
    }

    public void warn(String msg, Object... args) {
        backing.warn(prefixGetter.apply(backing) + msg, args);
    }

    public void info(String msg) {
        backing.info(prefixGetter.apply(backing) + msg);
    }

    public void info(String msg, Throwable t) {
        backing.info(prefixGetter.apply(backing) + msg, t);
    }

    public void info(Throwable msg) {
        backing.info(prefixGetter.apply(backing), msg);
    }

    public void info(Object msg) {
        backing.info(prefixGetter.apply(backing) + "{}", msg);
    }

    public void info(String msg, Object... args) {
        backing.info(prefixGetter.apply(backing) + msg, args);
    }

    public void debug(String msg) {
        backing.debug(prefixGetter.apply(backing) + msg);
    }

    public void debug(String msg, Throwable t) {
        backing.debug(prefixGetter.apply(backing) + msg, t);
    }

    public void debug(Throwable msg) {
        backing.debug(prefixGetter.apply(backing), msg);
    }

    public void debug(Object msg) {
        backing.debug(prefixGetter.apply(backing) + "{}", msg);
    }

    public void debug(String msg, Object... args) {
        backing.debug(prefixGetter.apply(backing) + msg, args);
    }

    public void trace(String msg) {
        backing.trace(prefixGetter.apply(backing) + msg);
    }

    public void trace(String msg, Throwable t) {
        backing.trace(prefixGetter.apply(backing) + msg, t);
    }

    public void trace(Throwable msg) {
        backing.trace(prefixGetter.apply(backing), msg);
    }

    public void trace(Object msg) {
        backing.trace(prefixGetter.apply(backing) + "{}", msg);
    }

    public void trace(String msg, Object... args) {
        backing.trace(prefixGetter.apply(backing) + msg, args);
    }

    public Logger getBacking() {
        return backing;
    }
}
