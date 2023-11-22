package me.melontini.dark_matter.api.base.util;

import lombok.experimental.UtilityClass;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public final class MathStuff {

    //"doesn't MathHelper already add this?" - yes, but it uses Mogang's random, and I don't like it.
    public static double nextDouble(Random random, double min, double max) {
        return min >= max ? min : random.nextDouble() * (max - min) + min;
    }

    public static double nextDouble(double min, double max) {
        return min >= max ? min : threadRandom().nextDouble() * (max - min) + min;
    }

    public static float nextFloat(Random random, float min, float max) {
        return min >= max ? min : random.nextFloat() * (max - min) + min;
    }

    public static float nextFloat(float min, float max) {
        return min >= max ? min : threadRandom().nextFloat() * (max - min) + min;
    }

    public static int nextInt(Random random, int min, int max) {
        return min >= max ? min : random.nextInt(max - min + 1) + min;
    }

    public static int nextInt(int min, int max) {
        return min >= max ? min : threadRandom().nextInt(max - min + 1) + min;
    }

    public static long nextLong(Random random, long min, long max) {
        return min >= max ? min : random.nextLong(max - min + 1) + min;
    }

    public static long nextLong(long min, long max) {
        return min >= max ? min : threadRandom().nextLong(max - min + 1) + min;
    }

    public static ThreadLocalRandom threadRandom() {
        return ThreadLocalRandom.current();
    }

    public static Random random() {
        return new Random();
    }

    public static Random random(long seed) {
        return new Random(seed);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int fastCeil(double value) {
        return (int) (value - 1024.0) + 1024;
    }

    public static int fastFloor(double value) {
        return (int) (value + 1024.0) - 1024;
    }

    public static double fastSqrt(double x) {
        return inverseSqrt(x) * x;
    }

    public static float fastSqrt(float x) {
        return inverseSqrt(x) * x;
    }

    public static double inverseSqrt(double x) {
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6ec85e7de30daL - (i >> 1);
        x = Double.longBitsToDouble(i);
        return x * (1.5 - (0.5 * x) * x * x);
    }

    public static float inverseSqrt(float x) {
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        return x * (1.5f - (0.5f * x) * x * x);
    }

    @Deprecated(since = "2.0.0")
    public static double fastSqrt(double x, int numIterations) {
        return Math.sqrt(x);
    }

    @Deprecated(since = "2.0.0")
    public static float fastSqrt(float x, int numIterations) {
        return (float) Math.sqrt(x);
    }

    @Deprecated(since = "2.0.0")
    public static double inverseSqrt(double x, int numIterations) {
        return 1 / Math.sqrt(x);
    }

    @Deprecated(since = "2.0.0")
    public static float inverseSqrt(float x, int numIterations) {
        return 1 / (float) Math.sqrt(x);
    }

    @Deprecated(since = "2.0.0")
    public static long round(double value) {
        return Math.round(value);
    }

    @Deprecated(since = "2.0.0")
    public static int round(float value) {
        return Math.round(value);
    }
}
