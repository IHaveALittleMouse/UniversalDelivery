package com.charlesgloria.ud.atys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.frag.FragCommunity;
import com.charlesgloria.ud.frag.FragHome;
import com.charlesgloria.ud.frag.FragMe;
import com.charlesgloria.ud.frag.FragOrder;
import com.charlesgloria.ud.net.UploadDeviceId;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by Administrator on 2017/10/31.
 */

public class AtyMainFrame extends FragmentActivity implements View.OnClickListener, FragHome.OnFragHomeListener, FragCommunity.OnFragChatListener {

    private LinearLayout tabHome;
    private LinearLayout tabOrder;
    private LinearLayout tabCommunity;
    private LinearLayout tabMe;

    private FrameLayout ly_content;

    private Fragment[] fragments = new Fragment[4];
    private FragHome fragHome;
    private FragOrder fragOrder;
    private FragCommunity fragCommunity;
    private FragMe fragMe;

    private TextView unreadMsgCount;

    // 用来在log输出中标志这个Activity的信息
    private String TAG = "atymainframe";

    protected static final int PHONE_STATE_GRANTED = 1;
    protected static final int CALL_PHONE_GRANTED = 3;
    protected static final int SHOW_UNREADMSG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        bindView();

        //---------------------状态栏透明 begin----------------------------------------
        Window window = AtyMainFrame.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        //---------------------状态栏透明 end----------------------------------------

        // 获取携带传入参数的intent实体
        Intent intent = getIntent();
        setIntent(intent);
        bindView();

        // page是传入参数，提示应该显示哪个界面
        String page = intent.getStringExtra("page");
        if (page != null) {
            switch (page) {
                case "home":
                    Log.i(TAG, "page home");
                    showFragHome();
                    break;
                case "order":
                    Log.i(TAG, "page order");
                    showFragOrder();
                    break;
                case "order_history":
                    Log.i(TAG, "page order history");
                    showFragOrderHistory();
                    break;
                case "community":
                    Log.i(TAG, "page community");
                    showFragCommunity();
                    break;
                case "me":
                    Log.i(TAG, "page me");
                    showFragMe();
                    break;
                default:
                    // page不为空但是不符合上述任何选项，那么默认显示Home页面
                    Log.i(TAG, "page not null but is empty");
                    showFragHome();
                    break;
            }
        } else {
            // page为空，默认显示Home页面
            Log.i(TAG, "page is null");
            showFragHome();
        }

        // 申请 读取手机状态 权限
        AndPermission.with(this)
                .permission(Manifest.permission.READ_PHONE_STATE).callback(new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {

                // 申请写入文件权限
                handler.sendEmptyMessage(PHONE_STATE_GRANTED);
                String permission = Manifest.permission.READ_PHONE_STATE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int i = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
                    if (i != PackageManager.PERMISSION_GRANTED) {

                    } else {
                        CloudPushService pushService = PushServiceFactory.getCloudPushService();
                        String deviceId = pushService.getDeviceId();
                        Config.cacheDeviceID(AtyMainFrame.this, deviceId);
                        new UploadDeviceId(Config.getCachedPhoneNum(AtyMainFrame.this), deviceId, new UploadDeviceId.SuccessCallback() {
                            @Override
                            public void onSuccess() {
                                Log.i(TAG, "upload deviceID succ");
                            }
                        }, new UploadDeviceId.FailCallback() {
                            @Override
                            public void onFail() {
                                Log.w(TAG, "upload deviceID fail");
                            }
                        });
                        Log.i(TAG, "AliPush deviceID:" + deviceId);
                    }
                }
            }

            @Override
            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                Log.i(TAG, "-------------------------------no permission of read_phone_state--------------------------------");
            }

        }).start();

        //show unreadmsgcount
        showUnreadMsg();
        ChatListener();
    }

    private void showUnreadMsg() {
        EMClient client = EMClient.getInstance();
        if (client == null || client.chatManager() == null) {
            return;
        }
        Map<String, EMConversation> conversations = client.chatManager().getAllConversations();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            int UnreadMsgCount = 0;
            for (EMConversation conversation : conversations.values()) {
                UnreadMsgCount += conversation.getUnreadMsgCount();
            }
            Log.i(TAG, UnreadMsgCount + "");
            Config.cachePreference(getApplicationContext(), Config.KEY_HX_UNRADMSGCOUNT, UnreadMsgCount + "");
        }
        String unread = Config.getCachedPreference(getApplicationContext(), Config.KEY_HX_UNRADMSGCOUNT);

        //show unreadmsgcount
        if (unread != null && !unread.equals("") && !unread.equals("0")) {
            unreadMsgCount.setText(unread);
            unreadMsgCount.setVisibility(View.VISIBLE);
        } else {
            unreadMsgCount.setVisibility(View.GONE);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PHONE_STATE_GRANTED:
                    AndPermission.with(getApplicationContext()).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE).callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            handler.sendEmptyMessage(CALL_PHONE_GRANTED);
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        }
                    }).start();
                    break;
                case CALL_PHONE_GRANTED:
                    AndPermission.with(getApplicationContext()).permission(Manifest.permission.CALL_PHONE).callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        }
                    }).start();
                    break;
                case SHOW_UNREADMSG:
                    showUnreadMsg();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //UI组件初始化与事件绑定
    private void bindView() {
        tabHome = (LinearLayout) this.findViewById(R.id.txt_home);
        tabOrder = (LinearLayout) this.findViewById(R.id.txt_order);
        tabCommunity = (LinearLayout) this.findViewById(R.id.txt_community);
        tabMe = (LinearLayout) this.findViewById(R.id.txt_me);
        ly_content = (FrameLayout) findViewById(R.id.fragment_container);

        tabHome.setOnClickListener(this);
        tabOrder.setOnClickListener(this);
        tabCommunity.setOnClickListener(this);
        tabMe.setOnClickListener(this);

        unreadMsgCount = (TextView) findViewById(R.id.unread_msg_number);
    }

    //重置所有文本的选中状态
    public void clearSelected() {
        tabHome.setSelected(false);
        tabOrder.setSelected(false);
        tabCommunity.setSelected(false);
        tabMe.setSelected(false);
    }

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction) {
        if (fragHome != null) {
            transaction.hide(fragHome);
        }
        if (fragOrder != null) {
            transaction.hide(fragOrder);
        }
        if (fragCommunity != null) {
            transaction.hide(fragCommunity);
        }
        if (fragMe != null) {
            transaction.hide(fragMe);
        }
    }

    // 当点击主界面上的fragment标签时显示相应fragment
    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (v.getId()) {
            case R.id.txt_home:
                // 清除所有frag的选中状态，全部设置为false（先前选中的frag只可能有一个，但无法预知是哪一个，因此全部清除）
                clearSelected();
                // 设置点击的frag的状态为选中
                tabHome.setSelected(true);
                // 如果选中的frag已经实例化，就跳转（transaction）到这个实例上，如果没有实例化就新建一个实例
                if (fragHome == null) {
                    fragHome = new FragHome();
                    fragHome.setOnFragHomeListener(this);
                    transaction.add(R.id.fragment_container, fragHome);
                } else {
                    transaction.show(fragHome);
                }
                break;

            case R.id.txt_order:
                clearSelected();
                tabOrder.setSelected(true);
                if (fragOrder == null) {
                    fragOrder = new FragOrder();
                    transaction.add(R.id.fragment_container, fragOrder);
                } else {
                    transaction.show(fragOrder);
                }
                break;

            case R.id.txt_community:
                clearSelected();
                tabCommunity.setSelected(true);
                if (fragCommunity == null) {
                    fragCommunity = new FragCommunity();
                    fragCommunity.setOnFragChatListener(this);
                    transaction.add(R.id.fragment_container, fragCommunity);
                } else {
                    transaction.show(fragCommunity);
                }
                break;

            case R.id.txt_me:
                clearSelected();
                tabMe.setSelected(true);
                if (fragMe == null) {
                    fragMe = new FragMe();
                    transaction.add(R.id.fragment_container, fragMe);
                } else {
                    transaction.show(fragMe);
                }
                break;
        }

        transaction.commit();
    }

    // 用于在本activity生成时指定显示的Fragment，生成时由于没有输入所以无法触发onClick方法，通过page参数指定显示的Fragment
    public void showFragHome() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        clearSelected();
        tabHome.setSelected(true);
        fragHome = new FragHome();
        fragHome.setOnFragHomeListener(this);
        transaction.add(R.id.fragment_container, fragHome);
        transaction.show(fragHome);
        transaction.commit();
        fragHome.refresh();
    }

    public void showFragOrder() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        clearSelected();
        tabOrder.setSelected(true);
        fragOrder = new FragOrder(FragOrder.SHOW_CURRENT);
        transaction.add(R.id.fragment_container, fragOrder);
        transaction.show(fragOrder);
        transaction.commit();
    }

    public void showFragOrderHistory() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        clearSelected();
        tabOrder.setSelected(true);
        fragOrder = new FragOrder(FragOrder.SHOW_HISTORY);
        transaction.add(R.id.fragment_container, fragOrder);
        transaction.show(fragOrder);
        transaction.commit();
    }

    public void showFragCommunity() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        clearSelected();
        tabCommunity.setSelected(true);
        fragCommunity = new FragCommunity();
        fragCommunity.setOnFragChatListener(this);
        transaction.add(R.id.fragment_container, fragCommunity);
        transaction.show(fragCommunity);
        transaction.commit();
    }

    public void showFragMe() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        clearSelected();
        tabMe.setSelected(true);
        fragMe = new FragMe();
        transaction.add(R.id.fragment_container, fragMe);
        transaction.show(fragMe);
        transaction.commit();
    }

    // 用于被MainApplication的setConsoleText调用，MainApplication的setConsoleText被MyMessageIntentService调用，用于MyMessageIntentService在AtyMainFrame中输出信息
    public void appendConsoleText(String text) {
        Log.i(TAG, text);
        Toast.makeText(AtyMainFrame.this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConversationClicked(int responseCode) {
        showUnreadMsg();
    }

    @Override
    public void onMessageReceived(int responseCode) {
        showUnreadMsg();
    }

    @Override
    public void onItemsClicked(int responseCode) {
        Log.i(TAG, "onItemsClicked entered");
        switch (responseCode) {
            case FragHome.DELIVERING_ORDERS_CLIKED:
                if (fragOrder != null) {
                    Log.i(TAG, "DELIVERING_ORDERS_CLIKED");
                    fragOrder.selectTv(FragOrder.SHOW_CURRENT);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    hideAllFragment(transaction);
                    clearSelected();
                    tabOrder.setSelected(true);
                    transaction.show(fragOrder);
                    transaction.commit();
                } else {
                    showFragOrder();
                }
                break;
            case FragHome.HISTORY_ORDERS_CLIKED:
                if (fragOrder != null) {
                    Log.i(TAG, "HISTORY_ORDERS_CLIKED");
                    fragOrder.selectTv(FragOrder.SHOW_HISTORY);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    hideAllFragment(transaction);
                    clearSelected();
                    tabOrder.setSelected(true);
                    transaction.show(fragOrder);
                    transaction.commit();
                } else {
                    showFragOrderHistory();
                }
                break;
            case FragHome.ERROR_ORDERS_CLIKED:
                if (fragOrder != null) {
                    Log.i(TAG, "ERROR_ORDERS_CLIKED");
                    fragOrder.selectTv(FragOrder.SHOW_CURRENT);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    hideAllFragment(transaction);
                    clearSelected();
                    tabOrder.setSelected(true);
                    transaction.show(fragOrder);
                    transaction.commit();
                } else {
                    showFragOrder();
                }
                break;
            default:
        }
    }

    @Override
    public void contactCustomerService(int responseCode) {
        if (fragCommunity == null) {
            showFragCommunity();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            hideAllFragment(transaction);
            clearSelected();
            tabCommunity.setSelected(true);
            fragCommunity.setOnFragChatListener(this);
            transaction.show(fragCommunity);
            transaction.commit();
        }
        startActivity(new Intent(getApplicationContext(), AtyChat.class).putExtra(EaseConstant.EXTRA_CONVERSATION_ID, "18795808378"));
    }

    //聊天消息
    public void ChatListener() {

        EMMessageListener msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                handler.sendEmptyMessage(SHOW_UNREADMSG);
                if (fragCommunity != null) {
                    Log.i(TAG, "ChatListener onResume");
//                     refresh conversationList in fragCommunity
//                    fragCommunity.getConversationListFragment().getHandler().sendEmptyMessage(EaseConversationListFragment);
                }
                Log.i(TAG, "onMessageReceived");
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
                Log.i(TAG, "onCmdMessageReeived");
            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {
                Log.i(TAG, "onMessageDelivered");
            }

            @Override
            public void onMessageRecalled(List<EMMessage> list) {
                Log.i(TAG, "onMessageRecalled");
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
                Log.i(TAG, "onMessageChanged");
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

}
