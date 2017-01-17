package test.kth.xmpptestproject;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.kth.xmpptestproject.services.MyXMPP;
import test.kth.xmpptestproject.utils.XmppApi;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.txtUser)
    EditText txtUser;

    @BindView(R.id.txtPwd)
    EditText txtPwd;

    MyXMPP myXMPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


//        new TedPermission(this)
//                .setPermissionListener(permissionlistener)
//                .setRationaleMessage("권한설정필요합니다.")
//                .setDeniedMessage("왜 거부함?? 웃김..")
//                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .check();

//        XmppApi xmppApi = new XmppApi(this);
//        xmppApi.getUsers();

    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public void onClickLoginBtn(View view){
        Intent intent = new Intent(this , FriendListActivity.class);
        intent.putExtra("userId" , txtUser.getText().toString());
        intent.putExtra("userPwd" , txtPwd.getText().toString());
        startActivity(intent);
        finish();
    }
}
