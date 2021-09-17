package com.charlesgloria.ud.frag;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.atys.AtyLogin;
import com.charlesgloria.ud.atys.AtyMainFrame;
import com.charlesgloria.ud.net.DeleteHXFriend;
import com.charlesgloria.ud.net.DownloadHXFriends;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.modules.contact.EaseContactListFragment;
import com.hyphenate.easeui.modules.conversation.EaseConversationListFragment;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by Administrator on 2018/2/11 0011.
 */

public class FragCommunity extends Fragment {
  //    private TextView unreadLabel;
  private Button[] mTabs;
  private EaseConversationListFragment conversationListFragment;
  private EaseContactListFragment contactListFragment;
  //    private SettingsFragment settingFragment;
  private Fragment[] fragments;
  private int index;
  private int currentTabIndex;

  private final String TAG = "FragChatMain";

  protected InputMethodManager inputMethodManager;

  //动画图片
  private ImageView cursor;

  //动画图片偏移量
  private int offset = 0;
  private int position_one;

  //动画图片宽度
  private int bmpW;

  //    protected void onCreate(Bundle arg0) {
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    String PHONE = Config.getCachedPhoneNum(getActivity());
    String TOKEN = Config.getCachedToken(getActivity());

    View view;

    if (TOKEN == null || TOKEN.equals("") || !TOKEN.equals(PHONE)) {
      view = inflater.inflate(R.layout.aty_unlog, container, false);
      view.findViewById(R.id.to_login).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(getActivity(), AtyLogin.class);
          startActivity(intent);
          getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
              R.transition.switch_still);
        }
      });

      view.findViewById(R.id.back_to_home).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent i = new Intent(getActivity(), AtyMainFrame.class);
          i.putExtra("page", "home");
          startActivity(i);
        }
      });
      return view;
    }

    view = inflater.inflate(R.layout.aty_chat_main, container, false);

//        ChatListener();

    //http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
    // should be in launcher activity, but all app use this can avoid the problem
    if (!getActivity().isTaskRoot()) {
      Intent intent = getActivity().getIntent();
      String action = intent.getAction();
      if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
        getActivity().finish();
        return view;
      }
    }
    inputMethodManager =
        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    /*
     *  以上用于避免extends EaseBaseActivity，已经将EaseBaseActivity中的有价值内容复制过来了
     * */

    // 控件绑定
//        unreadLabel = (TextView) view.findViewById(R.id.unread_msg_number);
    // 用数组来存放三个按钮
    mTabs = new Button[3];
    // 2个按钮用来跳转到各自的fragment
    // 会话按钮
    mTabs[0] = (Button) view.findViewById(R.id.btn_conversation);
    // 联系人列表按钮
    mTabs[1] = (Button) view.findViewById(R.id.btn_address_list);

    //动画效果
    cursor = (ImageView) view.findViewById(R.id.cursor);
    InitImageView();
    // set first tab as selected
    resetTextViewTextColor();
    mTabs[0].setSelected(true);
    mTabs[0].setTextColor(getResources().getColor(R.color.white));

    for (int i = 0; i < 2; i++) {
      mTabs[i].setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onTabClicked(v);
        }
      });
    }

    conversationListFragment = new EaseConversationListFragment() {
      // 设置会话列表里的会话点击事件，就是点击会话之后的处理，即弹出会话窗口，会话窗口类是ChatActivity
      @Override
      public void onItemClick(View view, int position) {
//        startActivity(new Intent(getActivity(), AtyChat.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()));
//                getActivity().overridePendingTransition(R.transition.switch_slide_in_right, R
//                .transition.switch_still);
      }
    };
    contactListFragment = new EaseContactListFragment() {
      // 设置联系人列表里的点击事件，点击联系人之后弹出会话窗口
      @Override
      public void onItemClick(View view, int position) {
//        startActivity(new Intent(getActivity(), AtyChat.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
//                getActivity().overridePendingTransition(R.transition.switch_slide_in_right, R
//                .transition.switch_still);
      }
    };

//        settingFragment = new SettingsFragment();
//        contactListFragment.setContactsMap(getContacts());

    // 将三个fragment存入数组
    fragments = new Fragment[]{conversationListFragment, contactListFragment};
    // add and show first fragment
    getChildFragmentManager().beginTransaction().add(R.id.fragment_container,
        conversationListFragment)
        .add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(conversationListFragment)
        .commit();
    return view;
  }

  /**
   * 将顶部文字恢复默认值
   */
  private void resetTextViewTextColor() {

    mTabs[0].setTextColor(getResources().getColor(R.color.grey_blue));
    mTabs[1].setTextColor(getResources().getColor(R.color.grey_blue));

    mTabs[0].setBackgroundColor(Color.TRANSPARENT);
    mTabs[1].setBackgroundColor(Color.TRANSPARENT);
  }

  /**
   * 初始化动画
   */
  private void InitImageView() {
    DisplayMetrics dm = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

    // 获取分辨率宽度
    int screenW = dm.widthPixels;

    bmpW = (screenW / 2);

    //设置动画图片宽度
    setBmpW(cursor, bmpW);
    offset = 0;

    //动画图片偏移量赋值
    position_one = (int) (screenW / 2.0);

  }

  /**
   * 设置动画图片宽度
   *
   * @param mWidth
   */
  private void setBmpW(ImageView imageView, int mWidth) {
    ViewGroup.LayoutParams para;
    para = imageView.getLayoutParams();
    para.width = mWidth;
    imageView.setLayoutParams(para);
  }

  // 可移植到fragment

  /**
   * onTabClicked
   *
   * @param view
   */
  public void onTabClicked(View view) {
    Animation animation = null;
    switch (view.getId()) {
      case R.id.btn_conversation:
        index = 0;
        break;
      case R.id.btn_address_list:
        index = 1;
        break;
    }
    // 如果选中的页面和当前页面不一致
    if (currentTabIndex != index) {
      FragmentTransaction trx = getChildFragmentManager().beginTransaction();
      // 将当前页面隐藏
      trx.hide(fragments[currentTabIndex]);
      resetTextViewTextColor();
      // 如果选中的页面还没有被添加到transaction里面，那么就进行添加
      if (!fragments[index].isAdded()) {
        trx.add(R.id.fragment_container, fragments[index]);
      }
      // 呈现选中的页面
      trx.show(fragments[index]).commit();
      if (currentTabIndex == 0 && index == 1) {
        animation = new TranslateAnimation(offset, position_one, 0, 0);
        resetTextViewTextColor();
//                tv2.setTextColor(getResources().getColor(R.color.white));
      } else if (currentTabIndex == 1 && index == 0) {
        animation = new TranslateAnimation(position_one, offset, 0, 0);
        resetTextViewTextColor();
//                tv1.setTextColor(getResources().getColor(R.color.white));
      }
      animation.setFillAfter(true);// true:图片停在动画结束位置
      animation.setDuration(300);
      cursor.startAnimation(animation);
    }
    // 将当前页面（跳转之前的页面）的选中状态设置为false
    mTabs[currentTabIndex].setSelected(false);
    // set current tab as selected.
    // 将要跳转到的页面的选中状态设置为true
    mTabs[index].setSelected(true);
    mTabs[index].setTextColor(getResources().getColor(R.color.white));
    // 将当前页面编号设置为要跳转页面的编号
    currentTabIndex = index;

  }

  /**
   * prepared users, password is "123456"
   * you can use these user to test
   *
   * @return
   */
  private Map<String, EaseUser> getContacts() {
    final Map<String, EaseUser>[] arrContacts = new HashMap[1];
    Map<String, EaseUser> contacts = new HashMap<>();
    for (int i = 1; i <= 10; i++) {
      EaseUser user = new EaseUser("easeuitest" + i);
      contacts.put("easeuitest" + i, user);
    }

    new DownloadHXFriends(Config.getCachedPhoneNum(getActivity()),
        friendsName -> {
          arrContacts[0] = new HashMap<>();
          for (int i = 0; i < friendsName.size(); i++) {
            EaseUser user = new EaseUser(friendsName.get(i));
            arrContacts[0].put(user.getUsername(), user);
            Log.i(TAG, "write arrContacts");
            String fname = arrContacts[0].get(user.getUsername()).getUsername();
            Log.i(TAG, "friend name is " + fname);
          }
//           contactListFragment.setContactsMap(arrContacts[0]);
        }, () -> {

        });

    return arrContacts[0];
  }

  @Override
  public void onResume() {
    super.onResume();
//    cancel notification
//    EaseUI.getInstance().getNotifier().reset();
    if (onFragChatListener != null) {
      onFragChatListener.onConversationClicked(0);
      onFragChatListener.onMessageReceived(0);
    }
  }

  public EaseConversationListFragment getConversationListFragment() {
    return conversationListFragment;
  }

  private OnFragChatListener onFragChatListener;

  public interface OnFragChatListener {
    void onConversationClicked(int responseCode);

    void onMessageReceived(int responseCode);
  }

  public void setOnFragChatListener(OnFragChatListener onFragHomeListener) {
    this.onFragChatListener = onFragHomeListener;
  }

  private void dialogChoice(String friendname, String nickname) {

    //单选对话窗口
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);

    //定义标题样式
    TextView title = new TextView(getActivity());
    title.setText("确定要删除 " + nickname + " 吗？");
    title.setPadding(10, 100, 10, 100);
    title.setGravity(Gravity.CENTER);
    title.setTextColor(getResources().getColor(com.hyphenate.easecallkit.R.color.black_deep));
    title.setTextSize(18);


    //使用自定义title
    builder.setCustomTitle(title);
    builder.setCancelable(true);
    try {
      final String myName = Config.getCachedPhoneNum(getActivity());
      final String friendName = friendname;
      builder.setPositiveButton("删除", (dialog, which) -> {
        new DeleteHXFriend(myName, friendName, () -> {
          Log.i(TAG, "delete on success");
          contactListFragment.onResume();
        }, () -> {

        });
        dialog.dismiss();
      });
      builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
    } catch (Exception e) {
      e.printStackTrace();
    }

    builder.create().show();
  }
}
