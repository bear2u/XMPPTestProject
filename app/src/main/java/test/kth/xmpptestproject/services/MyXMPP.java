package test.kth.xmpptestproject.services;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import co.devcenter.androiduilibrary.models.ChatMessage;
import test.kth.xmpptestproject.common.RxEventBusProvider;
import test.kth.xmpptestproject.datas.ChatData;
import test.kth.xmpptestproject.datas.Friends;
import test.kth.xmpptestproject.utils.Logger;

import static org.jivesoftware.smackx.privacy.packet.PrivacyItem.Type.jid;
import static org.jivesoftware.smackx.pubsub.ChildrenAssociationPolicy.owners;

/**
 * Presence 종류
 available - (기본값)는 사용자가 메시지를 수신 할 수 있음을 나타냅니다.
 unavailable - 사용자는 메시지를 수신 할 수 없습니다.
 subscribe - 수신자의 현재 상태에 대한 가입을 요청합니다.
 subscribed - 발신자의 현재 상태에 대한 가입을 승인합니다.
 unsubscribe - 발신자의 현재 상태에 대한 가입 제거 요청.
 unsubscribed - 발신자의 현재 상태에 대한 가입을 제거합니다.
 error - 존재 스 D 자 (/ packet)에 오류 메시지가 있습니다.
 */
public class MyXMPP implements ConnectionListener, ChatManagerListener, RosterListener, ChatMessageListener, PingFailedListener {

    private static final String DOMAIN = "tommy";
    private static final String HOST = "192.168.0.2";
    private static final int PORT = 5222;
    private String userName ="";
    private String passWord = "";
    AbstractXMPPConnection connection ;
    ChatManager chatmanager ;
    Chat newChat;
    XMPPConnectionListener connectionListener = new XMPPConnectionListener();
    private boolean connected;
    private boolean isToasted;
    private boolean chat_created;
    private boolean loggedin;
    Context mContext;

    private static MyXMPP instance;
    private MyXMPP(){};

    public static synchronized MyXMPP getInstance(){
        if(instance == null){
            instance = new MyXMPP();
        }

        return instance;
    }


    //Initialize
    public void init(Context context , String userId, String pwd ) {
        Log.i("XMPP", "Initializing!");
        this.mContext = context;
        this.userName = userId;
        this.passWord = pwd;
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(userName, passWord);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setResource("Android");
        configBuilder.setServiceName(DOMAIN);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        configBuilder.setDebuggerEnabled(true);

        SmackConfiguration.setDefaultPacketReplyTimeout(10000);
        //SmackConfiguration.addDisabledSmackClass(Socks5BytestreamManager.class);
        //configBuilder.setDebuggerEnabled(true);
        connection = new XMPPTCPConnection(configBuilder.build());
        connection.addConnectionListener(connectionListener);
//        connection.addPacketListener(new StanzaListener() {
//            @Override
//            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
//
//            }
//        });

        ChatManager.getInstanceFor(connection).addChatListener(this);

        MultiUserChatManager.getInstanceFor(connection).addInvitationListener(new GroupChatListener());

    }

    // Disconnect Function
    public void disconnectConnection(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
    }

    public void connectConnection()
    {
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... arg0) {

                Logger.log("connect xmpp donInBackground~~");
                // Create a connection
                try {
                    connection.connect();
//                    login();
                    connected = true;

//                    sendMsg();
//                    createNewAccount("test4" , "1234");

                } catch (Exception ex){
                    ex.printStackTrace();
                }

                return null;
            }
        };
        connectionThread.execute();
    }

    public void sendMsg(String sender , String msg) {
        if (connection.isConnected()== true) {
            // Assume we've created an XMPPConnection name "connection"._
            chatmanager = ChatManager.getInstanceFor(connection);
            newChat = chatmanager.createChat(sender , this);

            try {
                Logger.log("#840 msg -> " + sender + " >>> " + msg);
                newChat.sendMessage( msg );
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendGroupMsg(String jid , String msg){
        Message message = new Message("ddd@conference.tommy" , Message.Type.groupchat  );
        message.setBody(msg);
        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat("ddd@conference.tommy");
        try {
            multiUserChat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }





    public void createGroup(String jid , String inviter){

        // Get the MultiUserChatManager
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

// Get a MultiUserChat using MultiUserChatManager
        MultiUserChat muc = manager.getMultiUserChat("ddd@conference.tommy");

// Create the room
        try {
            List<String> serviceNames = manager.getServiceNames();
            Logger.log("servicenames -> " + serviceNames);
            muc.create("test");
            muc.sendConfigurationForm(new Form(DataForm.Type.submit));

            muc.invite("test2@tommy/Android" , "~~~~~ test2 How r u ??");
            muc.invite("test4@tommy/Android" , "~~~~~ test4 How r u ??");

            muc.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    ChatMessage chatMessage = new ChatMessage(message.getBody() , System.currentTimeMillis() , ChatMessage.Status.RECEIVED);
                    RxEventBusProvider.provide().post(chatMessage);
                }
            });


        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }

// Send an empty room configuration form which indicates that we want
// an instant room


    }

    class GroupChatListener implements InvitationListener {

        @Override
        public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
            Logger.log(" Entered invitation handler... ");
            try
            {
//                MultiUserChatManager mcm = MultiUserChatManager.getInstanceFor(connection);
//                mcm.getMultiUserChat(inviter);
//                MultiUserChat chatRoom = new MultiUserChat(room, room);

                room.join("ddd");
                room.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(Message message) {
                        ChatMessage chatMessage = new ChatMessage(message.getBody() , System.currentTimeMillis() , ChatMessage.Status.RECEIVED);
                        RxEventBusProvider.provide().post(chatMessage);
                    }
                });

            }
            catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e)
            {
                e.printStackTrace();
            } catch (SmackException e)
            {
                e.printStackTrace();
            }
            Logger.log(" Invitation Accepted... ");
        }
    }

//    private void setConfig(MultiUserChat multiUserChat) {
//        try {
//            Form form = multiUserChat.getConfigurationForm();
//            Form submitForm = form.createAnswerForm();
//            for (Iterator<FormField> fields = (Iterator<FormField>) submitForm.getFields(); fields.hasNext();) {
//                FormField field = (FormField) fields.next();
//                if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
//                    submitForm.setDefaultAnswer(field.getVariable());
//                }
//            }
//            submitForm.setAnswer("muc#roomconfig_publicroom", true);
//            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
//            multiUserChat.sendConfigurationForm(submitForm);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public ArrayList<Friends> getFriendList() {

        ArrayList<Friends> usersList=new ArrayList<Friends>();

        Presence presence = new Presence(Presence.Type.available);
        try {
            connection.sendPacket(presence);

            final Roster roster = Roster.getInstanceFor(connection);
            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            Collection<RosterEntry> entries = roster.getEntries();

            for (RosterEntry entry : entries) {

                HashMap<String, String> map = new HashMap<String, String>();
                Presence entryPresence = roster.getPresence(entry.getUser());

                Presence.Type type = entryPresence.getType();

                Friends friends = new Friends();
                friends.setName( entry.getName());
                friends.setUser(entry.getUser());
                friends.setType(type.toString());

                usersList.add(friends);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        setConnection(Constants.connection);

        return usersList;
    }


    protected void addFriend(String address, String name, String[] groups){

        Roster roster = Roster.getInstanceFor(connection);

        try {
            Presence response = new Presence(Presence.Type.subscribed);
            response.setTo(address);

//            response.setTo(presence.getFrom());
            response.setType(Presence.Type.subscribed);
            response.setFrom("current_logged_in_user");
            connection.sendPacket(response);

            roster.createEntry(address, name, groups);

//            // If contact exists locally, don't create another copy
//            Contact contact = makeContact(name, address);
//            if (!containsContact(contact))
//                notifyContactListUpdated(list,
//                        ContactListListener.LIST_CONTACT_ADDED, contact);
//            else
//                debug(TAG, "skip adding existing contact locally " + name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void addFriend2(String address, String name, String[] groups){

        Roster roster = Roster.getInstanceFor(connection);
        roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

        try {
//            Presence response = new Presence(Presence.Type.subscribed);
//            response.setTo(address);
//
////            response.setTo(presence.getFrom());
//            response.setType(Presence.Type.subscribe);
//            response.setFrom(userName);
//            connection.sendPacket(response);

            roster.createEntry(address, name, groups);

//            // If contact exists locally, don't create another copy
//            Contact contact = makeContact(name, address);
//            if (!containsContact(contact))
//                notifyContactListUpdated(list,
//                        ContactListListener.LIST_CONTACT_ADDED, contact);
//            else
//                debug(TAG, "skip adding existing contact locally " + name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createChat(String jid){

    }

    public void login() {

        try {
            Logger.log("#123 login do");
            connection.login(userName, passWord);
            //Log.i("LOGIN", "Yey! We're connected to the Xmpp server!");

        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
            Log.d("KTH" , connection.getUser());
        } catch (Exception e) {
        }
    }

    public boolean createNewAccount(String username, String newpassword) {
        boolean status = false;
        if (connection == null) {
            try {
                connection.connect();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }

        try {
            String newusername = username + connection.getServiceName();
            Log.i("service", connection.getServiceName());
            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.createAccount(username, newpassword);
            status = true;
        } catch (SmackException.NoResponseException e) {
            status = false;
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            status = false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            status = false;
        }
//        connection.disconnect();
        return status;

    }

    public void checkMyStatus(){
        try {
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            final Presence.Type presenceType = presence.getType();
            final String fromId = presence.getFrom();
            Roster roster = Roster.getInstanceFor(connection);
            final RosterEntry newEntry = roster.getEntry(fromId);

            if (presenceType == Presence.Type.subscribe)
            {
                //from new user
                if (newEntry == null)
                {
                    //save request locally for later accept/reject
                    //later accept will send back a subscribe & subscribed presence to user with fromId
                    //or accept immediately by sending back subscribe and unsubscribed right now
                }
                //from a user that previously accepted your request
                else
                {
                    //send back subscribed presence to user with fromId
                }
            }

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void connected(XMPPConnection connection) {
        Logger.log("#888 connected");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Logger.log("#888 authenticated");
    }

    @Override
    public void connectionClosed() {
        Logger.log("#888 connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Logger.log("#888 connectionClosedOnError");
    }

    @Override
    public void reconnectionSuccessful() {
        Logger.log("#888 reconnectionSuccessful");
    }

    @Override
    public void reconnectingIn(int seconds) {
        Logger.log("#888 authenticated");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Logger.log("#888 reconnectionFailed");
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        Logger.log("#888 chatCreated");
        chat.addMessageListener(this);
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        //gson = new Gson();
        Logger.log("#888 processMessage()");
        if (message.getType().equals(Message.Type.chat) || message.getType().equals(Message.Type.normal)) {
            Log.i("MyXMPP_MESSAGE_LISTENER", "Xmpp message received: '"
                    + message);
            if (message.getType() == Message.Type.chat
                    && message.getBody() != null) {
                String sender1 = message.getFrom();
                final Random random = new Random();
                final String delimiter = "\\@";
                String[] temp = sender1.split(delimiter);
                final String sender = temp[0];
                Logger.log("receive msg -> " + message.getBody());
                final String msg = message.getBody().toString();

//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(mContext , msg  , Toast.LENGTH_SHORT).show();
//                    }
//                });
                //final ChatMessage chatMessage = gson.fromJson(message.getBody(), ChatMessage.class);
                //chatMessage.msgid = " " + random.nextInt(1000);
                //processMessage(sender, chatMessage);


                ChatMessage chatMessage = new ChatMessage(msg , System.currentTimeMillis() , ChatMessage.Status.RECEIVED);
                RxEventBusProvider.provide().post(chatMessage);
            }
        }
    }

    @Override
    public void entriesAdded(Collection<String> addresses) {
        Logger.log("#888 entriesAdded");
    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {
        Logger.log("#888 entriesUpdated");
    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {
        Logger.log("#888 entriesDeleted");
    }

    @Override
    public void presenceChanged(Presence presence) {
        Logger.log("#888 presenceChanged");
    }

    @Override
    public void pingFailed() {
        Logger.log("#888 pingFailed");
    }


    //Connection Listener to check connection state
    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {
            Logger.log("#888 connected");

            connected = true;
            if (!connection.isAuthenticated()) {
                Logger.log("xmpp login try~~");
                login();
//                addFriend2("test2@tommy/Android" , null , null);
//                addFriend("test3" ,null , null);
//
//                addFriend("test2" , "test3" , new String[]{"test"});
//                ArrayList list = getFriendList();
//                Logger.log("#123 #222 friend list -> " + list.size());
            }
        }

        @Override
        public void connectionClosed() {
            Logger.log("#888 connectionClosed");

            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub


                    }
                });
            Log.d("xmpp", "ConnectionCLosed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            Logger.log("#888 connectionClosedOnError");
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                    }
                });
            Log.d("xmpp", "ConnectionClosedOn Error!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {
            Logger.log("#888 reconnectingIn");
            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            Logger.log("#888 reconnectionFailed");
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {



                    }
                });
            Log.d("xmpp", "ReconnectionFailed!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            Logger.log("#888 reconnectionSuccessful");
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub



                    }
                });
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Logger.log("#888 #2222 authenticated");
            Log.d("xmpp", "Authenticated!");
            loggedin = true;

            chat_created = false;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub



                    }
                });
        }
    }

    public void disconnect() {
        Log.i("KTH", "disconnect()");
        if (connection != null) {
            connection.disconnect();
        }
    }

    public boolean userExists(XMPPConnection mXMPPConnection, String jid) {
        //ProviderManager.getInstance().addIQProvider("query","jabber:iq:search", new UserSearchManager().Provider());
        ProviderManager.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
        ProviderManager.addIQProvider("query", "jabber:iq:vjud", new UserSearch.Provider());
        UserSearchManager searchManager = new UserSearchManager(mXMPPConnection);
        try {

            List<String> services = searchManager.getSearchServices();
            if (services == null || services.size() < 1)
                return false;
            for (String service : services) {
                Log.e("SERVICE", service);
            }
            Form searchForm;
            try {
                searchForm = searchManager.getSearchForm(services.get(0));
                Form answerForm = searchForm.createAnswerForm();
                try {
                    answerForm.setAnswer("user", jid);
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                    return false;
                }
                //answerForm.setAnswer("search", jid);
                ReportedData data;
                try {
                    data = searchManager.getSearchResults(answerForm, services.get(0));
                    Log.e("HERE\t", "1");
                    if (data.getRows() != null) {
                        List<ReportedData.Row> rowList = data.getRows();
                        if (rowList.size() > 0) {
                            Log.e("HERE\t", "2");
                            return true;
                        } else
                            return false;

                    }
                } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    return false;
                }


            } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
                e.printStackTrace();
                return false;
            }

        } catch (SmackException.NoResponseException | SmackException.NotConnectedException | XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

}
