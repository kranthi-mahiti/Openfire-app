/*
 * Copyright (c) 2019.
 * Project created and maintained by sanjay kranthi kumar
 * if need to contribute contact us on
 * kranthi0987@gmail.com
 */

package com.sanjay.openfire.chat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanjay.openfire.MyApplication;
import com.sanjay.openfire.R;
import com.sanjay.openfire.chat.database.dao.MessagesDTO;
import com.sanjay.openfire.chat.database.dao.MessagesDao;
import com.sanjay.openfire.chat.exceptions.OelpException;
import com.sanjay.openfire.chat.models.MessageChatModel;
import com.sanjay.openfire.chat.utilies.ConnectionUtils;
import com.sanjay.openfire.chat.utilies.DateandTimeUtils;
import com.sanjay.openfire.chat.utilies.Logger;
import com.sanjay.openfire.chat.utilies.MySharedPref;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.sanjay.openfire.chat.Constants.HOST;


public class ConversationActivity extends AppCompatActivity {
    XMPPTCPConnection connection = null;
    ConnectionUtils connectionUtils = new ConnectionUtils();
    String roomName = "";
    MySharedPref mySharedPref;
    DateandTimeUtils dateandTimeUtils;
    TextView tvTitle;
    MessagesDao messagesDao = new MessagesDao();
    Resourcepart nickname = null;
    RecyclerView recyclerView;
    ChatAdapter adapter;
    EditText message_edit_text;
    ImageView messge_send_button;
    List<MessageChatModel> messageChatModelList = new ArrayList<>();
    private String TAG = ConversationActivity.class.getSimpleName();
    private String groupUuid = null;
    private String username = null;
    private String groupName = "";
    private Toolbar toolbar;
    private int userType;
    private CardView cutomchatui;
    ReconnectionManager reconnectionManager = null;
    PingManager pingManager = null;
    ProgressDialog pd = null;
    MultiUserChatManager manager;
    MultiUserChat muc;
    EntityBareJid mucJid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
//        toolbar = findViewById(R.id.white_toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null)
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("");
//        }
        pd = new ProgressDialog(this);

        pd.setMessage("loading");
        pd.setIndeterminate(true);

        //AppUtils.setupUI(findViewById(R.id.rlMain), this);

        connection = connectionUtils.getXmptcConnection();
        reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        pingManager = PingManager.getInstanceFor(connection);
        dateandTimeUtils = new DateandTimeUtils();
        mySharedPref = new MySharedPref(this);
        try {
            nickname = Resourcepart.from(mySharedPref.readString(Constants.USER_NAME, ""));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        tvTitle = findViewById(R.id.tvTitle);
        Intent intent = getIntent();
        if (intent != null) {
            groupName = intent.getStringExtra("group_name");
            groupUuid = intent.getStringExtra("group_uuid");
            username = intent.getStringExtra("username");
        }
        roomName = groupUuid + "@conference." + HOST;
        initView();


        messge_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = message_edit_text.getText().toString().trim();
                if (!message.equalsIgnoreCase(""))
                    sendMessagetochat(message, roomName);
            }
        });
        tvTitle.setText(groupName);
        mySharedPref.writeBoolean("notify", false);

        pingManager.setPingInterval(300);
        reconnectionManager.enableAutomaticReconnection();

        manager = MultiUserChatManager.getInstanceFor(connection);
        try {
            mucJid = JidCreate.entityBareFrom(roomName);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        muc = manager.getMultiUserChat(mucJid);
        manager.setAutoJoinOnReconnect(true);
        checkJoin();

        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                stableConnection();
                adapter.notifyDataSetChanged();
            }
        }, 3000);


    }

    private void changeReadStatus() {
        MessagesDao messagesDao = new MessagesDao();
        try {
            messagesDao.changeReadStatus(groupName);
        } catch (OelpException e) {
            e.printStackTrace();
        }
    }

    private void checkJoin() {
//        if(){
        try {
            Set<EntityBareJid> roomList = manager.getJoinedRooms();
            if (!roomList.contains(manager.getRoomInfo(mucJid).getRoom())
//                    &&
//                    userType == Constants.USER_MASTER
            ) {
                muc.join(nickname);
            }
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        }
//        }

    }

    private void sendMessagetochat(String body, String roomName) {
        Logger.logD(TAG, "Sending message to :" + roomName);
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        manager = MultiUserChatManager.getInstanceFor(connection);

        try {
//            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName);
            muc = manager.getMultiUserChat(mucJid);
            if (!muc.isJoined()) {
                muc.join(nickname);
            }
            manager.setAutoJoinOnReconnect(true);
            if (!body.isEmpty() && !body.equalsIgnoreCase("")) {

                String intiatorMessageUUid = UUID.randomUUID().toString();

                messagesDao.insertMessages(intiatorMessageUUid,
                        body,
                        String.valueOf(nickname),
                        roomName,
                        DateandTimeUtils.currentDateTime(), "SENT",
                        "0", groupName, roomName);


                Message message = new Message();

                message.setBody(body);
                message.addBody("generator", intiatorMessageUUid);
                message.addBody("label", "false");
                muc.sendMessage(message);

                Handler refresh = new Handler(Looper.getMainLooper());
                refresh.post(new Runnable() {
                    public void run() {
                        MessageChatModel messageSentChatModel = new MessageChatModel();

                        messageSentChatModel.setMessage_type("SENT");
                        messageSentChatModel.setMessage(body);
                        messageSentChatModel.setMessage_time(DateandTimeUtils.currentDateTime());
                        messageSentChatModel.setTo(roomName);
                        messageSentChatModel.setFrom(nickname.toString());
                        messageSentChatModel.setAvatat_intial(nickname.toString());
                        messageChatModelList.add(messageSentChatModel);

                        adapter.setList(messageChatModelList, ConversationActivity.this);
                        if (messageChatModelList.size() != 0) {
                            recyclerView.smoothScrollToPosition(messageChatModelList.size() - 1);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }


            message_edit_text.setText("");
        } catch (OelpException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        }
    }

    public void initView() {
        message_edit_text = findViewById(R.id.edit_text_message);
        messge_send_button = findViewById(R.id.msg_sendButton);
        cutomchatui = findViewById(R.id.cutomchatui);

        recyclerView = findViewById(R.id.rvMessage);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);

        adapter = new ChatAdapter(messageChatModelList, this);
        recyclerView.setAdapter(adapter);

        if (mySharedPref.readInt("oldmessagesfetched", 0) == 0) {
            new ShowDialogAsyncTask().execute();
//            oldMessages();
        }

//        userType = mySharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER);
//
//        if (userType != Constants.USER_MASTER) {
//            cutomchatui.setVisibility(View.VISIBLE);
//        }
//
//        if (userType == Constants.USER_TRAINER || userType == Constants.USER_MASTER)
//            getRetrivedMessages();
//        else
//            getRetrivedMessagesOnDate();
//
//
//        if (userType == Constants.USER_MASTER) {
//            connectionUtils.getconnected(this);
//            pingServer();
//            onResume();
//            adapter.notifyDataSetChanged();
//        }
        message_edit_text.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                if (!messageChatModelList.isEmpty())
                    recyclerView.scrollToPosition(messageChatModelList.size() - 1);
            }
        });

        recieveIncomingChatmessage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        connectionUtils.getconnected(this);
//        recieveIncomingChatmessage();
        handleOfflineMessages(connection);
        pingServer();
        adapter.notifyDataSetChanged();
    }

    public void pingServer() {
//        pingManager.pingMyServer();
        //            pingManager.pingMyServer(true, 10);
        pingManager.pingServerIfNecessary();
        pingManager.registerPingFailedListener(new PingFailedListener() {
            @Override
            public void pingFailed() {

                connection.disconnect();
                connection = connectionUtils.getXmptcConnection();
                connectionUtils.getconnected(MyApplication.getContext());
            }
        });
    }

    public void listnerMuc() {
        connection = connectionUtils.getXmptcConnection();

        manager = MultiUserChatManager.getInstanceFor(connection);

        //            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName);
        muc = manager.getMultiUserChat(mucJid);
        muc.addMessageListener(new MessageListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void processMessage(Message message) {

                String generator = message.getBody("generator");
                String label = message.getBody("label");

//                    Logger.logD(TAG, "message" + xmlToJson);
//                    Logger.logD(TAG, "message.getBody() :" + message.getBody());
//                    Logger.logD(TAG, "message.getFrom() :" + message.getFrom());

                String from = message.getFrom().toString();
                Logger.logD(TAG, "message.getFrom():" + from + "to" + roomName);


                String contactJid = "";
                String contactName = "";
                if (from.contains("/")) {
                    contactJid = from.split("/")[0];
                    contactName = from.split("/")[1];
                    Logger.logD(TAG, "The real jid is :" + contactJid);
                    Logger.logD(TAG, "The message is from :" + from);
                } else {
                    contactJid = from;
                }


                if (message != null) {
                    if (message.getBody() != null && !message.getBody().equalsIgnoreCase("")) {
                        try {
                            if (!messagesDao.isGeneratorExists(generator)) {
                                if (contactName.equalsIgnoreCase(nickname.toString())) {
                                    if (label.equalsIgnoreCase("true")) {
                                        messagesDao.insertMessages(message.getStanzaId(),
                                                message.getBody(),
                                                contactName,
                                                roomName,
                                                getMessageTime(message.toXML()), "LABEL",
                                                "0", groupName, roomName);
                                        String finalContactName = contactName;
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                MessageChatModel messageSentChatModel1 = new MessageChatModel();

                                                messageSentChatModel1.setMessage_type("LABEL");
                                                messageSentChatModel1.setMessage(message.getBody());
                                                messageSentChatModel1.setMessage_time(getMessageTime(message.toXML()));
                                                messageSentChatModel1.setTo(roomName);
                                                messageSentChatModel1.setFrom(finalContactName);
                                                messageSentChatModel1.setAvatat_intial(finalContactName);
                                                if (!finalContactName.equalsIgnoreCase(nickname.toString())) {
                                                    messageChatModelList.add(messageSentChatModel1);
                                                }
                                                adapter.setList(messageChatModelList, ConversationActivity.this);
                                                if (messageChatModelList.size() != 0) {
                                                    recyclerView.smoothScrollToPosition(messageChatModelList.size() - 1);
                                                }
                                            }
                                        });
//                                        Intent intent = new Intent(ConversationActivity.this, NotifyService.class);
//                                        startService(intent);

                                    } else {
                                        messagesDao.insertMessages(message.getStanzaId(),
                                                message.getBody(),
                                                contactName,
                                                roomName,
                                                getMessageTime(message.toXML()), "SENT",
                                                "0", groupName, roomName);
                                        String finalContactName = contactName;
                                        runOnUiThread(() -> {
                                            MessageChatModel messageSentChatModel1 = new MessageChatModel();

                                            messageSentChatModel1.setMessage_type("SENT");
                                            messageSentChatModel1.setMessage(message.getBody());
                                            messageSentChatModel1.setMessage_time(getMessageTime(message.toXML()));
                                            messageSentChatModel1.setTo(roomName);
                                            messageSentChatModel1.setFrom(finalContactName);
                                            messageSentChatModel1.setAvatat_intial(finalContactName);
                                            if (!finalContactName.equalsIgnoreCase(nickname.toString())) {
                                                messageChatModelList.add(messageSentChatModel1);
                                            }
                                            adapter.setList(messageChatModelList, ConversationActivity.this);
                                            if (messageChatModelList.size() != 0) {
                                                recyclerView.smoothScrollToPosition(messageChatModelList.size() - 1);
                                            }
                                        });
                                    }

//                                        Intent intent = new Intent(ConversationActivity.this, NotifyService.class);
//                                        startService(intent);
                                } else if (label.equalsIgnoreCase("true")) {
                                    messagesDao.insertMessages(message.getStanzaId(),
                                            message.getBody(),
                                            contactName,
                                            roomName,
                                            getMessageTime(message.toXML()), "LABEL",
                                            "0", groupName, roomName);
                                    String finalContactName = contactName;
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            MessageChatModel messageSentChatModel1 = new MessageChatModel();

                                            messageSentChatModel1.setMessage_type("LABEL");
                                            messageSentChatModel1.setMessage(message.getBody());
                                            messageSentChatModel1.setMessage_time(getMessageTime(message.toXML()));
                                            messageSentChatModel1.setTo(roomName);
                                            messageSentChatModel1.setFrom(finalContactName);
                                            messageSentChatModel1.setAvatat_intial(finalContactName);
                                            if (!finalContactName.equalsIgnoreCase(nickname.toString())) {
                                                messageChatModelList.add(messageSentChatModel1);
                                            }
                                            adapter.setList(messageChatModelList, ConversationActivity.this);
                                            if (messageChatModelList.size() != 0) {
                                                recyclerView.smoothScrollToPosition(messageChatModelList.size() - 1);
                                            }
                                        }
                                    });
//                                        Intent intent = new Intent(ConversationActivity.this, NotifyService.class);
//                                        startService(intent);

                                } else {
                                    messagesDao.insertMessages(message.getStanzaId(),
                                            message.getBody(),
                                            contactName,
                                            roomName,
                                            getMessageTime(message.toXML()), "RECEIVED",
                                            "0", groupName, roomName);
                                    String finalContactName = contactName;
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            MessageChatModel messageSentChatModel1 = new MessageChatModel();

                                            messageSentChatModel1.setMessage_type("RECEIVED");
                                            messageSentChatModel1.setMessage(message.getBody());
                                            messageSentChatModel1.setMessage_time(getMessageTime(message.toXML()));
                                            messageSentChatModel1.setTo(roomName);
                                            messageSentChatModel1.setFrom(finalContactName);
                                            messageSentChatModel1.setAvatat_intial(finalContactName);
                                            if (!finalContactName.equalsIgnoreCase(nickname.toString())) {
                                                messageChatModelList.add(messageSentChatModel1);
                                            }
                                            adapter.setList(messageChatModelList, ConversationActivity.this);
                                            if (messageChatModelList.size() != 0) {
                                                recyclerView.smoothScrollToPosition(messageChatModelList.size() - 1);
                                            }

                                        }
                                    });
//                                        Intent intent = new Intent(ConversationActivity.this, NotifyService.class);
//                                        startService(intent);
                                }
                            }
                        } catch (OelpException e) {
                            e.printStackTrace();
                        }

                    }


                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getMessageTime(XmlStringBuilder xmlStringBuilder) {
        String currentStudentSection = null;
        try {
            InputStream i = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            String XML_ROOT_STRING = "message";
            String currentTag = null;
            // Get XmlPullParserFactory new instance
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            // Get new XmlPullParser
            XmlPullParser parser = factory.newPullParser();
            // Specify the XmlPullParser input stream
            parser.setInput(i, null);

            // Event types are END_DOCUMENT, START_TAG, END_TAG and TEXT
            int eventType = parser.getEventType();

            // String variables to hold student data
            String currentStudent = "";
            currentStudentSection = "";

            // Loop through the xml document
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (!parser.getName().equals(XML_ROOT_STRING)) {
                        currentTag = parser.getName();
                        currentStudentSection = parser.getAttributeValue(null, "stamp");
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    currentTag = null;
                } else if (eventType == XmlPullParser.TEXT) {
                    if (currentTag != null) {
                        // Get the text from current tag
                        currentStudent = parser.getText();

                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (currentStudentSection == null)
            return DateandTimeUtils.currentDateTime();
        return currentStudentSection;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getMessagegeneratoruuid(XmlStringBuilder xmlStringBuilder) {
        String currentStudentSection = null;
        String currentStudent = null;
        try {
            InputStream i = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            String XML_ROOT_STRING = "message";
            String currentTag = null;
            // Get XmlPullParserFactory new instance
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            // Get new XmlPullParser
            XmlPullParser parser = factory.newPullParser();
            // Specify the XmlPullParser input stream
            parser.setInput(i, null);

            // Event types are END_DOCUMENT, START_TAG, END_TAG and TEXT
            int eventType = parser.getEventType();


            // Loop through the xml document
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (!parser.getName().equals(XML_ROOT_STRING)) {
                        currentTag = parser.getName();
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    currentTag = null;
                } else if (eventType == XmlPullParser.TEXT) {
                    if (currentTag != null) {
                        // Get the text from current tag
                        currentStudent = parser.getText();

                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (currentStudent == null)
            currentStudent = "";
        return currentStudent;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection();
//        if (mBroadcastReceiver != null) {
//        unregisterReceiver(mBroadcastReceiver);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection();
    }

    public void recieveIncomingChatmessage() {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();

        manager = MultiUserChatManager.getInstanceFor(connection);

        //            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName);
        muc = manager.getMultiUserChat(mucJid);
        Message message;
        muc.addMessageListener(new MessageListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void processMessage(Message message) {

                String generator = message.getBody("generator");
                String label = message.getBody("label");

//                    Logger.logD(TAG, "message" + xmlToJson);
//                    Logger.logD(TAG, "message.getBody() :" + message.getBody());
//                    Logger.logD(TAG, "message.getFrom() :" + message.getFrom());

                String from = message.getFrom().toString();
                Logger.logD(TAG, "message.getFrom():" + from + "to" + roomName);


                String contactJid = "";
                String contactName = "";
                if (from.contains("/")) {
                    contactJid = from.split("/")[0];
                    contactName = from.split("/")[1];
                    Logger.logD(TAG, "The real jid is :" + contactJid);
                    Logger.logD(TAG, "The message is from :" + from);
                } else {
                    contactJid = from;
                }


                if (message != null) {
                    if (message.getBody() != null && !message.getBody().equalsIgnoreCase("")) {
                        try {
                            if (!messagesDao.isGeneratorExists(generator)) {
                                if (contactName.equalsIgnoreCase(nickname.toString())) {
                                    if (label.equalsIgnoreCase("true")) {
                                        messagesDao.insertMessages(message.getStanzaId(),
                                                message.getBody(),
                                                contactName,
                                                roomName,
                                                getMessageTime(message.toXML()), "LABEL",
                                                "0", groupName, roomName);
                                        String finalContactName = contactName;
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                MessageChatModel messageSentChatModel1 = new MessageChatModel();

                                                messageSentChatModel1.setMessage_type("LABEL");
                                                messageSentChatModel1.setMessage(message.getBody());
                                                messageSentChatModel1.setMessage_time(getMessageTime(message.toXML()));
                                                messageSentChatModel1.setTo(roomName);
                                                messageSentChatModel1.setFrom(finalContactName);
                                                messageSentChatModel1.setAvatat_intial(finalContactName);
                                                if (!finalContactName.equalsIgnoreCase(nickname.toString())) {
                                                    messageChatModelList.add(messageSentChatModel1);
                                                }
                                                adapter.setList(messageChatModelList, ConversationActivity.this);
                                                if (messageChatModelList.size() != 0) {
                                                    recyclerView.smoothScrollToPosition(messageChatModelList.size() - 1);
                                                }
                                            }
                                        });
//                                        Intent intent = new Intent(ConversationActivity.this, NotifyService.class);
//                                        startService(intent);

                                    } else {
                                        messagesDao.insertMessages(message.getStanzaId(),
                                                message.getBody(),
                                                contactName,
                                                roomName,
                                                getMessageTime(message.toXML()), "SENT",
                                                "0", groupName, roomName);
                                        String finalContactName = contactName;
                                        runOnUiThread(() -> {
                                            MessageChatModel messageSentChatModel1 = new MessageChatModel();

                                            messageSentChatModel1.setMessage_type("SENT");
                                            messageSentChatModel1.setMessage(message.getBody());
                                            messageSentChatModel1.setMessage_time(getMessageTime(message.toXML()));
                                            messageSentChatModel1.setTo(roomName);
                                            messageSentChatModel1.setFrom(finalContactName);
                                            messageSentChatModel1.setAvatat_intial(finalContactName);
                                            if (!finalContactName.equalsIgnoreCase(nickname.toString())) {
                                                messageChatModelList.add(messageSentChatModel1);
                                            }
                                            adapter.setList(messageChatModelList, ConversationActivity.this);
                                            if (messageChatModelList.size() != 0) {
                                                recyclerView.smoothScrollToPosition(messageChatModelList.size() - 1);
                                            }
                                        });
                                    }

//                                        Intent intent = new Intent(ConversationActivity.this, NotifyService.class);
//                                        startService(intent);
                                } else if (label.equalsIgnoreCase("true")) {
                                    messagesDao.insertMessages(message.getStanzaId(),
                                            message.getBody(),
                                            contactName,
                                            roomName,
                                            getMessageTime(message.toXML()), "LABEL",
                                            "0", groupName, roomName);
                                    String finalContactName = contactName;
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            MessageChatModel messageSentChatModel1 = new MessageChatModel();

                                            messageSentChatModel1.setMessage_type("LABEL");
                                            messageSentChatModel1.setMessage(message.getBody());
                                            messageSentChatModel1.setMessage_time(getMessageTime(message.toXML()));
                                            messageSentChatModel1.setTo(roomName);
                                            messageSentChatModel1.setFrom(finalContactName);
                                            messageSentChatModel1.setAvatat_intial(finalContactName);
                                            if (!finalContactName.equalsIgnoreCase(nickname.toString())) {
                                                messageChatModelList.add(messageSentChatModel1);
                                            }
                                            adapter.setList(messageChatModelList, ConversationActivity.this);
                                            if (messageChatModelList.size() != 0) {
                                                recyclerView.smoothScrollToPosition(messageChatModelList.size() - 1);
                                            }
                                        }
                                    });
//                                        Intent intent = new Intent(ConversationActivity.this, NotifyService.class);
//                                        startService(intent);

                                } else {
                                    messagesDao.insertMessages(message.getStanzaId(),
                                            message.getBody(),
                                            contactName,
                                            roomName,
                                            getMessageTime(message.toXML()), "RECEIVED",
                                            "0", groupName, roomName);
                                    String finalContactName = contactName;
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            MessageChatModel messageSentChatModel1 = new MessageChatModel();

                                            messageSentChatModel1.setMessage_type("RECEIVED");
                                            messageSentChatModel1.setMessage(message.getBody());
                                            messageSentChatModel1.setMessage_time(getMessageTime(message.toXML()));
                                            messageSentChatModel1.setTo(roomName);
                                            messageSentChatModel1.setFrom(finalContactName);
                                            messageSentChatModel1.setAvatat_intial(finalContactName);
                                            if (!finalContactName.equalsIgnoreCase(nickname.toString())) {
                                                messageChatModelList.add(messageSentChatModel1);
                                            }
                                            adapter.setList(messageChatModelList, ConversationActivity.this);
                                            if (messageChatModelList.size() != 0) {
                                                recyclerView.smoothScrollToPosition(messageChatModelList.size() - 1);
                                            }

                                        }
                                    });
//                                        Intent intent = new Intent(ConversationActivity.this, NotifyService.class);
//                                        startService(intent);
                                }
                            }
                        } catch (OelpException e) {
                            e.printStackTrace();
                        }

                    }


                }

            }
        });
        PingManager pingManager = PingManager.getInstanceFor(connection);
        pingManager.setPingInterval(300);

        ReconnectionManager reconnectionManager = null;
        reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        ReconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();


        changeReadStatus();
        pingServer();
    }


    public void getRetrivedMessages() {
        MessagesDao messagesDao = new MessagesDao();
        List<MessagesDTO> messagesAllDTOList = new ArrayList<>();
        try {
            messagesAllDTOList = messagesDao.getAllMessages(roomName);
        } catch (OelpException e) {
            e.printStackTrace();
        }
        MessageChatModel messageSingleModel = new MessageChatModel();
        messageChatModelList = new ArrayList<>();
        for (MessagesDTO m : messagesAllDTOList) {
            messageSingleModel = new MessageChatModel();
            messageSingleModel.setAvatat_intial(nickname.toString());
            messageSingleModel.setFrom(m.getFrom_user());
            messageSingleModel.setTo(m.getTo_user());
            messageSingleModel.setMessage(m.getMessage());
            messageSingleModel.setMessage_time(m.getMessage_date());
            messageSingleModel.setMessage_type(m.getMessage_type());
            messageChatModelList.add(messageSingleModel);
        }
        adapter.setList(messageChatModelList, this);
        if (messageChatModelList.size() != 0) {
            recyclerView.scrollToPosition(messageChatModelList.size() - 1);
        }
        adapter.notifyDataSetChanged();

    }

    public void getRetrivedMessagesOnDate() {
        MessagesDao messagesDao = new MessagesDao();
        List<MessagesDTO> messagesAllDTOList = new ArrayList<>();
        try {
            String datetime = messagesDao.userDateTime(mySharedPref.readString(Constants.USER_ID, ""));
            messagesAllDTOList = messagesDao.getAllMessagesFromTime(roomName, datetime);
        } catch (OelpException e) {
            e.printStackTrace();
        }
        MessageChatModel messageSingleModel = new MessageChatModel();
        messageChatModelList = new ArrayList<>();
        for (MessagesDTO m : messagesAllDTOList) {
            messageSingleModel = new MessageChatModel();
            messageSingleModel.setAvatat_intial(nickname.toString());
            messageSingleModel.setFrom(m.getFrom_user());
            messageSingleModel.setTo(m.getTo_user());
            messageSingleModel.setMessage(m.getMessage());
            messageSingleModel.setMessage_time(m.getMessage_date());
            messageSingleModel.setMessage_type(m.getMessage_type());
            messageChatModelList.add(messageSingleModel);
        }
        adapter.setList(messageChatModelList, this);
        if (messageChatModelList.size() != 0) {
            recyclerView.scrollToPosition(messageChatModelList.size() - 1);
        }
        adapter.notifyDataSetChanged();

    }

    public void leaveMuc() {
        Logger.logD(TAG, "leaving group to :" + roomName);
        manager = MultiUserChatManager.getInstanceFor(connection);
        try {
//            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName);
            muc = manager.getMultiUserChat(mucJid);
            if (muc.isJoined())
                muc.leave();
        } catch (SmackException.NotConnectedException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        if (userType == Constants.USER_MASTER) {
//            leaveMuc();
//        }
        ConversationActivity.this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void oldMessages() {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();

        manager = MultiUserChatManager.getInstanceFor(connection);

        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName);
            muc = manager.getMultiUserChat(mucJid);
            manager.setAutoJoinOnReconnect(true);
            muc.addMessageListener(new MessageListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void processMessage(Message message) {
                    String generator = message.getBody("generator");
                    String label = message.getBody("label");
//                    Logger.logD(TAG, "message.getBody() :" + message.getBody());
//                    Logger.logD(TAG, "message.getFrom() :" + message.getFrom());

                    String from = message.getFrom().toString();
//                    Logger.logD(TAG, "message.getFrom():" + from + "to" + roomName);


                    String contactJid = "";
                    String contactName = "";
                    if (from.contains("/")) {
                        contactJid = from.split("/")[0];
                        contactName = from.split("/")[1];
                        Logger.logD(TAG, "The real jid is :" + contactJid);
                        Logger.logD(TAG, "The message is from :" + from);
                    } else {
                        contactJid = from;
                    }
                    if (message != null) {
                        if (message.getBody() != null && !message.getBody().equalsIgnoreCase("")) {
                            try {
                                if (!messagesDao.isGeneratorExists(generator)) {
                                    if (contactName.equalsIgnoreCase(nickname.toString())) {
                                        if (label.equalsIgnoreCase("true")) {
                                            messagesDao.insertMessages(message.getStanzaId(),
                                                    message.getBody(),
                                                    contactName,
                                                    roomName,
                                                    getMessageTime(message.toXML()), "LABEL",
                                                    "0", groupName, roomName);

                                        } else {
                                            messagesDao.insertMessages(message.getStanzaId(),
                                                    message.getBody(),
                                                    contactName,
                                                    roomName,
                                                    getMessageTime(message.toXML()), "SENT",
                                                    "0", groupName, roomName);
                                        }
                                    } else if (label.equalsIgnoreCase("true")) {
                                        messagesDao.insertMessages(message.getStanzaId(),
                                                message.getBody(),
                                                contactName,
                                                roomName,
                                                getMessageTime(message.toXML()), "LABEL",
                                                "0", groupName, roomName);

                                    } else {
                                        messagesDao.insertMessages(message.getStanzaId(),
                                                message.getBody(),
                                                contactName,
                                                roomName,
                                                getMessageTime(message.toXML()), "RECEIVED",
                                                "0", groupName, roomName);
                                    }
                                }
                            } catch (OelpException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            });
        } catch (XmppStringprepException e1) {
            e1.printStackTrace();
        }
        mySharedPref.writeInt("oldmessagesfetched", 1);
    }

    //    public void gettingHistoryMessages(){
//        if (connection == null)
//            connection = connectionUtils.getXmptcConnection();
//
//        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
//
//        try {
//            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName);
//            MultiUserChat muc = manager.getMultiUserChat(mucJid);
//            if(!muc.isJoined()){
//                muc.join(nickname);
//            }
//            Message message;
//            muc.addMessageListener(new MessageListener() {
//                @Override
//                public void processMessage(Message message) {
//                   Logger.logD(TAG, "message.getBody() :" + message.getBody());
//                   Logger.logD(TAG, "message.getFrom() :" + message.getFrom());
//
//                    String from = message.getFrom().toString();
//                   Logger.logD(TAG, "message.getFrom():" + from + "to" + roomName);
//
//
//                    String contactJid = "";
//                    String contactName = "";
//                    if (from.contains("/")) {
//                        contactJid = from.split("/")[0];
//                        contactName = from.split("/")[1];
//                       Logger.logD(TAG, "The real jid is :" + contactJid);
//                       Logger.logD(TAG, "The message is from :" + from);
//                    } else {
//                        contactJid = from;
//                    }
//
//                    if (message != null) {
//                        if (message.getBody() != null && !message.getBody().equalsIgnoreCase("")) {
//                            try {
//                                if (!messagesDao.isGeneratorExists(getMessagegeneratoruuid(message.toXML()))) {
//                                    messagesDao.insertMessages(message.getStanzaId(),
//                                            message.getBody(),
//                                            contactName,
//                                            roomName,
//                                            getMessageTime(message.toXML()), "RECEIVED",
//                                            "0", groupName, roomName);
//                                }
//                            } catch (OelpException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//
//
//                    }
//
//                }
//            });
//        } catch (
//                XmppStringprepException e1) {
//            e1.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (XMPPException.XMPPErrorException e) {
//            e.printStackTrace();
//        } catch (SmackException.NotConnectedException e) {
//            e.printStackTrace();
//        } catch (SmackException.NoResponseException e) {
//            e.printStackTrace();
//        } catch (MultiUserChatException.NotAMucServiceException e) {
//            e.printStackTrace();
//        }
//        ReconnectionManager reconnectionManager = null;
//        reconnectionManager = ReconnectionManager.getInstanceFor(connection);
//        ReconnectionManager.setEnabledPerDefault(true);
//        reconnectionManager.enableAutomaticReconnection();
//    }
    public void handleOfflineMessages(XMPPConnection connection) {
        OfflineMessageManager offlineMessageManager = new OfflineMessageManager(connection);

        try {
            if (!offlineMessageManager.supportsFlexibleRetrieval()) {
                Logger.logD(TAG, "Offline messages not supported");
            } else {
                offlineMessageManager.getMessages();
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

        try {
            if (offlineMessageManager.getMessageCount() == 0) {
                Logger.logD(TAG, "No offline messages found on server");
            } else {
                List<Message> msgs = offlineMessageManager.getMessages();
                for (Message msg : msgs) {
                    String fullJid = msg.getFrom().toString();
                    String bareJid = JidCreate.bareFrom(fullJid).toString();
                    String messageBody = msg.getBody();
                    if (messageBody != null) {
                        Logger.logD(TAG, "Retrieved offline message from " + messageBody);
                    }
                }
                offlineMessageManager.deleteMessages();
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

    public int getUserStatus() {
        Roster roster = Roster.getInstanceFor(connection);
        Presence availability = null;
        try {
            availability = roster.getPresence(JidCreate.bareFrom(nickname.toString()));
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
        } else if (userMode == Presence.Mode.available) {
            userState = 4;
        }
        return userState;
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

    public void logoff() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Presence presence = new Presence(Presence.Type.unavailable);
                    presence.setMode(Presence.Mode.away);
                    presence.setTo(JidCreate.domainBareFrom(HOST));
                    connection.sendStanza(presence);

                    reconnectionManager.disableAutomaticReconnection();
                    connection.disconnect();
//                    stopService(getApplication());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    clearInstance();
                }
            }
        }).start();
    }

    public void sendPresenceAvailable() {
        if (connection.isAuthenticated()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Presence presence = new Presence(JidCreate.domainBareFrom(HOST), Presence.Type.available);
                        presence.setMode(Presence.Mode.available);
                        connection.sendStanza(presence);
                        Logger.logD(TAG, "sent presence");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

    }

    /**
     * {@inheritDoc}
     */
//    @Override
//    public boolean isAvailable(String username) {
//        if (connection.isConnected()) {
//            Roster roster = connection.getRoster();
//            if (roster != null) {
//                Presence presence = roster.getPresence(username + XMPPPatternUtils.getUserSuffix());
//                return presence.isAvailable();
//            } else {
//                Logger.logD(TAG,"XMPP connection did not return a roster for communote bot");
//            }
//        } else {
//            Logger.logD(TAG,"Cannot check availability of user because XMPP connector is disconnected");
//        }
//        return false;
//    }
//


    public class ShowDialogAsyncTask extends AsyncTask<Void, Integer, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            oldMessages();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd != null) {
                pd.dismiss();
            }
        }
    }

    public void stableConnection() {

        connection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {

            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {

            }

            @Override
            public void connectionClosed() {
                connectionUtils.getconnected(ConversationActivity.this);
                ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection();

            }

            @Override
            public void connectionClosedOnError(Exception e) {
                connectionUtils.getconnected(ConversationActivity.this);
                ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection();
            }

            @Override
            public void reconnectionSuccessful() {

            }

            @Override
            public void reconnectingIn(int seconds) {

            }

            @Override
            public void reconnectionFailed(Exception e) {
                connectionUtils.getconnected(ConversationActivity.this);
                ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection();
            }
        });
    }
}












