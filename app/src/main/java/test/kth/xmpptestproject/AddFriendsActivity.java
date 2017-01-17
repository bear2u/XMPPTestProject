package test.kth.xmpptestproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import test.kth.xmpptestproject.services.MyXMPP;

public class AddFriendsActivity extends AppCompatActivity {
    MyXMPP myXMPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);

        myXMPP = MyXMPP.getInstance();
    }
}
