package com.charlesgloria.ud.frag;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.atys.AtyAddressMng;
import com.charlesgloria.ud.atys.AtyFeedBack;
import com.charlesgloria.ud.atys.AtyFetch;
import com.charlesgloria.ud.atys.AtyHelp;
import com.charlesgloria.ud.atys.AtyJoinUs;
import com.charlesgloria.ud.atys.AtyLocation;
import com.charlesgloria.ud.atys.AtyMail;
import com.charlesgloria.ud.atys.AtyTrustOrders;
import com.charlesgloria.ud.atys.AtyUnlog;
import com.charlesgloria.ud.net.DownloadOrders;
import com.charlesgloria.ud.net.Order;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/10/29.
 */

public class FragHome extends Fragment {

  // 代拿下单按钮
  private Button get_btn;
  private TextView tv_delivering;
  private TextView tv_history;
  private TextView tv_error;
  private LinearLayout ll_delivering;
  private LinearLayout ll_history;
  private LinearLayout ll_error;
  private int delivering_num = 0;
  private int history_num = 0;
  private int error_num = 0;

  //    private LinearLayout linearLayout;
  private String PHONE;
  private String TOKEN;

  public static final int DELIVERING_ORDERS_CLIKED = 1;
  public static final int HISTORY_ORDERS_CLIKED = 2;
  public static final int ERROR_ORDERS_CLIKED = 3;
  //请求码
  private final static int REQUEST_CODE = 0x123;

  private final String TAG = "FragHome";

  // 默认构造函数
  public FragHome() {

  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    PHONE = Config.getCachedPhoneNum(getActivity());
    TOKEN = Config.getCachedToken(getActivity());
    View view = inflater.inflate(R.layout.frag_home, container, false);
    get_btn = (Button) view.findViewById(R.id.get_btn);
//        linearLayout = (LinearLayout) view.findViewById(R.id.take_orders);

    refresh();

    // 绑定下单按钮的事件
    get_btn.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        String token = Config.getCachedToken(getActivity());
        String phone = Config.getCachedPhoneNum(getActivity());
        if (token != null && !token.equals("") && token.equals(phone)) {
          // 用户已登录
          // 这个startActivityforResult没有写对应的onActivityResult函数进行处理
          startActivityForResult(new Intent(getActivity(), AtyFetch.class),
              Activity.RESULT_FIRST_USER);
          getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
              R.transition.switch_still);
        } else {
          // 用户未登录
          Intent intent = new Intent(getActivity(), AtyUnlog.class);
          startActivity(intent);
          getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
              R.transition.switch_still);
        }
      }
    });

    //--------------------------九宫格 begin--------------------------------------------------------
    final String[] name = {"取快递", "寄快递", "信任订单",
        "定位", "地址管理", "使用指南",
        "加入UDers", "客服", "问题反馈"};

    final int[] imageRes = {R.drawable.item_fraghome_fetch,
        R.drawable.item_fraghome_mail,
        R.drawable.item_fraghome_order,
        R.drawable.item_fraghome_locate,
        R.drawable.item_fraghome_address,
        R.drawable.item_fraghome_help,
        R.drawable.item_fraghome_join,
        R.drawable.item_fraghome_consult,
        R.drawable.item_fraghome_feedback};
    GridView gridView = (GridView) view.findViewById(R.id.gv_fragHome_func);//初始化

    //生成动态数组，并且转入数据
    ArrayList<HashMap<String, Object>> listItemArrayList = new ArrayList<HashMap<String, Object>>();
    for (int i = 0; i < imageRes.length; i++) {
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("itemImage", imageRes[i]);
      map.put("itemText", name[i]);
      listItemArrayList.add(map);
    }
    //生成适配器的ImageItem 与动态数组的元素相对应
    SimpleAdapter saImageItems = new SimpleAdapter(getActivity(),
        listItemArrayList,//数据来源
        R.layout.item_grid_func,//item的XML

        //动态数组与ImageItem对应的子项
        new String[]{"itemImage", "itemText"},

        //ImageItem的XML文件里面的一个ImageView,TextView ID
        new int[]{R.id.iv_itemGrid, R.id.tv_itemGrid});
    //添加并且显示
    gridView.setAdapter(saImageItems);
    //添加消息处理
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), name[position], Toast.LENGTH_LONG).show();
        switch (position) {
          case 0:
            if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
              Intent intent0 = new Intent(getActivity(), AtyFetch.class);
              startActivity(intent0);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            } else {
              Intent intent = new Intent(getActivity(), AtyUnlog.class);
              startActivity(intent);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            }

            break;
          case 1:
            if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
              Intent intent1 = new Intent(getActivity(), AtyMail.class);
              startActivity(intent1);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            } else {
              Intent intent = new Intent(getActivity(), AtyUnlog.class);
              startActivity(intent);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            }

            break;
          case 2:
            if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
              Intent intent2 = new Intent(getActivity(), AtyTrustOrders.class);
              startActivity(intent2);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            } else {
              Intent intent = new Intent(getActivity(), AtyUnlog.class);
              startActivity(intent);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            }

            break;
          case 3://locate
            String permission = Manifest.permission.ACCESS_FINE_LOCATION;
            int i = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (i != PackageManager.PERMISSION_GRANTED) {
              AndPermission.with(getActivity()).permission(Manifest.permission.ACCESS_FINE_LOCATION).callback(new PermissionListener() {
                @Override
                public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                  Intent intent = new Intent(getActivity(), AtyLocation.class);
                  startActivityForResult(intent, REQUEST_CODE);
                }

                @Override
                public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                }
              }).start();
            } else {
              Intent intent = new Intent(getActivity(), AtyLocation.class);
              startActivityForResult(intent, REQUEST_CODE);
            }

            break;
          case 4:
            if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
              Intent intent4 = new Intent(getActivity(), AtyAddressMng.class);
              startActivity(intent4);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            } else {
              Intent intent = new Intent(getActivity(), AtyUnlog.class);
              startActivity(intent);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            }
            break;

          case 5:
            Intent intent5 = new Intent(getActivity(), AtyHelp.class);
            startActivity(intent5);
            getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                R.transition.switch_still);
            break;
          case 6:
            if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
              Intent intent6 = new Intent(getActivity(), AtyJoinUs.class);
              startActivity(intent6);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            } else {
              Intent intent = new Intent(getActivity(), AtyUnlog.class);
              startActivity(intent);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            }
            break;
          case 7://客服
            if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
              onFragHomeListener.contactCustomerService(0);
            } else {
              Intent intent = new Intent(getActivity(), AtyUnlog.class);
              startActivity(intent);
              getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                  R.transition.switch_still);
            }
            break;
          case 8:
            Intent intent8 = new Intent(getActivity(), AtyFeedBack.class);
            startActivity(intent8);
            getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
                R.transition.switch_still);
            break;
        }
      }
    });

    gridView.setFocusable(false);
    //--------------------------九宫格 end--------------------------------------------------------

    //bindviews
    tv_delivering = view.findViewById(R.id.tv_fragHome_delivering);
    tv_history = view.findViewById(R.id.tv_fragHome_history);
    tv_error = view.findViewById(R.id.tv_fragHome_error);
    ll_delivering = view.findViewById(R.id.ll_fragHome_delivering);
    ll_history = view.findViewById(R.id.ll_fraghome_history);
    ll_error = view.findViewById(R.id.ll_fragHome_error);

    ll_delivering.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.i(TAG, "delivering clicked");
        onFragHomeListener.onItemsClicked(DELIVERING_ORDERS_CLIKED);
      }
    });

    ll_history.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.i(TAG, "history clicked");
        onFragHomeListener.onItemsClicked(HISTORY_ORDERS_CLIKED);
      }
    });

    ll_error.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onFragHomeListener.onItemsClicked(ERROR_ORDERS_CLIKED);
      }
    });

    return view;
  }

  public void refresh() {

    if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
      new DownloadOrders(PHONE, new DownloadOrders.SuccessCallback() {

        @Override
        public void onSuccess(ArrayList<Order> orders) {
          for (Order o : orders) {
            String orderStatus = o.getOrderStatus();

            if (orderStatus.equals("0")) {
              history_num++;
            } else if (orderStatus.equals("1") || orderStatus.equals("2")) {
              delivering_num++;
            } else if (orderStatus.equals("3")) {
              error_num++;
            }
          }
          tv_delivering.setText(delivering_num + "");
          tv_history.setText(history_num + "");
          tv_error.setText(error_num + "");

        }
      }, new DownloadOrders.FailCallback() {

        @Override
        public void onFail() {
          Toast.makeText(getActivity(), R.string.fail_to_commit, Toast.LENGTH_LONG).show();
        }
      });
    }

  }

  private OnFragHomeListener onFragHomeListener;

  public interface OnFragHomeListener {
    void onItemsClicked(int responseCode);

    void contactCustomerService(int responseCode);
  }

  public void setOnFragHomeListener(OnFragHomeListener onFragHomeListener) {
    this.onFragHomeListener = onFragHomeListener;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // 返回成功
    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
      String position = data.getStringExtra("position");
      showAddressDialog(position);
    }
  }

  private void showAddressDialog(String text) {
    /*@setView 装入一个EditView
     */

    final TextView textView = new TextView(getActivity());

    //定义标题样式
    TextView title = new TextView(getActivity());
    title.setText("您当前的地址是");
    title.setPadding(10, 100, 10, 100);
    title.setGravity(Gravity.CENTER);
    title.setTextColor(getResources().getColor(com.hyphenate.easecallkit.R.color.black_deep));
    title.setTextSize(18);

    //定义editview样式
    textView.setGravity(Gravity.CENTER);

    textView.setText(text);
    AlertDialog.Builder inputDialog =
        new AlertDialog.Builder(getActivity());
    inputDialog.setCustomTitle(title).setView(textView);
    inputDialog.setPositiveButton("确定",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
          }
        }).show();
  }
}



