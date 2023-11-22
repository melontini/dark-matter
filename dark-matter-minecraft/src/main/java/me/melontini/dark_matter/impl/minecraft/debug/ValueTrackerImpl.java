package me.melontini.dark_matter.impl.minecraft.debug;

import com.google.common.base.Strings;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.classes.Tuple;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

public class ValueTrackerImpl {

    private static final Map<String, Supplier<?>> TRACKERS = new LinkedHashMap<>();
    private static final Map<String, Supplier<?>> VIEW = Collections.unmodifiableMap(TRACKERS);

    private static final Map<String, Tuple<Instant, Duration>> TIMERS = new HashMap<>();
    private static final Set<String> FOR_REMOVAL = new HashSet<>();

    public static void removeTracker(String s) {
        TRACKERS.remove(s);
        TIMERS.remove(s);
    }

    public static void addTracker(String s, Supplier<?> supplier) {
        MakeSure.notNulls(s, supplier);
        TRACKERS.putIfAbsent(s, supplier);
    }

    public static void addTracker(String s, Supplier<?> supplier, Duration duration) {
        MakeSure.notNulls(s, supplier, duration);
        TRACKERS.putIfAbsent(s, supplier);
        TIMERS.put(s, Tuple.of(Instant.now(), duration));
    }

    public static void addFieldTracker(String s, Field f, Object o) {
        MakeSure.notNulls(s, f, o);
        Reflect.setAccessible(f);
        TRACKERS.put(s, () -> {
            try {
                return f.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void addFieldTracker(String s, Field f, Object o, Duration duration) {
        MakeSure.notNulls(s, f, o, duration);
        Reflect.setAccessible(f);
        TRACKERS.put(s, () -> {
            try {
                return f.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        TIMERS.put(s, Tuple.of(Instant.now(), duration));
    }

    public static void addStaticFieldTracker(String s, Field f) {
        MakeSure.notNulls(s, f);
        Reflect.setAccessible(f);
        TRACKERS.putIfAbsent(s, () -> {
            try {
                return f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void addStaticFieldTracker(String s, Field f, Duration duration) {
        MakeSure.notNulls(s, f, duration);
        Reflect.setAccessible(f);
        TRACKERS.putIfAbsent(s, () -> {
            try {
                return f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        TIMERS.put(s, Tuple.of(Instant.now(), duration));
    }

    public static void checkTimers() {
        if (TIMERS.isEmpty()) return;

        Instant now = Instant.now();
        TIMERS.forEach((s, tuple) -> {
            if (tuple.left().plus(tuple.right()).isBefore(now)) {
                FOR_REMOVAL.add(s);
            }
        });
        FOR_REMOVAL.forEach(s -> {
            TIMERS.remove(s);
            TRACKERS.remove(s);
        });
        FOR_REMOVAL.clear();
    }

    public static Map<String, Supplier<?>> getView() {
        return VIEW;
    }

    public static void tick() {
        Renderer.DARK_MATTER$VALUES_TO_RENDER.clear();

        ValueTrackerImpl.checkTimers();
        ValueTrackerImpl.getView().forEach((id, supplier) -> Renderer.DARK_MATTER$VALUES_TO_RENDER.add(id + ": " + supplier.get()));
    }

    public static class Renderer {
        static final List<String> DARK_MATTER$VALUES_TO_RENDER = new ArrayList<>();

        public static void render(DrawContext context) {
            if (!DARK_MATTER$VALUES_TO_RENDER.isEmpty()) {
                var textRenderer = MinecraftClient.getInstance().textRenderer;
                for (int i = 0; i < DARK_MATTER$VALUES_TO_RENDER.size(); ++i) {
                    String string = DARK_MATTER$VALUES_TO_RENDER.get(i);
                    if (!Strings.isNullOrEmpty(string)) {
                        int k = textRenderer.getWidth(string);
                        int m = 2 + 9 * i;
                        context.fill(1, m - 1,  2 + k + 1, m + 9 - 1, -1873784752);
                        context.drawText(textRenderer, string, 2, m, 14737632, false);
                    }
                }
            }
        }
    }
}
