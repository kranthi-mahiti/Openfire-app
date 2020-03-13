package com.sanjay.openfire.utilies;

import java.util.Random;

public class ColorUtils {
    static String TAG = ColorUtils.class.getSimpleName();

    public static String getRandomColor() {
        Random random = new Random(); // Probably really put this somewhere where it gets executed only once
//        int red = random.nextInt(256*256*256);
//        int green = random.nextInt(256*256*256);
//        int blue = random.nextInt(256*256*256);
        int nextInt = random.nextInt(256 * 256 * 256);
        //Log.d(TAG, "getRandomColor:" + String.format("#%06x", nextInt));
        return String.format("#%06x", nextInt);
    }

}
