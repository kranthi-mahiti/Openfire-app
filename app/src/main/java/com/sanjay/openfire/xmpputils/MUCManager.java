/*
 * Copyright (c) 2019.
 * Project created and maintained by sanjay kranthi kumar
 * if need to contribute contact us on
 * kranthi0987@gmail.com
 */

package com.sanjay.openfire.xmpputils;


import android.content.Context;
import android.widget.Toast;

import com.sanjay.openfire.app.Constants;
import com.sanjay.openfire.database.dao.MessagesDao;
import com.sanjay.openfire.exceptions.OelpException;
import com.sanjay.openfire.utilies.ConnectionUtils;
import com.sanjay.openfire.utilies.DateandTimeUtils;
import com.sanjay.openfire.utilies.Logger;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.jid.util.JidUtil;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Date;
import java.util.Set;


public class MUCManager {
    private ConnectionUtils connectionUtils = null;
    private XMPPConnection connection = null;
    private MultiUserChatManager manager;
    private static String TAG = MUCManager.class.getSimpleName();
    private MySharedPref sharedPref = null;
    private Context mContext = null;
    DateandTimeUtils dateandTimeUtils = null;

    public MUCManager(Context mContext) {
        this.mContext = mContext;
        this.sharedPref = new MySharedPref(mContext);
        connectionUtils = new ConnectionUtils();
        dateandTimeUtils = new DateandTimeUtils();
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
    }

    public boolean isRoomExist(String roomName) throws Exception {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc = null;
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName + "@conference." + Constants.HOST);
            muc = manager.getMultiUserChat(mucJid);
            RoomInfo roomInfo = manager.getRoomInfo(muc.getRoom());
            return roomInfo != null;
        } catch (Exception e) {
            return false;
        }
    }

    public MultiUserChat getMUCChat(String roomName) {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc = null;
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName + "@conference." + Constants.HOST);
            muc = manager.getMultiUserChat(mucJid);
            Userutils userutils = new Userutils();
            userutils.setUserNickName("admin");

            Resourcepart nickname = Resourcepart.from(userutils.getUserNickname());

            muc.createOrJoin(nickname).getConfigFormManager().submitConfigurationForm();
            muc.sendMessage("hai");
        } catch (SmackException.NotConnectedException | SmackException.NoResponseException | XMPPException.XMPPErrorException | MultiUserChatException.NotAMucServiceException | MultiUserChatException.MucAlreadyJoinedException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        return muc;
    }

    public void kickOutRoomMember(String roomName, String memberNickName, String reasonForKickout) {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc = null;
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName + "@conference." + Constants.HOST);
            muc = manager.getMultiUserChat(mucJid);
            Resourcepart nickname = Resourcepart.from(memberNickName);
            muc.kickParticipant(nickname, reasonForKickout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeOutRoomMember(String roomName, String memberNickName, String reasonForban) {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc = null;
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName + "@conference." + Constants.HOST);
            muc = manager.getMultiUserChat(mucJid);
            Resourcepart nickname = Resourcepart.from(memberNickName);
            muc.banUser((Jid) nickname, reasonForban);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessageinMuc(String roomName, String messageBody) {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc = null;
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName + "@conference." + Constants.HOST);
            muc = manager.getMultiUserChat(mucJid);
            muc.sendMessage(messageBody);
            return true;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Message getMessage(String body, String intiatorMessageUUid) {
         Message message = new Message();
        message.setBody(body);
        message.addBody("generator", intiatorMessageUUid);
        message.addBody("label","false");

        return message;
    }


    public void DeleteMucGroup() {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        MultiUserChat muc = null;
        try {
            if (manager == null) {
                manager = MultiUserChatManager.getInstanceFor(connection);
            }
            muc.destroy("not any more", JidCreate.entityBareFrom(""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean joinGroup(String roomName, String nickName) {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName + "@conference." + Constants.HOST);
            MultiUserChat muc = manager.getMultiUserChat(mucJid);
           Logger.logD(TAG, "username" + sharedPref.readString(Constants.USER_NAME, ""));
            Resourcepart nickname = Resourcepart.from(sharedPref.readString(Constants.USER_NAME, ""));
            if (nickname == null) {
                Toast.makeText(mContext, "Username is empty", Toast.LENGTH_SHORT).show();
                return false;
            }
            muc.join(nickname);


            sharedPref.writeBoolean("joined", true);
            MessagesDao messagesDao = new MessagesDao();
            try {
                if (!messagesDao.userDataExists(sharedPref.readString(Constants.USER_ID, "")))
                    messagesDao.insertUserPointEntry(sharedPref.readString(Constants.USER_ID, ""), "", DateandTimeUtils.currentDateTime());
                else
                    messagesDao.insertUserPointEntry(sharedPref.readString(Constants.USER_ID, ""), "", DateandTimeUtils.currentDateTime());
            } catch (OelpException e) {
                e.printStackTrace();
            }
            sharedPref.writeString("joineddate", DateandTimeUtils.currentDateTime());
            Toast.makeText(mContext, "joined to group", Toast.LENGTH_SHORT).show();
            return true;
        } catch (SmackException.NotConnectedException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean createChatGroup(String groupCreationKey) {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(groupCreationKey + "@conference." + Constants.HOST);
            MultiUserChat muc = manager.getMultiUserChat(mucJid);
            Resourcepart nickname = Resourcepart.from(sharedPref.readString(Constants.USER_NAME, ""));
            setConfig(muc);
            muc.createOrJoin(nickname);
            return true;
        } catch (SmackException.NotConnectedException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.MucAlreadyJoinedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setConfig(MultiUserChat multiUserChat) {

        try {
            Form form = multiUserChat.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            submitForm.setTitle(multiUserChat.getReservedNickname());
            Set<Jid> owners = JidUtil.jidSetFrom(new String[]{sharedPref.readString(Constants.USER_ID, "") + "@206.189.136.186"});
            submitForm.setAnswer("muc#roomconfig_roomowners", owners.toString());
            submitForm.setAnswer("muc#roomconfig_publicroom", true);
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            multiUserChat.sendConfigurationForm(submitForm);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void leaveMuc(String roomName) {
       Logger.logD(TAG, "leaving group to :" + roomName);
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName);
            MultiUserChat muc = manager.getMultiUserChat(mucJid);
            muc.leave();
        } catch (SmackException.NotConnectedException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    public String getTimestamp(Message message) {
        DelayInformation inf = null;
        inf = message.getExtension(DelayInformation.ELEMENT, DelayInformation.NAMESPACE);
        if (inf != null) {
            Date date = inf.getStamp();
            System.out.println("date: " + date);
        }
        return dateandTimeUtils.toString();
    }
//
//    // check chatroom joined
//    public boolean isChatRoomJoined(String userName, String roomName) {
////        try {
////            Log.d("xmpp: ", "Service Name: " + connection.getServiceName());
////        } catch (Exception e) {
////            e.printStackTrace();
////            Log.d("xmpp: ", "service name error: " + e.getMessage());MultiUserChatManager
////        }
//        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
//        MultiUserChat multiUserChat = null;
//        try {
//            multiUserChat = multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME));
//        } catch (XmppStringprepException e) {
//            e.printStackTrace();
//        }
//        return multiUserChat.isJoined();
//    }
//
//    // get room status
//    public void getRoomStatus(String roomName) {
//        multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
//        // Discover information about the room
//        try {
//            multiUserChatManager.getRoomInfo(JidCreate.entityBareFrom(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME));
//        } catch (SmackException.NoResponseException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
//        } catch (XMPPException.XMPPErrorException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
//        } catch (SmackException.NotConnectedException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
//        } catch (XmppStringprepException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
//        }
//    }
//
//    // configure room for getting messages
//    public void configRoom(String roomName) {
//        Log.d("xmpp: ", "ready to receive messages in the chat room");
//        // add listener for receiving messages
//        receiveGroupMessages(roomName);
//        receiveStanza();
//        try {
//            //room info
//            RoomInfo roomInfo = multiUserChatManager.getRoomInfo(JidCreate.entityBareFrom(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME));
//            String ownerNick = multiUserChat.getOwners().get(0).getJid().getLocalpartOrNull().toString();//.getNick().toString();
//            String roomNameGetFromServer = roomInfo.getName();
//            String roomDescriptionFromServer = roomInfo.getDescription();
//
//            Log.d("xmpp: ", "Room Name: " + roomNameGetFromServer + " Room Description: " + roomInfo.getDescription() + " Room Owner Nick: " + ownerNick);
//            // room list
//            int roomListSize = multiUserChatManager.getHostedRooms((DomainBareJid) JidCreate.from(CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME)).size();
//            if (roomListSize > 0) {
//                for (int i = 0; i < roomListSize; i++) {
//                    Log.d("xmpp: ", "Room List Id: " + i + "\nRoom Name: " + multiUserChatManager.getHostedRooms((DomainBareJid) JidCreate.from(CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME)).get(i).getName() + "\nRoom JID: " + multiUserChatManager.getHostedRooms((DomainBareJid) JidCreate.from(CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME)).get(i).getJid());
//                }
//            }
//        } catch (SmackException.NoResponseException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "Get Room Configuration Error: " + e.getMessage());
//        } catch (XMPPException.XMPPErrorException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "Get Room Configuration Error: " + e.getMessage());
//        } catch (SmackException.NotConnectedException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "Get Room Configuration Error: " + e.getMessage());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "Get Room Configuration Error: " + e.getMessage());
//        } catch (MultiUserChatException.NotAMucServiceException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "Get Room Configuration Error: " + e.getMessage());
//        } catch (XmppStringprepException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // join chat room function
//    public void joinChatRoom(String userName, String roomName) {
////        try {
////            Log.d("xmpp: ", "Service Name: " + connection.getServiceName());
////        } catch (Exception e) {
////            e.printStackTrace();
////            Log.d("xmpp: ", "service name error: " + e.getMessage());
//////        }
//        Log.d(TAG, "joinChatRoom() userName : " + userName + " roomName : " + roomName);
//
//        MultiUserChat multiUserChat;
//        try {
//            MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
//            multiUserChat = multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME));
//
//            //multiUserChatManager.isServiceEnabled(JidCreate.bareFrom(userName));
//            if (createAndJoinGroup) {
//                try {
//                    multiUserChat.createOrJoin(Resourcepart.from(userName));
//
//
//                    // Get the the room's configuration form
//                    Form form = null;
//                    try {
//                        form = multiUserChat.getConfigurationForm();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        Log.d(TAG, "InterruptedException e :  " + e.getMessage());
//                    }
//                    // Create a new form to submit based on the original form
//                    Form submitForm = form.createAnswerForm();
//                    // Add default answers to the form to submit
//
//                    List<FormField> fields = form.getFields();
//                    for (FormField formField : fields) {
//                        if (!FormField.Type.hidden.equals(formField.getType()) && formField.getVariable() != null) {
//                            // Sets the default value as the answer
//                            submitForm.setDefaultAnswer(formField.getVariable());
//                        }
//                    }
//                    // Sets the new owner of the room
//                    List<String> owners = new ArrayList<>();
//                    owners.add(String.valueOf(connection.getUser()));
//                    //submitForm.setAnswer("muc#roomconfig_moderatedroom", "1");
//                    submitForm.setAnswer("muc#roomconfig_roomowners", owners);
//                    submitForm.setAnswer("muc#roomconfig_allowinvites", true);
//                    submitForm.setAnswer("muc#roomconfig_changesubject", true);
//                    submitForm.setAnswer("muc#roomconfig_persistentroom", true);
//                    //TODO: set different Group name
//                    submitForm.setAnswer("muc#roomconfig_roomname", roomName);
//                    submitForm.setAnswer("muc#roomconfig_roomdesc", roomName);
//                    // Send the completed form (with default values) to the server to configure the room
//                    try {
//                        multiUserChat.sendConfigurationForm(submitForm);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        Log.d(TAG, "InterruptedException : " + e.getMessage());
//                    }
//
//                    //Add user with permission
////                    Message message5 = new Message();
////                    message5.setStanzaId(stanzaID);
////                    EntityBareJid jid5 = JidCreate.entityBareFrom("samsung_hemant" + "@" + CHAT_SERVER_ADDRESS);
////                    multiUserChat.invite(message5, jid5, roomName);
////                    multiUserChat.grantMembership(jid5);
//
//
//                } catch (MultiUserChatException.MucAlreadyJoinedException e) {
//                    e.printStackTrace();
//                    Log.d(TAG, " MultiUserChatException.MucAlreadyJoinedException : " + e.getMessage());
//                }
//            } else {
//                multiUserChat.join(Resourcepart.from(userName));
//                //Ban user from room
//                // multiUserChat.banUser(JidCreate.bareFrom("motorola@" + CHAT_SERVER_ADDRESS), "testing");
//                //Delete room
//                //multiUserChat.destroy("not in use",null);
//               /* new Handler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        //bann user
//                        //Give membership to other users
//                        try {
//
//                            //multiUserChat.banUser(JidCreate.bareFrom("motorola@"+CHAT_SERVER_ADDRESS),"no permission");
//                            multiUserChat.grantMembership(JidCreate.bareFrom("motorola@"+CHAT_SERVER_ADDRESS));
//                        } catch (XMPPException.XMPPErrorException e) {
//                            e.printStackTrace();
//                        } catch (SmackException.NoResponseException e) {
//                            e.printStackTrace();
//                        } catch (SmackException.NotConnectedException e) {
//                            e.printStackTrace();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (XmppStringprepException e) {
//                            e.printStackTrace();
//                        }
//                        //make user as visitor
//                        *//*try {
//                            multiUserChat.revokeVoice(Resourcepart.fromOrNull("motorola"));
//                        } catch (XMPPException.XMPPErrorException e) {
//                            e.printStackTrace();
//                        } catch (SmackException.NoResponseException e) {
//                            e.printStackTrace();
//                        } catch (SmackException.NotConnectedException e) {
//                            e.printStackTrace();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }*//*
//                    }
//                });*/
//
//
//            }
//
//            multiUserChatManager.setAutoJoinOnReconnect(true);
//            //set auto join callback
//            multiUserChatManager.setAutoJoinFailedCallback(new AutoJoinFailedCallback() {
//                @Override
//                public void autoJoinFailed(MultiUserChat muc, Exception e) {
//                    Log.d(TAG, "AutoJoin Callback : " + muc + " Exception : " + e.getMessage());
//                    //joinChatRoom(connection.getUser().toString() , muc.getRoom().toString());
//                    //configRoom(muc.getRoom().toString());
//                }
//            });
//
//
//            // User2 listens for invitation rejections
//            multiUserChat.addInvitationRejectionListener(new InvitationRejectionListener() {
//                @Override
//                public void invitationDeclined(EntityBareJid invitee, String reason, Message message, MUCUser.Decline rejection) {
//                    Log.d(TAG, "invitationDeclined : invitee :" + invitee + " reason : " + reason + " message : " + message + " rejection : " + rejection.toString());
//
//                }
//            });
//
//            // User3 listens for MUC invitations
//            multiUserChatManager.addInvitationListener(new InvitationListener() {
//                @Override
//                public void invitationReceived(XMPPConnection conn, MultiUserChat room, EntityJid inviter, String reason, String password, Message message, MUCUser.Invite invitation) {
//
//                    // Reject the invitation
//                   /* try {
//                        multiUserChatManager.decline(room.getRoom().asEntityBareJid(), inviter.asEntityBareJid(), "I'm busy right now");
//                    } catch (SmackException.NotConnectedException e) {
//                        e.printStackTrace();
//                        Log.d(TAG, "decline SmackException.NotConnectedException : " + e.getMessage());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        Log.d(TAG, "decline InterruptedException : " + e.getMessage());
//                    }*/
//
//                    //Accept the invitation
//
//
//                    Log.d(TAG, "conn : " + conn.isConnected() + " roomNikcName : " + room.getNickname() +
//                            " inviter : " + inviter.asEntityBareJid() + " reason : " + reason + " password : " +
//                            password + " message : " + message.getBody() + " invitation :" + invitation.getFrom());
//                }
//            });
//            //Subject Update Listener
//            multiUserChat.addSubjectUpdatedListener(new SubjectUpdatedListener() {
//                @Override
//                public void subjectUpdated(String subject, EntityFullJid from) {
//                    Log.d(TAG, "addSubjectUpdatedListener  subject : " + subject + " from : " + from);
//                }
//            });
//
//            //add User(Current logged in user) status listerer
//            multiUserChat.addUserStatusListener(new UserStatusListener() {
//                @Override
//                public void kicked(Jid actor, String reason) {
//                    Log.d(TAG, "Logged in user kicked");
//                }
//
//                @Override
//                public void voiceGranted() {
//                    Log.d(TAG, "voiceGranted to Logged in user ");
//                }
//
//                @Override
//                public void voiceRevoked() {
//                    Log.d(TAG, "voiceRevoked from Logged in user ");
//                }
//
//                @Override
//                public void banned(Jid actor, String reason) {
//                    Log.d(TAG, "Logged in user banned");
//                }
//
//                @Override
//                public void membershipGranted() {
//                    Log.d(TAG, "membershipGranted to Logged in user");
//                }
//
//                @Override
//                public void membershipRevoked() {
//                    Log.d(TAG, "membershipRevoked from Logged in user");
//                }
//
//                @Override
//                public void moderatorGranted() {
//                    Log.d(TAG, "membershipGranted from Logged in user");
//                }
//
//                @Override
//                public void moderatorRevoked() {
//                    Log.d(TAG, "moderatorRevoked from Logged in user");
//                }
//
//                @Override
//                public void ownershipGranted() {
//                    Log.d(TAG, "ownershipGranted to Logged in user");
//                }
//
//                @Override
//                public void ownershipRevoked() {
//                    Log.d(TAG, "ownershipRevoked from Logged in user");
//                }
//
//                @Override
//                public void adminGranted() {
//                    Log.d(TAG, "adminGranted to Logged in user");
//                }
//
//                @Override
//                public void adminRevoked() {
//                    Log.d(TAG, "adminRevoked from Logged in user");
//                }
//
//                @Override
//                public void roomDestroyed(MultiUserChat alternateMUC, String reason) {
//                    Log.d(TAG, "roomDestroyed");
//                }
//            });
//            //add Partitipant status listener
//            multiUserChat.addParticipantStatusListener(new ParticipantStatusListener() {
//                @Override
//                public void joined(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat joined : " + participant);
//                }
//
//                @Override
//                public void left(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat left : " + participant);
//                }
//
//                @Override
//                public void kicked(EntityFullJid participant, Jid actor, String reason) {
//                    Log.d(TAG, "multiUserChat kicked : " + participant + " reason : " + reason);
//                }
//
//                @Override
//                public void voiceGranted(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat voiceGranted : " + participant);
//                }
//
//                @Override
//                public void voiceRevoked(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat voiceRevoked : " + participant);
//                }
//
//                @Override
//                public void banned(EntityFullJid participant, Jid actor, String reason) {
//                    Log.d(TAG, "multiUserChat banned : " + participant);
//                }
//
//                @Override
//                public void membershipGranted(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat membershipGranted : " + participant);
//                }
//
//                @Override
//                public void membershipRevoked(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat membershipRevoked : " + participant);
//                }
//
//                @Override
//                public void moderatorGranted(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat moderatorGranted : " + participant);
//                }
//
//                @Override
//                public void moderatorRevoked(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat moderatorRevoked : " + participant);
//                }
//
//                @Override
//                public void ownershipGranted(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat ownershipGranted : " + participant);
//                }
//
//                @Override
//                public void ownershipRevoked(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat ownershipRevoked : " + participant);
//                }
//
//                @Override
//                public void adminGranted(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat adminGranted : " + participant);
//                }
//
//                @Override
//                public void adminRevoked(EntityFullJid participant) {
//                    Log.d(TAG, "multiUserChat adminRevoked : " + participant);
//                }
//
//                @Override
//                public void nicknameChanged(EntityFullJid participant, Resourcepart newNickname) {
//                    Log.d(TAG, "multiUserChat nicknameChanged : " + participant + " newNickname :" + newNickname);
//                }
//            });
//            Log.d(TAG, "userName : " + userName);
//
//        } catch (SmackException.NoResponseException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "SmackException.NoResponseException Chat room join Error: " + e.getMessage());
//        } catch (XMPPException.XMPPErrorException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "XMPPException.XMPPErrorException Chat room join Error: " + e.getMessage());
//        } catch (SmackException.NotConnectedException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "SmackException.NotConnectedException Chat room join Error: " + e.getMessage());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "InterruptedException Chat room join Error: " + e.getMessage());
//        } catch (XmppStringprepException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "XmppStringprepException Chat room join Error: " + e.getMessage());
//        } catch (MultiUserChatException.NotAMucServiceException e) {
//            e.printStackTrace();
//            Log.d("xmpp: ", "MultiUserChatException.NotAMucServiceException Chat room join Error: " + e.getMessage());
//        } /*catch (MultiUserChatException.MucAlreadyJoinedException e) {
//            e.printStackTrace();
//            Log.d(TAG , "MultiUserChatException.MucAlreadyJoinedException :  "+e.getMessage());
//        }
//*/
//        // if user joined successfully
//        if (multiUserChat.isJoined()) {
//            Log.d("xmpp: ", "user has Joined in the chat room");
//            sendBroadCast("join", "done");
//            //call method to configure room
//            configRoom(roomName);
//
//            // get Roster
//            getBuddies();
//            //get user info
//            getUserInfo(userName);
//
//        }
//    }
//
//    // received Messages using multiUserChat from room
//    public void receiveGroupMessages(final String roomName) {
//        if (connected) {
//            //ArrayList<ChatItem> chatItem = new ArrayList<ChatItem>();
//            filter = MessageTypeFilter.GROUPCHAT;
//            //TODO:: added by satish
//            if(mStanzaListener != null){
//                connection.removeSyncStanzaListener(mStanzaListener);
//            }
//            mStanzaListener = new StanzaListener() {
//                @Override
//                public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
//                    Message message = (Message) packet;
//                    if (message.getBody() != null) {
//                        String from = message.getFrom().toString();
//                        String OnlyUserName = from.replace(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME + "\u002F", ""); //remove room name // (here \u002F is for forward slash)
//                        Log.d("xmpp: ", "Original sender: " + from);
//                        String body = message.getBody();
//                        String messageID = message.getStanzaId();
//                        String subject = message.getSubject();
//                        Log.d("xmpp: ", "From: " + OnlyUserName + "\nSubject: " + subject + "\nMessage: " + body + "\nMessage ID: " + messageID);
//                        EventBus.getDefault().postSticky(new ChatEvent(OnlyUserName, body, subject, messageID));
//                    }
//                }
//            };
//            connection.addSyncStanzaListener(mStanzaListener, filter);   // remove addAsyncStanzaListener to avoid duplicate messages
//        } else {
//            Log.d(TAG, " Connected status receiveGroupMessages " + connected);
//        }
//    }
//
//    // received Messages from individual user by Stanza
//    public void receiveStanza() {
//        if (connected) {
//            filter2 = MessageTypeFilter.CHAT;
//            if(mStanzaListener2 !=null){
//                connection.removeSyncStanzaListener(mStanzaListener2);
//            }
//            mStanzaListener2 = new StanzaListener() {
//                @Override
//                public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
//                    Message message = (Message) packet;
//                    if (message.getBody() != null) {
//                        String from = message.getFrom().toString();
//                        String body = message.getBody();
//                        String messageID = message.getStanzaId();
//                        String subject = message.getSubject();
//                        Log.d("xmpp: ", "From 2: " + from + "\nSubject 2: " + subject + "\nMessage 2: " + body + "\nMessage ID 2: " + messageID);
//                        if (NotificationUtils.isAppIsInBackground(mContext)) {
//                            Intent resultIntent = new Intent(mContext, SplashActivity.class);
//                            resultIntent.putExtra("message", body);
//                            NotificationUtils notificationUtils = new NotificationUtils(mContext);
//                            notificationUtils.showNotificationMessage(subject, body, "", resultIntent);
//                        }
//                        //EventBus.getDefault().postSticky(new ChatEvent(OnlyUserName, body, subject, messageID));
//                    }
//                }
//            };
//            connection.addSyncStanzaListener(mStanzaListener2, filter2);   // remove addAsyncStanzaListener to avoid duplicate messages
//        } else {
//            Log.d(TAG, " Connected status receiveStanza " + connected);
//        }
//    }
//
//    // method for ping multiUserChatManager
//    public void sendPing() {
//        /*PingManager pm = PingManager.getInstanceFor(connection);
//        pm.setPingInterval(30);  // 5 sec
//        try {
//            pm.pingMyServer();
//        } catch (SmackException.NotConnectedException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        pm.registerPingFailedListener(new PingFailedListener() {
//            @Override
//            public void pingFailed() {
//                Log.e("xmpp: ", "Ping Failed");
//            }
//        });*/
//
//        if (mPingPacketListener == null) {
//            mPingPacketListener = new PingPacketListener();
//
//        }
//
//        connection.addStanzaSendingListener(mPingPacketListener, new PingPacketFilter());
//        ProviderManager.addIQProvider("ping", "urn:xmpp:ping", new PingProvider());
//
//
//        PingManager.getInstanceFor(connection).setPingInterval(30);
//        PingManager.getInstanceFor(connection).registerPingFailedListener(new PingFailedListener() {
//            @Override
//            public void pingFailed() {
//                Log.d("xmpp: ", "Ping failed : " +
//                        NetworkChecking.getConnectivityStatusString(MyApplication.getAppApplicationContext()) +
//                        " hasConnection : " + NetworkChecking.hasConnection(MyApplication.getAppApplicationContext()));
//
//                if (connection.isConnected()) {
//                    Log.d(TAG, "connection.isConnected() == true");
//                } else {
//                    Log.d(TAG, "connection.isConnected() == false");
//                }
//
//                if (NetworkChecking.hasConnection(MyApplication.getAppApplicationContext())) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (connection.isConnected()) {
//                                connection.disconnect();
//
//                            }
//                        }
//                    }).start();
//
//                    // disconnectConnection();
//
//                }
//
//            }
//        });
//    }

}
