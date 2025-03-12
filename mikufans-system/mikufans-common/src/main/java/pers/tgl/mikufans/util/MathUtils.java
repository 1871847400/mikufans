package pers.tgl.mikufans.util;

import java.util.Random;

public class MathUtils {
    public static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        } else if (num > max) {
            return max;
        }
        return num;
    }

    public static long clamp(long num, long min, long max) {
        if (num < min) {
            return num;
        } else if (num > max) {
            return max;
        }
        return num;
    }

    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return num;
        } else if (num > max) {
            return max;
        }
        return num;
    }

    public static int randomInt(int min, int max) {
        Random random = new Random();
        return min + random.nextInt(max - min + 1);
    }
}