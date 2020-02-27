/*
 * Copyright (c) 2019.
 * Project created and maintained by sanjay kranthi kumar
 * if need to contribute contact us on
 * kranthi0987@gmail.com
 */

package com.sanjay.openfire.chat.utilies;

import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.List;

import mahiti.org.oelp.chat.models.UserModel.UserModel;
import mahiti.org.oelp.utils.Logger;

import static mahiti.org.oelp.chat.Constants.HOST;

public class Userutils {
    ConnectionUtils connectionUtils = new ConnectionUtils();
    XMPPConnection connection;
    private String TAG = Userutils.class.getSimpleName();

    public Userutils() {

    }

    public String getUserNickname() {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        VCard mVCard = new VCard();
        try {
            mVCard.load(connection, connection.getUser().asEntityBareJidIfPossible());
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mVCard.getNickName();
    }

    public void setUserNickName(String Nickname) {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        VCard mVCard = new VCard();
        try {
            mVCard.load(connection, connection.getUser().asEntityBareJidIfPossible());
            mVCard.setNickName(Nickname);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void getAllXmppUsers() {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        try {
            UserSearchManager manager = new UserSearchManager(connection);
            DomainBareJid searchFormString = connection.getServiceName().asDomainBareJid();
           Logger.logD("***", "SearchForm: " + searchFormString);
            Form searchForm = null;

            searchForm = manager.getSearchForm(searchFormString);

            Form answerForm = searchForm.createAnswerForm();

            UserSearch userSearch = new UserSearch();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", "*");

            ReportedData results = userSearch.sendSearchForm(connection, answerForm, searchFormString);
            if (results != null) {
                List<ReportedData.Row> rows = results.getRows();
                for (ReportedData.Row row : rows) {
                   Logger.logD("***", "row: " + row.getValues("Username").toString());
                }
            } else {
               Logger.logD("***", "No result found");
            }

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getUserStatus(String nickname) {
        Roster roster = Roster.getInstanceFor(connection);
        Presence availability = null;
        try {
            availability = roster.getPresence(JidCreate.bareFrom(nickname));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        Presence.Mode userMode = availability.getMode();
        int i = retrieveState_mode(userMode, availability.isAvailable());
       Logger.logD(TAG, "getUserStatus: " + i);
        return i;
    }

    public int retrieveState_mode(Presence.Mode userMode, boolean isOnline) {
        int userState = 0;
        /** 0 for offline, 1 for online, 2 for away,3 for busy*/
        if (userMode == Presence.Mode.dnd) {
            userState = 3;
        } else if (userMode == Presence.Mode.away || userMode == Presence.Mode.xa) {
            userState = 2;
        } else if (isOnline) {
            userState = 1;
        } else if (userMode == Presence.Mode.available)
            userState = 4;
        return userState;
    }

    // set current user additional information
    public void setMyExtraInfo(UserModel userModel) {
        VCard vcard = new VCard();
        vcard.setFirstName(userModel.getFirstName());
        vcard.setLastName(userModel.getLastName());
        vcard.setEmailHome(userModel.getEmailHome());
        vcard.setMiddleName(userModel.getMiddleName());
        vcard.setNickName(userModel.getNickName());
        vcard.setPhoneHome("Voice", userModel.getPhoneHome());
        vcard.setOrganization(userModel.getOrganization());
//        vcard.setJabberId();
        //vcard.setAvatar("" + image_path); //Image Path should be URL or Can be Byte Array etc.
        try {
            VCardManager vCardManager = VCardManager.getInstanceFor(connection);
            vCardManager.saveVCard(vcard);
//            vcard.save(connection);
            // send success broadcast
            //sendBroadCast("connection");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //get specific info
    public void getUserInfo(String userName) {
        VCardManager vCardManager = VCardManager.getInstanceFor(connection);
        VCard card = new VCard();
        try {
            card = vCardManager.loadVCard(JidCreate.entityBareFrom(userName + "@" + HOST));
//            card.load(connection, JidCreate.entityBareFrom(userName + "@" + CHAT_SERVER_SERVICE_NAME));
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        Log.d("xmpp: ", "Friend's Nick Name: " + card.getNickName() + "\nFriend's Email: " + card.getEmailHome());
    }

    // get user list
    public void getBuddies() {
        try {
            UserSearchManager manager = new UserSearchManager(connection);
            String searchFormString = "search." + connection.getXMPPServiceDomain();
            Log.d("***", "SearchForm: " + searchFormString);
            Form searchForm = null;

            searchForm = manager.getSearchForm((DomainBareJid) JidCreate.from(searchFormString));

            Form answerForm = searchForm.createAnswerForm();

            UserSearch userSearch = new UserSearch();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", "*");

            ReportedData results = userSearch.sendSearchForm(connection, answerForm, (DomainBareJid) JidCreate.from(searchFormString));
            if (results != null) {
                List<ReportedData.Row> rows = results.getRows();
                for (ReportedData.Row row : rows) {
                    Log.d("***", "xmpp:: row: " + row.getValues("Username").toString());  //***: xmpp:: row: [username]
                }
            } else {
                Log.d("***", "No result found");
            }

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }
    // check current user status
    public void UserStatus(String userName) {
        Roster roster = Roster.getInstanceFor(connection);
        Presence presence = null;
        try {
            presence = roster.getPresence(JidCreate.bareFrom(userName + "@" + "CHAT_SERVER_SERVICE_NAME"));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        if (presence.getType() == Presence.Type.available) {
            // User is online...
            Log.d("xmpp: ", "user Online");
        } else {
            // User is Offline...
            Log.d("xmpp: ", "user Offline");
        }
    }

//    /**
//     * Changes the occupant's nickname to a new nickname within the room. Each room occupant
//     * will receive two presence packets. One of type "unavailable" for the old nickname and one
//     * indicating availability for the new nickname. The unavailable presence will contain the new
//     * nickname and an appropriate status code (namely 303) as extended presence information. The
//     * status code 303 indicates that the occupant is changing his/her nickname.
//     *
//     * @param nickname the new nickname within the room.
//     * @throws XMPPErrorException if the new nickname is already in use by another occupant.
//     * @throws NoResponseException if there was no response from the server.
//     * @throws NotConnectedException
//     */
//    public void changeNickname(String nickname) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
//        StringUtils.requireNotNullOrEmpty(nickname, "Nickname must not be null or blank.");
//        // Check that we already have joined the room before attempting to change the
//        // nickname.
//        if (!joined) {
//            throw new IllegalStateException("Must be logged into the room to change nickname.");
//        }
//        // We change the nickname by sending a presence packet where the "to"
//        // field is in the form "roomName@service/nickname"
//        // We don't have to signal the MUC support again
//        Presence joinPresence = new Presence(Presence.Type.available);
//        joinPresence.setTo(room + "/" + nickname);
//
//        // Wait for a presence packet back from the server.
//        StanzaFilter responseFilter =
//                new AndFilter(
//                        FromMatchesFilter.createFull(room + "/" + nickname),
//                        new StanzaTypeFilter(Presence.class));
//        PacketCollector response = connection.createPacketCollectorAndSend(responseFilter, joinPresence);
//        // Wait up to a certain number of seconds for a reply. If there is a negative reply, an
//        // exception will be thrown
//        response.nextResultOrThrow();
//
//        this.nickname = nickname;
//    }


    /**
     //     * This method sends a {@link Presence} packet with the configured priority.
     //     */
//    private void sendPriorityPresence() {
//        if (!connection.isConnected()) {
//            return;
//        }
//        int priority = connection.getInstance().getConfigurationManager()
//                .getApplicationConfigurationProperties()
//                .getProperty(ApplicationPropertyXmpp.PRIORITY, 100);
//        Presence presence = new Presence(Presence.Type.available);
//        presence.setPriority(priority);
//        try {
//            connection.sendPacket(presence);
//        } catch (SmackException.NotConnectedException e) {
//            Logger.logD(TAG,"Could not send presence packet because XMPP connector is disconnected");
//        }
//    }

}
