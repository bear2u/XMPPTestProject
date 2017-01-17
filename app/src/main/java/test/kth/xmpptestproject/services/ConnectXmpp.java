package test.kth.xmpptestproject.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import test.kth.xmpptestproject.utils.Logger;


public class ConnectXmpp extends Service {

    private String userName;
    private String passWord;
    private MyXMPP xmpp = MyXMPP.getInstance();

    public ConnectXmpp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new LocalBinder<ConnectXmpp>(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Logger.log("onStartCommand start");
            if(intent != null){
            userName = intent.getStringExtra("user");
            passWord = intent.getStringExtra("pwd");
            xmpp.init(this , userName, passWord);
            xmpp.connectConnection();
                Logger.log("onStartCommand end");

        }

    return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Logger.log("service onDestoryed");
        xmpp.disconnectConnection();
        super.onDestroy();
    }









}
