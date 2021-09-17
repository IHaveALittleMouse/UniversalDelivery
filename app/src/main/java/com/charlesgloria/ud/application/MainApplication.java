package com.charlesgloria.ud.application;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.charlesgloria.ud.Config;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;

public class MainApplication extends Application {
    private static final String TAG = "Init";
    private Context appContext = null;
    private EaseUI easeUI = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 阿里云初始化
        initCloudChannel(this);

        // 百度地图初始化

        if (EaseUI.getInstance().init(this, null)) {
            //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
            EMClient.getInstance().setDebugMode(true);

            easeUI = EaseUI.getInstance();
            setEaseUIProviders();
//            setGlobalListeners();
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
                Log.d(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }

    private EaseUser getUserInfo(String username) {
        //获取 EaseUser实例, 这里从内存中读取
        //如果你是从服务器中读读取到的，最好在本地进行缓存
        EaseUser user = null;
        //如果用户是本人，就设置自己的头像
        if (username.equals(EMClient.getInstance().getCurrentUser())) {
            // 设置Username
            user = new EaseUser(username);
            // 设置头像
            user.setAvatar(Config.SERVER_URL_PORTRAITPATH + Config.getCachedPreference(this, Config.KEY_HX_PORTRAIT + username));
            // 暂时用ID替代
            user.setNick(Config.getCachedPreference(this, Config.KEY_HX_NICKNAME + username));
            return user;
        }
//        if (user==null && getRobotList()!=null){
//            user=getRobotList().get(username);
//        }
        //收到别人的消息，设置别人的头像
        if (user == null) {
            Config.setContactPortraitList();
            String portraitURL;
//            portraitPath = Config.getContactPortrait(username);
            portraitURL = Config.SERVER_URL_PORTRAITPATH + Config.getCachedPreference(this, Config.KEY_HX_PORTRAIT + username);
            String nickname = Config.getCachedPreference(getApplicationContext(), Config.KEY_HX_NICKNAME + username);
            if (portraitURL != null) {
                user = new EaseUser(username);
                // 设置头像
                user.setAvatar(portraitURL);
                // 设置昵称
                user.setNickname(nickname);
            }
//            EaseCommonUtils.setUserInitialLetter(user);
        } else {
            if (TextUtils.isEmpty(user.getNickname())) {//如果名字为空，则显示环信号码
                user.setNick(user.getUsername());
            }
        }
//        Log.i(TAG, "Portrait：" + user.getAvatar());
        return user;
    }

    protected void setEaseUIProviders() {
        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });
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
//        messageListener = new EMMessageListener() {
//            private BroadcastReceiver broadCastReceiver = null;
//            @Override
//            public void onMessageReceived(List<EMMessage> messages) {
//                for (EMMessage message : messages) {
//                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
//                    //接收并处理扩展消息
//                    String userName=message.getStringAttribute(Constant.USER_NAME,"");
//                    String userId=message.getStringAttribute(Constant.USER_ID,"");
//                    String userPic=message.getStringAttribute(Constant.HEAD_IMAGE_URL,"");
//                    String hxIdFrom=message.getFrom();
//                    System.out.println("helper接收到的用户名："+userName+"helper接收到的id："+userId+"helper头像："+userPic);
//                    EaseUser easeUser=new EaseUser(hxIdFrom);
//                    easeUser.setAvatar(userPic);
//                    easeUser.setNick(userName);
//                    //存入内存
//                    getContactList();
//                    contactList.put(hxIdFrom,easeUser);
//                    //存入db
//                    UserDao dao=new UserDao(appContext);
//                    List<EaseUser> users=new ArrayList<EaseUser>();
//                    users.add(easeUser);
//                    dao.saveContactList(users);
//                    // in background, do not refresh UI, notify it in notification bar
////                    if(!easeUI.hasForegroundActivies()){
////                        getNotifier().onNewMsg(message);
////                    }
//                }
//            }
//            @Override
//            public void onCmdMessageReceived(List<EMMessage> messages) {
//                for (EMMessage message : messages) {
//                    EMLog.d(TAG, "receive command message");
//                    //get message body
//                    //end of red packet code
//                    //获取扩展属性 此处省略
//                    //maybe you need get extension of your message
//                    //message.getStringAttribute("");
//                }
//            }
//            @Override
//            public void onMessageRead(List<EMMessage> messages) {
//            }
//            @Override
//            public void onMessageDelivered(List<EMMessage> message) {
//            }
//            @Override
//            public void onMessageChanged(EMMessage message, Object change) {
//            }
//        };
//        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }



}