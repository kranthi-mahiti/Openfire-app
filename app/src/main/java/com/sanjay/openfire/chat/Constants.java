/*
 * Copyright (c) 2019.
 * Project created and maintained by sanjay kranthi kumar
 * if need to contribute contact us on
 * kranthi0987@gmail.com
 */

package com.sanjay.openfire.chat;


import com.sanjay.openfire.MyApplication;
import com.sanjay.openfire.chat.database.DatabaseHandlerClass;

public class Constants {

    public static final int PORT = 5222;

    public static final String ACTION_LOGGED_IN = "ejabberd.loggedin";
    public static final String CHAT_PASSWORD = "123456";
    //    public static String HOST = "3.86.245.10";
//    public static String HOST = "206.189.136.186";
    public static String HOST = "13.232.42.167";
    public static DatabaseHandlerClass databaseHandlerClass = new DatabaseHandlerClass(MyApplication.getContext());


}
