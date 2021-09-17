package com.charlesgloria.ud.atys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.net.UploadToken;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import static com.charlesgloria.ud.Config.APP_ID;

public class AtyLaunch extends Activity {

  private Integer time = 3000;    //设置等待时间，单位为毫秒

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //加载启动图片
    setContentView(R.layout.aty_launch);


    //---------------------状态栏透明 begin----------------------------------------
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = AtyLaunch.this.getWindow();
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(Color.TRANSPARENT);
    }
    //---------------------状态栏透明 end----------------------------------------

    // 判断该用户是否已经登录
    {
      // 获得token
      SharedPreferences sharedPreferences = getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
      String token = sharedPreferences.getString(Config.KEY_TOKEN, "");

      // 用UploadToken类上传token，并处理返回值
      new UploadToken(token, new UploadToken.SuccessCallback() {
        @Override
        public void onSuccess() {
//                    Toast.makeText(AtyLaunch.this, R.string.login_already, Toast.LENGTH_LONG)
//                    .show();
          Config.loginStatus = 1;
        }
      }, new UploadToken.FailCallback() {
        @Override
        public void onFail() {
//                    Toast.makeText(AtyLaunch.this, R.string.login_notyet, Toast.LENGTH_LONG)
//                    .show();
          Config.loginStatus = 0;
        }
      });
    }


    Handler handler = new Handler();
    //当计时结束时，跳转至主界面
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent intent = new Intent();
        intent.setClass(AtyLaunch.this, AtyMainFrame.class);
        intent.putExtra("page", "home");
        startActivity(intent);
        AtyLaunch.this.finish();
//                AtyLaunch.this.overridePendingTransition(R.transition.switch_still, R
//                .transition.switch_still);

      }
    }, time);
  }

  @Override
  protected void onStart() {
    super.onStart();

    // 在欢迎界面登录环信账号（如果已经注册而且UD处于登录状态的话），但是UD处于登录状态时不会退出环信账号，这里的登录有多余的嫌疑。
    new Thread(new Runnable() {
      public void run() {
        String userName = Config.getCachedToken(AtyLaunch.this);
        String password = Config.getCachedToken(AtyLaunch.this);

        if (userName != "" && password != "" && userName != null && password != null) {
          EMClient.getInstance().login(userName, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
              EMClient.getInstance().groupManager().loadAllGroups();
              EMClient.getInstance().chatManager().loadAllConversations();
              Log.d("main", "登录聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
              Log.d("main", "登录聊天服务器失败！");
            }
          });
        }
      }
    }).start();

  }
}
