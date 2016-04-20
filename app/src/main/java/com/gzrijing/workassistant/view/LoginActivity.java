package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.db.ServiceOpenAndClose;
import com.gzrijing.workassistant.entity.User;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.MD5Encryptor;
import com.gzrijing.workassistant.util.ToastUtil;
import com.igexin.sdk.PushManager;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.litepal.crud.DataSupport;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String user;
    private String pwd = "";
    private EditText et_user;
    private EditText et_pwd;
    private Button btn_login;
    private ImageView iv_user;
    private ImageView iv_pwd;
    private ImageView iv_memory;
    private boolean flag;
    private String isMemory; // isMemory变量用来判断SharedPreferences有没有数据
    private String file = "saveUserAndPwd";// 用于保存账号密码的SharedPreferences的文件
    private SharedPreferences sp; // 声明一个SharedPreferences
    private String clientId;
    private ProgressDialog pDialog;
    private PushManager pushManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initData();
        setListeners();
    }


    private void initData() {
        //初始化推送服务
        pushManager = PushManager.getInstance();
        pushManager.initialize(getApplicationContext());
        ServiceOpenAndClose serviceOpenAndClose = DataSupport.find(ServiceOpenAndClose.class, 1);
        if (serviceOpenAndClose == null) {
            ServiceOpenAndClose service = new ServiceOpenAndClose();
            service.setPushService("1");
            service.save();
        } else {
            ContentValues values = new ContentValues();
            values.put("pushService", "1");
            DataSupport.update(ServiceOpenAndClose.class, values, 1);
        }
        clientId = pushManager.getClientid(getApplicationContext());

        getUserNamePwd();
    }

    private void getUserNamePwd() {
        sp = getSharedPreferences(file, MODE_PRIVATE);
        isMemory = sp.getString("isMemory", "no");
        // 进入界面时，这个if用来判断SharedPreferences里面user和pwd有没有数据，
        // 有的话则直接打在EditText上面
        if (isMemory.equals("yes")) {
            user = sp.getString("user", "");
            pwd = sp.getString("pwd", "");
            et_user.setText(user);
            et_pwd.setText(pwd);
            iv_memory.setImageResource(R.drawable.login_checkbox_on);
            flag = true;
        }
    }

    private void initViews() {
        et_user = (EditText) findViewById(R.id.login_user_et);
        iv_user = (ImageView) findViewById(R.id.login_user_iv);
        et_pwd = (EditText) findViewById(R.id.login_password_et);
        iv_pwd = (ImageView) findViewById(R.id.login_password_iv);
        btn_login = (Button) findViewById(R.id.login_login_btn);
        iv_memory = (ImageView) findViewById(R.id.login_checkbox_iv);
    }

    private void setListeners() {
        btn_login.setOnClickListener(this);
        iv_memory.setOnClickListener(this);

        et_user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    iv_user.setImageResource(R.drawable.login_user_on);
                } else {
                    iv_user.setImageResource(R.drawable.login_user_off);
                }
            }
        });

        et_pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    iv_pwd.setImageResource(R.drawable.login_pwd_on);
                } else {
                    iv_pwd.setImageResource(R.drawable.login_pwd_off);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login_btn:
                login();
                break;

            case R.id.login_checkbox_iv:
                if (flag) {
                    iv_memory.setImageResource(R.drawable.login_checkbox_off);
                } else {
                    iv_memory.setImageResource(R.drawable.login_checkbox_on);
                }
                flag = !flag;
                break;
        }
    }


    private void login() {
        if (clientId == null) {
            clientId = pushManager.getClientid(getApplicationContext());
            if(clientId == null){
                ToastUtil.showToast(LoginActivity.this, "网络延迟，请重新登录", Toast.LENGTH_LONG);
                return;
            }
        }
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在登陆...");
        pDialog.show();
        String userName = et_user.getText().toString().trim();
        String password = et_pwd.getText().toString().trim().toLowerCase();
        if (password.equals(pwd)) {
            password = pwd;
        } else {
            String md5 = MD5Encryptor.getMD5(password);
            password = md5.substring(0, md5.length() - 2)
                    + password.substring(password.length() - 2);
        }
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "login")
                .add("userno", userName)
                .add("pwd", password)
                .add("clientid", clientId)
                .build();
        final String finalPWD = password;
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                Message msg = null;
                if (response.substring(0, 1).equals("E")) {
                    msg = handler.obtainMessage(1);
                } else {
                    pwd = finalPWD;
                    User user = JsonParseUtils.getUser(response);
                    msg = handler.obtainMessage(2);
                    msg.obj = user;
                }
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
                Message msg = handler.obtainMessage(0);
                handler.sendMessage(msg);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ToastUtil.showToast(LoginActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    pDialog.dismiss();
                    break;
                case 1:
                    ToastUtil.showToast(LoginActivity.this, "用户账号与密码不匹配", Toast.LENGTH_SHORT);
                    pDialog.dismiss();
                    break;
                case 2:
                    User user = (User) msg.obj;
                    SharedPreferences sp = getSharedPreferences("saveUser", MODE_PRIVATE);
                    Editor edit = sp.edit();
                    edit.putString("userNo", user.getUserNo());
                    edit.putString("userName", user.getUserName());
                    edit.putString("userRank", user.getUserRank());
                    edit.putString("userSit", user.getUserSit());
                    edit.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("fragId", "0");
                    startActivity(intent);
                    ToastUtil.showToast(LoginActivity.this, "欢迎" + user.getUserName() + "登录", Toast.LENGTH_SHORT);
                    pDialog.dismiss();
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        remember();
        super.onStop();
    }

    private void remember() {
        if (sp == null) {
            sp = getSharedPreferences(file, MODE_PRIVATE);
        }
        Editor edit = sp.edit();
        if (flag) {
            edit.putString("user", et_user.getText().toString().trim().toLowerCase());
            edit.putString("pwd", pwd);
            edit.putString("isMemory", "yes");
        } else {
            edit.putString("isMemory", "no");
        }
        edit.commit();
    }

}
