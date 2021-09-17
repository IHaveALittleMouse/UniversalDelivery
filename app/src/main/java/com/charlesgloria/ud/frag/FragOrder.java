package com.charlesgloria.ud.frag;

import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.atys.AtyDetails;
import com.charlesgloria.ud.atys.AtyLogin;
import com.charlesgloria.ud.atys.AtyMainFrame;
import com.charlesgloria.ud.widget.MultiSwipeRefreshLayout;
import com.charlesgloria.ud.widget.OrderView;
import com.charlesgloria.ud.net.DownloadOrders;
import com.charlesgloria.ud.net.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/29.
 */

public class FragOrder extends Fragment {
  private ArrayList<Order> ORDERS;
  private LinearLayout ll;
  private LinearLayout history;
  private ScrollView scrollView1;
  private ScrollView scrollView2;
  private MultiSwipeRefreshLayout swipeRefreshLayout;
  private ImageView top;
  private int selection = 0; //0:current page;    1:history page;
  //    protected EditText query;
//    protected ImageButton clearSearch;
  protected InputMethodManager inputMethodManager;
  private ViewPager pager;

  //动画图片
  private ImageView cursor;

  //动画图片偏移量
  private int offset = 0;
  private int position_one;

  //动画图片宽度
  private int bmpW;


  private String phone;

  private List<View> views;
  private List<TextView> tvs = new ArrayList<TextView>();
  private TextView tv1;
  private TextView tv2;
  protected boolean hidden;

  private final int CODE_REFRESH = 1;
  private final String TAG = "FragOrder";

  public static final int SHOW_HISTORY = 1;
  public static final int SHOW_CURRENT = 0;

  public FragOrder() {

  }

  @SuppressLint("ValidFragment")
  public FragOrder(int selection) {
    this.selection = selection;
  }

  @Nullable
  @Override
  public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = null;
    String token = Config.getCachedToken(getActivity());
    String phone = Config.getCachedPhoneNum(getActivity());
    ORDERS = new ArrayList<>();
    if (token == null || token.equals("")) {
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
    } else if (token.equals(phone)) {
      view = inflater.inflate(R.layout.frag_order, container, false);

      scrollView1 =
          inflater.inflate(R.layout.mod_current_order, container, false).findViewById(R.id.current_order_scroll);
      scrollView2 =
          inflater.inflate(R.layout.mod_history_order, container, false).findViewById(R.id.history_order_scroll);

      swipeRefreshLayout = view.findViewById(R.id.srl_fragOrder);

      ll = scrollView1.findViewById(R.id.current_order_ll);
      history = scrollView2.findViewById(R.id.history_order_ll);

      pager = view.findViewById(R.id.vp_fragOrder);
      tv1 = view.findViewById(R.id.page_current);
      tv2 = view.findViewById(R.id.page_history);
      tv1.setOnClickListener(new FragOrder.MyClickListener(0));
      tv2.setOnClickListener(new FragOrder.MyClickListener(1));
      tvs.add(tv1);
      tvs.add(tv2);

      //        初始化ViewPager组件
      Log.i(TAG, selection + "init");
      initView();
      resetTextViewTextColor();
      initViewPager();

      if (selection == 1) {
        pager.setCurrentItem(selection);
      }

      //动画效果
      cursor = (ImageView) view.findViewById(R.id.cursor);
      InitImageView();

      //---------------------解决RefreshLayout和ScrollView的冲突 begin-----------------------------------
      scrollView1.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
          if (selection == 0) {
            swipeRefreshLayout.setEnabled(scrollView1.getScrollY() == 0);
          }
        }
      });
      scrollView2.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
          if (selection == 1) {
            swipeRefreshLayout.setEnabled(scrollView2.getScrollY() == 0);
          }
        }
      });
      //---------------------解决RefreshLayout和ScrollView的冲突 end-----------------------------------

      //---------------------------下拉刷新 begin-------------------------------
      //setColorSchemeResources()可以改变加载图标的颜色。
      swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.theme_blue,
          R.color.colorPrimary, com.hyphenate.easeui.R.color.black);
      swipeRefreshLayout.setOnRefreshListener(() -> {
        refresh();
        swipeRefreshLayout.setRefreshing(false);
      });
      //---------------------------下拉刷新 end-------------------------------

      //---------------------------BACK TO TOP begin-------------------------------
      top = (ImageView) view.findViewById(R.id.iv_fragOrder_backtotop);
      top.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
          scrollView1.post(new Runnable() {

            @Override
            public void run() {
              scrollView1.post(new Runnable() {
                public void run() {
                  // 滚动至顶部
                  scrollView1.fullScroll(ScrollView.FOCUS_UP);
                }
              });
            }
          });
          scrollView2.post(new Runnable() {

            @Override
            public void run() {
              scrollView2.post(new Runnable() {
                public void run() {
                  // 滚动至顶部
                  scrollView2.fullScroll(ScrollView.FOCUS_UP);
                }
              });
            }
          });
        }
      });
      //---------------------------BACK TO TOP end-------------------------------

      inputMethodManager =
          (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

      return view;
    }
    return view;
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

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    this.hidden = hidden;
    String token = Config.getCachedToken(getActivity());
    if (!hidden && token != null && token != "") {
      refresh();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    String token = Config.getCachedToken(getActivity());
    if (!hidden && token != null && token != "") {
      refresh();
    }
  }

  public void refresh() {

    if (ll != null) {
      ll.removeAllViews();
      Log.i(TAG, "ll != null");
    }
    if (history != null) {
      history.removeAllViews();
    }
    new DownloadOrders(Config.getCachedPhoneNum(getActivity()),
        new DownloadOrders.SuccessCallback() {

      @Override
      public void onSuccess(ArrayList<Order> orders) {

        //        订单号   order_number
        //        下单时间 order_time
        //        快递体积 size(L M S)
        //        收货地点 arrive_address
        //        收货时间 arrive_time
        //        备注     note

        for (Order o : orders) {
          ORDERS.add(o);
          final String orderNumber = o.getOrderNumber();
          String arriveAddress = o.getArriveAddress();
          String arriveTime = o.getArriveTime();
          String note = o.getNote();
          String size = o.getSize();
          String orderTime = o.getOrderTime();
          String orderStatus = o.getOrderStatus();
          String trustFriend = o.getTrust_friend();

          final OrderView newov = new OrderView(getActivity());
          newov.setTv_size(size);
          newov.setTv_orderNumber(orderNumber);
          newov.setTv_arriveTime(arriveTime);
          newov.setTv_orderTime(orderTime);
          if (note.equals("none") || note.equals("null") || note == null) {
            note = "无";
          }
//                    newov.setTv_note(note);
          if (trustFriend.equals("none") || trustFriend.equals("null") || trustFriend == null) {
            //自己拿
            newov.setTv_pickPattern("自己拿");
          } else {
            //信任好友代拿
            newov.setTv_pickPattern("信任好友代拿");
          }

          newov.getLl_modOrder_allAround().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String orderNum = newov.getTv_orderNumber().getText().toString();
              Log.i(TAG, "orderNumber:" + orderNum);
              Intent intent = new Intent(getActivity(), AtyDetails.class);
              intent.putExtra("orderNumber", orderNum);
              intent.putExtra("pattern", "");
              startActivityForResult(intent, CODE_REFRESH);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            }
          });

          if (orderStatus.equals("0")) {
            history.addView(newov);
          } else {
            ll.addView(newov);
          }
        }
      }
    }, new DownloadOrders.FailCallback() {

      @Override
      public void onFail() {
        Toast.makeText(getActivity(), R.string.fail_to_commit, Toast.LENGTH_LONG).show();
      }
    });
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    String token = Config.getCachedToken(getActivity());
    if (token != null && token != "") {
      tv1.setOnClickListener(new FragOrder.MyClickListener(0));
      tv2.setOnClickListener(new FragOrder.MyClickListener(1));
    }
  }

  public void selectTv(int selection) {
    switch (selection) {
      case SHOW_CURRENT:
        tv1.performClick();
        break;
      case SHOW_HISTORY:
        tv2.performClick();
        break;
      default:
    }
  }

  private class MyClickListener implements View.OnClickListener {

    private int index;

    public MyClickListener(int index) {
      // TODO Auto-generated constructor stub
      this.index = index;
    }

    @Override
    public void onClick(View v) {
      // TODO Auto-generated method stub
      //改变ViewPager当前显示页面
      pager.setCurrentItem(index);
    }
  }

  //初始化ViewPager中显示的数据
  public void initView() {
    // TODO Auto-generated method stub
    views = new ArrayList<View>();
//        scrollView = new ArrayList<View>();
//        LayoutInflater li = getActivity().getLayoutInflater();

    views.add(scrollView1);
    views.add(scrollView2);
  }

  public void initViewPager() {
    // TODO Auto-generated method stub
    PagerAdapter adapter = new FragOrder.MyPagerAdapter();
    pager.setAdapter(adapter);
    Log.i(TAG, selection + "here");
    tvs.get(selection).setTextColor(getResources().getColor(R.color.white));
    pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

      @Override
      public void onPageSelected(int index) {
        // TODO Auto-generated method stub
        Animation animation = null;
        for (int i = 0; i < tvs.size(); i++) {

          if (selection == 0 && index == 1) {
            animation = new TranslateAnimation(offset, position_one, 0, 0);
            resetTextViewTextColor();
            tv2.setTextColor(getResources().getColor(R.color.white));
          } else if (selection == 1 && index == 0) {
            animation = new TranslateAnimation(position_one, offset, 0, 0);
            resetTextViewTextColor();
            tv1.setTextColor(getResources().getColor(R.color.white));
          }
        }
        selection = index;
        animation.setFillAfter(true);// true:图片停在动画结束位置
        animation.setDuration(300);
        cursor.startAnimation(animation);
      }

      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

      }

      @Override
      public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

      }
    });
  }

  /**
   * 将顶部文字恢复默认值
   */
  private void resetTextViewTextColor() {

    tv1.setTextColor(getResources().getColor(R.color.grey_blue));
    tv2.setTextColor(getResources().getColor(R.color.grey_blue));

    tv1.setBackgroundColor(Color.TRANSPARENT);
    tv2.setBackgroundColor(Color.TRANSPARENT);
  }

  private class MyPagerAdapter extends PagerAdapter {

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
      // TODO Auto-generated method stub
      return arg0 == arg1;
    }

    //有多少个切换页
    @Override
    public int getCount() {
      // TODO Auto-generated method stub
      return views.size();
    }

    //对超出范围的资源进行销毁
    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
      // TODO Auto-generated method stub
      //super.destroyItem(container, position, object);

      container.removeView(views.get(position));
    }

    //对显示的资源进行初始化
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      // TODO Auto-generated method stub
      //return super.instantiateItem(container, position);
      container.addView(views.get(position));
      return views.get(position);
    }
  }

  protected void hideSoftKeyboard() {
    if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
      if (getActivity().getCurrentFocus() != null)
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CODE_REFRESH) {
      Log.i(TAG, "refresh");
//            refresh();
    }
  }

}
