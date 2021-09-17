package com.charlesgloria.ud.atys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.bean.HXContact;
import com.charlesgloria.ud.widget.SoftHideKeyBoardUtil;
import com.charlesgloria.ud.net.DownloadAddress;
import com.charlesgloria.ud.net.GetCode;
import com.charlesgloria.ud.net.Login;
import com.charlesgloria.ud.net.UploadHXContact;
import com.charlesgloria.ud.utils.MD5Tool;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class AtyLogin extends Activity {

    private EditText etPhone = null;
    private EditText etCode = null;

    private Button getcodeBtn, loginBtn;
    private final String TAG = "AtyLogin";

    //UI组件初始化
    private void bindView() {
        etPhone = (EditText) findViewById(R.id.etPhoneNum);
        etCode = (EditText) findViewById(R.id.etCode);
        getcodeBtn = (Button) findViewById(R.id.btnGetCode);
        loginBtn = (Button) findViewById(R.id.btnLogin);
//        registerBtn = (Button) findViewById(R.id.btnRegister);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.aty_login);
        //键盘不覆盖，需放在setContentView之后
        SoftHideKeyBoardUtil.assistActivity(this);
        bindView();

        //---------------------状态栏透明 begin----------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = AtyLogin.this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        //---------------------状态栏透明 end----------------------------------------

        findViewById(R.id.back_to_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);
            }
        });
        getcodeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (TextUtils.isEmpty(etPhone.getText())) {
                    Toast.makeText(AtyLogin.this, R.string.phone_num_cannot_be_empty, Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog pd = ProgressDialog.show(AtyLogin.this, getResources().getString(R.string.connecting), getResources().getString(R.string.connecting_to_server));

                new GetCode(etPhone.getText().toString(), new GetCode.SuccessCallback() {

                    @Override
                    public void onSuccess() {
                        pd.dismiss();
                        Toast.makeText(AtyLogin.this, R.string.suc_to_get_code, Toast.LENGTH_LONG).show();
                    }
                }, new GetCode.FailCallback() {

                    @Override
                    public void onFail() {
                        pd.dismiss();
                        Toast.makeText(AtyLogin.this, R.string.fail_to_get_code, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (TextUtils.isEmpty(etPhone.getText())) {
                    Toast.makeText(AtyLogin.this, R.string.phone_num_cannot_be_empty, Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(etCode.getText())) {
                    Toast.makeText(AtyLogin.this, R.string.code_cannot_be_empty, Toast.LENGTH_LONG).show();
                    return;
                }

                new Login(MD5Tool.md5(etPhone.getText().toString()), etCode.getText().toString(), etPhone.getText().toString(), new Login.SuccessCallback() {

                    @Override
                    public void onSuccess(String token, int isvalid) {
                        // 登录成功
                        // 记录下返回的Token和该用户的手机号
                        Config.cacheToken(AtyLogin.this, token);
                        Config.cachePhoneNum(AtyLogin.this, etPhone.getText().toString());

                        // 登录app成功，于是向环信注册用户，注册失败会抛出HyphenateException
                        //  注册环信最好在服务器端注册，即跟服务器端发送注册请求。但是网可能不好，这样一来二去浪费时间之后可能会导致下面的登录先执行，造成错误
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    EMClient.getInstance().createAccount(Config.getCachedToken(AtyLogin.this), Config.getCachedToken(AtyLogin.this));//同步方法
                                    Log.i(TAG, "EMClient register succ");
                                } catch (HyphenateException e) {
                                    Log.i(TAG, "EMClient register exception");
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        // 向环信注册用户完毕后登录环信
                        // 由于网络的延迟，可能上边还没有注册成功，下边就开始登录，会造成登录失败
                        // 所以应该将注册和登录放在一个线程里，严格地按照顺序执行。
                        // 应该给注册写一个NetConnection的衍生类，在衍生类的onSuccess接口中执行登录操作
                        // 衍生类的名字叫HuanXinLogin，URL就写api.jsp好了，输入的参数是用户名和密码，用户名和密码暂时都用手机号。
                        new Thread(new Runnable() {
                            public void run() {
                                String userName = Config.getCachedToken(AtyLogin.this);
                                String password = Config.getCachedToken(AtyLogin.this);

                                // 本地存储的Token可能为""，因为退出登录时把""cache进去了
                                if (userName != "" && password != "" && userName != null && password != null) {
                                    EMClient.getInstance().login(userName, password, new EMCallBack() {
                                        // 成功或失败后的回调方法
                                        @Override
                                        public void onSuccess() {
                                            // 加载所有组？
                                            EMClient.getInstance().groupManager().loadAllGroups();
                                            // 加载该用户的所有会话
                                            EMClient.getInstance().chatManager().loadAllConversations();
                                            Log.d(TAG, "登录聊天服务器成功！");
                                        }

                                        @Override
                                        public void onProgress(int progress, String status) {

                                        }

                                        @Override
                                        public void onError(int code, String message) {
                                            Log.d(TAG, "登录聊天服务器失败！");
                                        }
                                    });
                                }
                            }
                        }).start();

                        Config.loginStatus = Config.RESULT_STATUS_SUCCESS;

                        String phone = etPhone.getText().toString();

                        HXContact hxContact = new HXContact(phone, phone, "null");

                        new UploadHXContact(hxContact, new UploadHXContact.SuccessCallback() {
                            @Override
                            public void onSuccess() {
                                Log.i(TAG, "upload hx contact on success!");
                            }
                        }, new UploadHXContact.FailCallback() {
                            @Override
                            public void onFail() {

                            }
                        });

                        // isvalid：用户是否注册的信息
                        if (isvalid == Config.RESULT_STATUS_SUCCESS) {

                            // 下载用户的的地址，在下单的时候取出来，作为默认地址填写
                            new DownloadAddress(etPhone.getText().toString(), new DownloadAddress.SuccessCallback() {

                                @Override
                                public void onSuccess(String school, String area, String building, String room) {
                                    Config.cacheAddress(AtyLogin.this, area + building + room);
                                }
                            }, new DownloadAddress.FailCallback() {

                                @Override
                                public void onFail() {
                                    Toast.makeText(AtyLogin.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
                                }
                            });

                            Toast.makeText(AtyLogin.this, "您已注册", Toast.LENGTH_LONG).show();

                            // 登录成功，跳转到主界面
                            Intent i = new Intent(AtyLogin.this, AtyMainFrame.class);
                            // 登录后默认启动FragMe页面（登录操作必定在FragMe页面发出）
                            i.putExtra("page", "me");
                            startActivity(i);
                            finish();
                        } else {

                            Intent i = new Intent(AtyLogin.this, AtyAddress.class);
                            startActivity(i);
                            finish();
                        }

                    }
                }, new Login.FailCallback() {

                    @Override
                    public void onFail() {
                        Toast.makeText(AtyLogin.this, R.string.fail_to_login, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}
