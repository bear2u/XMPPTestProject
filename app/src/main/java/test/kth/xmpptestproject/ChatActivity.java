package test.kth.xmpptestproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.devcenter.androiduilibrary.ChatView;
import co.devcenter.androiduilibrary.SendButton;
import co.devcenter.androiduilibrary.models.ChatMessage;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import test.kth.xmpptestproject.common.RxEventBus;
import test.kth.xmpptestproject.common.RxEventBusProvider;
import test.kth.xmpptestproject.datas.ChatData;
import test.kth.xmpptestproject.datas.Friends;
import test.kth.xmpptestproject.services.MyXMPP;
import test.kth.xmpptestproject.utils.Logger;

public class ChatActivity extends AppCompatActivity {
    List<ChatData> mChatDataList = new ArrayList<ChatData>();
    ChatAdapter_v2 chatAdapter;
//    @BindView(R.id.listView)
//    RecyclerView recyclerView;
//
//    @BindView(R.id.et_content)
//    EditText etContent;
//
//    @BindView(R.id.btnSend)
//    Button btnSend;

    Friends friends = null;
    MyXMPP myXMPP;

    @BindView(R.id.chat_view)
    ChatView chatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        myXMPP = MyXMPP.getInstance();



//        chatAdapter = new ChatAdapter_v2(ChatActivity.this, mChatDataList);
//        chatAdapter.setHasStableIds(true);
//        LinearLayoutManager lm = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(lm);
//        recyclerView.setAdapter( chatAdapter );
//        recyclerView.getItemAnimator().endAnimations();

        friends = getIntent().getExtras().getParcelable("data");
        if (friends != null)
            Logger.log("friend data -> " + friends);

        setTitle(friends.getUser());

        if(friends.getUser().startsWith("test2@")) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    myXMPP.createGroup(friends.getUser(), "test2@tommy");
                }
            });
            thread.start();
        }


        registerPush();

        SendButton sendButton = chatView.getSendButton();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log("typed String -> " + chatView.getTypedString());
                ChatMessage chatMessage = new ChatMessage(chatView.getTypedString(), System.currentTimeMillis(), ChatMessage.Status.SENT);
                myXMPP.sendGroupMsg(friends.getUser() + "/Android", chatView.getTypedString());

                chatView.sendMessage(chatMessage);
            }
        });

    }

    public void send() {

    }

    RxEventBus rxEventBus;

    public void registerPush() {
        rxEventBus = RxEventBusProvider.provide();
        rxEventBus.onEventOnMain(ChatMessage.class, new Action1<ChatMessage>() {
            @Override
            public void call(ChatMessage chatData) {
                Logger.log("#4200 chatData -> " + chatData.toString());
                chatView.sendMessage(chatData);
            }
        });
    }

    ;

    @Override
    protected void onDestroy() {
        rxEventBus.unregister();
        super.onDestroy();
    }
}
