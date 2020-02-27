package com.sanjay.openfire.chat.utilies;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InternetConnection {

    private Context context;

    public InternetConnection(Context context) {
        this.context = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

}
