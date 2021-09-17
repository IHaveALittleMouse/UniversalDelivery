package com.charlesgloria.ud.application;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.charlesgloria.ud.Config;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class MainApplication extends MultiDexApplication {
  private static final String TAG = "Init";

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(base);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    // 阿里云初始化
//    initCloudChannel(this);

    // 百度地图初始化

    if (EaseIM.getInstance().init(this, null)) {
      //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
      EMClient.getInstance().setDebugMode(true);

      setEaseUIProviders();
    }

  }

  /**
   * 初始化阿里云推送通道
   *
   * @param applicationContext
   */
  private void initCloudChannel(Context applicationContext) {
    PushServiceFactory.init(applicationContext);
    CloudPushService pushService = PushServiceFactory.getCloudPushService();
    pushService.register(applicationContext, new CommonCallback() {
      @Override
      public void onSuccess(String response) {
        Log.d(TAG, "init cloudchannel success");
      }

      @Override
      public void onFailed(String errorCode, String errorMessage) {
        Log.d(TAG,
            "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
      }
    });
  }

  private EaseUser getUserInfo(String username) {
    //获取 EaseUser实例, 这里从内存中读取
    //如果你是从服务器中读读取到的，最好在本地进行缓存
    EaseUser user;
    //如果用户是本人，就设置自己的头像
    if (username.equals(EMClient.getInstance().getCurrentUser())) {
      // 设置Username
      user = new EaseUser(username);
      // 设置头像
      user.setAvatar(Config.SERVER_URL_PORTRAITPATH + Config.getCachedPreference(this,
          Config.KEY_HX_PORTRAIT + username));
      // 暂时用ID替代
      user.setNickname(Config.getCachedPreference(this, Config.KEY_HX_NICKNAME + username));
      return user;
    }
//        if (user==null && getRobotList()!=null){
//            user=getRobotList().get(username);
//        }
    //收到别人的消息，设置别人的头像
    Config.setContactPortraitList();
    String portraitURL;
//            portraitPath = Config.getContactPortrait(username);
    portraitURL = Config.SERVER_URL_PORTRAITPATH + Config.getCachedPreference(this,
        Config.KEY_HX_PORTRAIT + username);
    String nickname = Config.getCachedPreference(getApplicationContext(),
        Config.KEY_HX_NICKNAME + username);
    user = new EaseUser(username);
    // 设置头像
    user.setAvatar(portraitURL);
    // 设置昵称
    user.setNickname(nickname);
    //            EaseCommonUtils.setUserInitialLetter(user);
    //        Log.i(TAG, "Portrait：" + user.getAvatar());
    return user;
  }

  protected void setEaseUIProviders() {
    // set profile provider if you want easeUI to handle avatar and nickname
    EaseIM.getInstance().setUserProvider((EaseUserProfileProvider) this::getUserInfo);
  }

  /**
   * set global listener
   */
  protected void setGlobalListeners() {
    registerMessageListener();
  }

  /**
   * Global listener
   * If this event already handled by an activity, you don't need handle it again
   * activityList.size() <= 0 means all activities already in background or not in Activity Stack
   */
  protected void registerMessageListener() {
  }

}