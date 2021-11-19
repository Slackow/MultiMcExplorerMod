package com.slackow.sub10;

import java.util.Random;

public class Randoms {
    public static Random GRAVEL_RANDOM = new Random();
    public static Random BARTER_RANDOM = new Random();
    public static Random BLAZE_ROD_RANDOM = new Random();
    public static Random EYE_RANDOM = new Random();
    public static Random BLAZE_CYCLE_RANDOM = new Random();
    public static Random BLAZE_POS_RANDOM = new Random();

    public static boolean perched = false;

    public static void setAll(long seed) {

        GRAVEL_RANDOM = new Random(seed);
        BARTER_RANDOM = new Random(seed);
        BLAZE_ROD_RANDOM = new Random(seed);
        EYE_RANDOM = new Random(seed);
        BLAZE_CYCLE_RANDOM = new Random(seed);
        BLAZE_POS_RANDOM = new Random(seed);
        perched = false;
    }
}
