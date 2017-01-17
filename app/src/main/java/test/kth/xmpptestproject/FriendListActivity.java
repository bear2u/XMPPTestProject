package test.kth.xmpptestproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import test.kth.xmpptestproject.datas.Friends;
import test.kth.xmpptestproject.services.ConnectXmpp;
import test.kth.xmpptestproject.services.MyXMPP;
import test.kth.xmpptestproject.utils.Logger;


public class FriendListActivity extends AppCompatActivity {
    private ConnectXmpp mService;
    private boolean mBounded;

    @BindView(R.id.list)
    RecyclerView recycleListView;
    MyAdpater adpater;

    List<Friends> friendsList = new ArrayList<Friends>();

    MyXMPP xmpp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        ButterKnife.bind(this);

        adpater = new MyAdpater(this, friendsList);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        recycleListView.setLayoutManager(lm);
        recycleListView.setAdapter(adpater);

        startXMPP();

//        startService();
        //test@tommy:5222/Android
    }

    private void startXMPP() {

        String userId = getIntent().getStringExtra("userId");
        String userPwd = getIntent().getStringExtra("userPwd");

        Observable.just(userId, userPwd).toList().map(new Func1<List<String>, Object>() {

            @Override
            public Object call(List<String> datas) {
                xmpp = MyXMPP.getInstance();
                if (datas.size() > 1) {
                    xmpp.init(FriendListActivity.this, datas.get(0), datas.get(1));
                    xmpp.connectConnection();
                }
                return null;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).toCompletable().subscribe(
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        Logger.log("#710 start xmpp");
//                        setData();
                    }
                });
    }

    public void startService() {
        String userId = getIntent().getStringExtra("userId");
        String userPwd = getIntent().getStringExtra("userPwd");

        try {
            Logger.log("회원정보 -> " + userId + "," + userPwd);
            Intent intent = new Intent(getBaseContext(), ConnectXmpp.class);
            intent.putExtra("user", userId);
            intent.putExtra("pwd", userPwd);
            startService(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setData() {
        friendsList.clear();
        List<Friends> list = xmpp.getFriendList();
        Logger.log("#4200 friend list -> " + list.size());
        for(Friends friends : list){
            friendsList.add(friends);
        }

        adpater.notifyDataSetChanged();
    }

    @OnClick(R.id.btnSearch)
    public void findFriend() {
        setData();
    }

    public void startChatting(View view){
        int pos = recycleListView.getChildLayoutPosition(view);
        Logger.log("clicked friends data -> " + friendsList.get(pos).toString());
        Friends friends = friendsList.get(pos);
        Intent intent = new Intent(this , ChatActivity.class);
        intent.putExtra("data" , friends);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (xmpp != null) {
            xmpp.disconnect();
        }
        super.onDestroy();
    }

    class MyAdpater extends RecyclerView.Adapter<MyAdpater.ViewHolder> {
        private Context context;
        private List<Friends> mItems;

        // Allows to remember the last item shown on screen
        private int lastPosition = -1;

        public MyAdpater(Context mContext, List<Friends> items) {
            context = mContext;
            mItems = items;
        }

        // 필수로 Generate 되어야 하는 메소드 1 : 새로운 뷰 생성
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // 새로운 뷰를 만든다
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startChatting(view);
                }
            });
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        // 필수로 Generate 되어야 하는 메소드 2 : ListView의 getView 부분을 담당하는 메소드
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.textView.setText(mItems.get(position).getUser());
//            holder.imageView.setImageResource(mItems.get(position).image);
//            setAnimation(holder.imageView, position);
        }

        // // 필수로 Generate 되어야 하는 메소드 3
        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;

            public ViewHolder(View view) {
                super(view);
//                imageView = (ImageView) view.findViewById(R.id.image);
                textView = (TextView) view.findViewById(android.R.id.text1);
            }
        }

        private void setAnimation(View viewToAnimate, int position) {
            // 새로 보여지는 뷰라면 애니메이션을 해줍니다
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

    }

}
