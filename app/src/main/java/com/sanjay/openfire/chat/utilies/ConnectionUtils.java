package com.sanjay.openfire.chat.utilies;


import android.content.Context;

import com.sanjay.openfire.chat.service.XMPP;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;


public class ConnectionUtils {

    XMPPTCPConnection connection = null;

    public XMPPTCPConnection getXmptcConnection() {

        if (XMPP.getInstance().isConnected()) {
            try {
                connection = XMPP.getInstance().getConnection();
            } catch (XMPPException | SmackException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                connection = XMPP.getInstance().getConnection();
            } catch (XMPPException | SmackException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public void getconnected(Context context) {

        if (!XMPP.getInstance().isConnected()) {
            XMPP.getInstance().reconnectAndLogin(AppSettings.getUser(context));
        }

    }


//    public Presence getPresenceStatus(XMPPTCPConnection connection){
//        if(connection==null) {
//            try {
//                connection=XMPP.getInstance().getConnection();
//            } catch (XMPPException | SmackException | IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//        Roster roster=connection.get
//
//
//    }

}
