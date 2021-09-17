package com.charlesgloria.ud.atys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.charlesgloria.ud.R;
import com.charlesgloria.ud.widget.SoftHideKeyBoardUtil;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;

import androidx.fragment.app.FragmentActivity;

public class AtyChat extends FragmentActivity {
  public static AtyChat activityInstance;
  private EaseChatFragment chatFragment;
  String toChatUsername;

  protected InputMethodManager inputMethodManager;

  @Override
  protected void onCreate(Bundle arg0) {
    super.onCreate(arg0);

    //http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
    // should be in launcher activity, but all app use this can avoid the problem
    if (!isTaskRoot()) {
      Intent intent = getIntent();
      String action = intent.getAction();
      if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
        finish();
        return;
      }
    }
    inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    /*
     *  以上用于避免extends EaseBaseActivity，已经将EaseBaseActivity中的有价值内容复制过来了
     * */
    setContentView(R.layout.aty_chat_chat);
    //---------------------状态栏透明 begin----------------------------------------
    Window window = AtyChat.this.getWindow();
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(Color.TRANSPARENT);
    //---------------------状态栏透明 end----------------------------------------
    SoftHideKeyBoardUtil.assistActivity(this);
    activityInstance = this;
    //user or group id
    toChatUsername = getIntent().getExtras().getString(EaseConstant.EXTRA_CONVERSATION_ID);
    chatFragment = new EaseChatFragment();
    //set arguments
    chatFragment.setArguments(getIntent().getExtras());
    getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    activityInstance = null;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    // enter to chat activity when click notification bar, here make sure only one chat activiy
    String username = intent.getStringExtra("userId");
    if (toChatUsername.equals(username))
      super.onNewIntent(intent);
    else {
      finish();
      startActivity(intent);
    }

  }

  @Override
  public void onBackPressed() {
//    chatFragment.onBackPressed();
  }

  public String getToChatUsername() {
    return toChatUsername;
  }

  @Override
  protected void onResume() {
    super.onResume();
    // cancel the notification
    EaseIM.getInstance().getNotifier().reset();
  }
}
