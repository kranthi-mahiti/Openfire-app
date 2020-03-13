/*
 * Copyright (c) 2019.
 * Project created and maintained by sanjay kranthi kumar
 * if need to contribute contact us on
 * kranthi0987@gmail.com
 */

package com.sanjay.openfire.app;


import com.sanjay.openfire.database.DatabaseHandlerClass;

public class Constants {

    public static final int PORT = 5222;

    public static final String ACTION_LOGGED_IN = "ejabberd.loggedin";
    public static final String CHAT_PASSWORD = "123456";
    public static final String USER_NAME = "";
    public static final String USER_ID = "";
    //    public static String HOST = "3.86.245.10";
//    public static String HOST = "206.189.136.186";
    public static String HOST = "ama1s-imac.localdomain";
    public static DatabaseHandlerClass databaseHandlerClass = new DatabaseHandlerClass(MyApplication.getContext());


}
