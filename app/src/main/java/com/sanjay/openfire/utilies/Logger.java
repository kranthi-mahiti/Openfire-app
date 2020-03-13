package com.sanjay.openfire.utilies;

import android.util.Log;



public class Logger {
    /**
     * Default constructors
     */
    private Logger() {
        // This Constructor is not Used
    }

    /**
     * function to use in catch block....
     *
     * @param tag
     * @param desc
     * @param e
     */
    public static void logE(String tag, String desc, Exception e) {
//        Crashlytics.log(Log.ERROR, tag, desc + e);
        Log.e(tag, desc, e);
    }

    /**
     * function to use for debug and showing in console..
     *
     * @param tag
     * @param desc
     */
    public static void logD(String tag, String desc) {
//        Crashlytics.log(Log.DEBUG, tag, desc);
//       Logger.logD(tag, "" + desc);
        /*int maxLogSize = 1000;
        for(int i = 0; i <= desc.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > desc.length() ? desc.length() : end;
           Logger.logD(tag, desc.substring(start, end));
        }*/
//       Logger.logD(tag, desc);
    }

    /**
     * function to use for debug and showing in console....
     *
     * @param tag
     * @param desc
     */
    public static void logV(String tag, String desc) {
//        Log.v(tag, "" + desc);
//        Crashlytics.log(Log.VERBOSE, tag, desc);
    }
}
