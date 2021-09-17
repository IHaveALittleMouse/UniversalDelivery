package com.charlesgloria.ud.atys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.adapter.AbsCommonAdapter;
import com.charlesgloria.ud.adapter.AbsViewHolder;
import com.charlesgloria.ud.bean.TableModel;
import com.charlesgloria.ud.net.CompleteOrder;
import com.charlesgloria.ud.net.DownloadTakenOrders;
import com.charlesgloria.ud.net.Order;
import com.charlesgloria.ud.utils.WeakHandler;
import com.charlesgloria.ud.widget.SyncHorizontalScrollView;
import com.charlesgloria.ud.widget.pullrefresh.AbPullToRefreshView;
import com.example.administrator.zxinglibrary.android.CaptureActivity;
import com.example.administrator.zxinglibrary.bean.ZxingConfig;
import com.example.administrator.zxinglibrary.common.Constant;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

//        手机号   phone
//        信任好友 trust_friend
//        收货地点 arrive_address
//        收货时间 arrive_time
public class AtyStaffOnly extends AppCompatActivity {

  private String TAG = "atystaffOnly";
  private ImageView scanner;
  private int REQUEST_CODE_SCAN = 111;

  /**
   * 用于存放标题的id,与textview引用
   */
  private SparseArray<TextView> mTitleTvArray;
  //表格部分
  private TextView tv_table_title_left;
  private LinearLayout right_title_container;
  private ListView leftListView;
  private ListView rightListView;
  private AbsCommonAdapter<TableModel> mLeftAdapter, mRightAdapter;
  private SyncHorizontalScrollView titleHorScv;
  private SyncHorizontalScrollView contentHorScv;
  private AbPullToRefreshView pulltorefreshview;
  private int pageNo = 0;
  private WeakHandler mHandler = new WeakHandler();
  private Context mContext;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.aty_staff_only);

    getSupportActionBar().hide();

    init();

    //---------------------状态栏透明 begin----------------------------------------
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = AtyStaffOnly.this.getWindow();
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(Color.TRANSPARENT);
    }
    //---------------------状态栏透明 end----------------------------------------

    findViewById(R.id.iv_staff_back).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
        overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);
      }
    });

    // 绑定按钮到扫描二维码
    //scanner
    scanner = (ImageView) findViewById(R.id.iv_staff_scanner);
//        result = view.findViewById(R.id.result_tv);
    scanner.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AndPermission.with(AtyStaffOnly.this)
            .permission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).callback(new PermissionListener() {
          @Override
          public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {

            Intent intent = new Intent(AtyStaffOnly.this, CaptureActivity.class);

            /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
             * 也可以不传这个参数
             * 不传的话  默认都为默认不震动  其他都为true
             * */

            ZxingConfig config = new ZxingConfig();
            config.setPlayBeep(false);
            config.setShake(true);
            intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);

            startActivityForResult(intent, REQUEST_CODE_SCAN);

          }

          @Override
          public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            Uri packageURI = Uri.parse("package:" + AtyStaffOnly.this.getPackageName());
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

            Toast.makeText(AtyStaffOnly.this, "没有权限无法扫描", Toast.LENGTH_LONG).show();
          }

        }).start();

      }

    });

  }

  // 处理startActivityForResult的返回值
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent imgReturnIntent) {
    super.onActivityResult(requestCode, resultCode, imgReturnIntent);

    // 扫描二维码/条码回传
    if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
      if (imgReturnIntent != null) {
        //-------------------下单成功 给自己发一条推送-----------------------

//                Runnable networkTask = new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // TODO
//                        // 在这里进行 http request.网络请求相关操作
//                        PushMessage pushMessage = new PushMessage();
//                        try {
//                            pushMessage.PushToSelf(Config.getCachedDeviceID(AtyStaffOnly.this),
//                            "下单成功！", "UDers正在努力派送中…");
//                        } catch (ClientException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//                Thread thread = new Thread(networkTask);
//                thread.start();
        //---------------------------推送结束-----------------------------

        String content = imgReturnIntent.getStringExtra(Constant.CODED_CONTENT);
//                result.setText("扫描结果为：" + content);
        new CompleteOrder(content, new CompleteOrder.SuccessCallback() {

          @Override
          public void onSuccess() {

            Toast.makeText(AtyStaffOnly.this, "完成订单！", Toast.LENGTH_LONG).show();
            Intent i = new Intent(AtyStaffOnly.this, AtyMainFrame.class);
            i.putExtra("page", "me");
            startActivity(i);
          }
        }, new CompleteOrder.FailCallback() {

          @Override
          public void onFail() {
            Toast.makeText(AtyStaffOnly.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
          }
        });
      }
    }

    super.onActivityResult(requestCode, resultCode, imgReturnIntent);
  }

  public void init() {
    mContext = getApplicationContext();
    findByid();
    setListener();
    setData();
  }

  public void findByid() {
    pulltorefreshview = (AbPullToRefreshView) findViewById(R.id.pulltorefreshview);
//        pulltorefreshview.setPullRefreshEnable(false);
    tv_table_title_left = (TextView) findViewById(R.id.tv_table_title_left);
    tv_table_title_left.setText("序号");
    leftListView = (ListView) findViewById(R.id.left_container_listview);
    rightListView = (ListView) findViewById(R.id.right_container_listview);
    right_title_container = (LinearLayout) findViewById(R.id.right_title_container);
    getLayoutInflater().inflate(R.layout.table_right_title, right_title_container);
    titleHorScv = (SyncHorizontalScrollView) findViewById(R.id.title_horsv);
    contentHorScv = (SyncHorizontalScrollView) findViewById(R.id.content_horsv);
    // 设置两个水平控件的联动
    titleHorScv.setScrollView(contentHorScv);
    contentHorScv.setScrollView(titleHorScv);
    findTitleTextViewIds();
    initTableView();
  }

  /**
   * 初始化标题的TextView的item引用
   */
  private void findTitleTextViewIds() {
    mTitleTvArray = new SparseArray<>();
    for (int i = 0; i <= 20; i++) {
      try {
        Field field = R.id.class.getField("tv_table_title_" + 0);
        int key = field.getInt(new R.id());
        TextView textView = (TextView) findViewById(key);
        mTitleTvArray.put(key, textView);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void initTableView() {
    mLeftAdapter = new AbsCommonAdapter<TableModel>(mContext, R.layout.table_left_item) {
      @Override
      public void convert(AbsViewHolder helper, TableModel item, int pos) {
        TextView tv_table_content_left = helper.getView(R.id.tv_table_content_item_left);
        tv_table_content_left.setText(item.getLeftTitle());
      }
    };
    mRightAdapter = new AbsCommonAdapter<TableModel>(mContext, R.layout.table_right_item) {
      @Override
      public void convert(AbsViewHolder helper, TableModel item, int pos) {
        TextView tv_table_content_right_item0 = helper.getView(R.id.tv_table_content_right_item0);
        TextView tv_table_content_right_item1 = helper.getView(R.id.tv_table_content_right_item1);
        TextView tv_table_content_right_item2 = helper.getView(R.id.tv_table_content_right_item2);
        TextView tv_table_content_right_item3 = helper.getView(R.id.tv_table_content_right_item3);
        TextView tv_table_content_right_item4 = helper.getView(R.id.tv_table_content_right_item4);
        TextView tv_table_content_right_item5 = helper.getView(R.id.tv_table_content_right_item5);
        TextView tv_table_content_right_item6 = helper.getView(R.id.tv_table_content_right_item6);
        TextView tv_table_content_right_item7 = helper.getView(R.id.tv_table_content_right_item7);
        TextView tv_table_content_right_item8 = helper.getView(R.id.tv_table_content_right_item8);
        TextView tv_table_content_right_item9 = helper.getView(R.id.tv_table_content_right_item9);
        TextView tv_table_content_right_item10 = helper.getView(R.id.tv_table_content_right_item10);
        TextView tv_table_content_right_item11 = helper.getView(R.id.tv_table_content_right_item11);

        tv_table_content_right_item0.setText(item.getText0());
        tv_table_content_right_item1.setText(item.getText1());
        tv_table_content_right_item2.setText(item.getText2());
        tv_table_content_right_item3.setText(item.getText3());
        tv_table_content_right_item4.setText(item.getText4());
        tv_table_content_right_item5.setText(item.getText5());
        tv_table_content_right_item6.setText(item.getText6());
        tv_table_content_right_item7.setText(item.getText7());
        tv_table_content_right_item8.setText(item.getText8());
        tv_table_content_right_item9.setText(item.getText9());
        tv_table_content_right_item10.setText(item.getText10());
        tv_table_content_right_item11.setText(item.getText11());

        //部分行设置颜色凸显
        item.setTextColor(tv_table_content_right_item0, item.getText0());
        item.setTextColor(tv_table_content_right_item1, item.getText1());
        item.setTextColor(tv_table_content_right_item6, item.getText6());
        item.setTextColor(tv_table_content_right_item8, item.getText8());

        for (int i = 0; i < 12; i++) {
          View view = ((LinearLayout) helper.getConvertView()).getChildAt(i);
          view.setVisibility(View.VISIBLE);
        }
      }
    };
    leftListView.setAdapter(mLeftAdapter);
    rightListView.setAdapter(mRightAdapter);
  }


  public void setListener() {
    pulltorefreshview.setOnHeaderRefreshListener(new AbPullToRefreshView.OnHeaderRefreshListener() {
      @Override
      public void onHeaderRefresh(AbPullToRefreshView view) {
        mHandler.postDelayed(new Runnable() {
          @Override
          public void run() {
            pageNo = 0;
            doGetDatas(0, Config.REFRESH_DATA);
          }
        }, 1000);
      }
    });
    pulltorefreshview.setOnFooterLoadListener(new AbPullToRefreshView.OnFooterLoadListener() {
      @Override
      public void onFooterLoad(AbPullToRefreshView view) {
        mHandler.postDelayed(new Runnable() {
          @Override
          public void run() {
            doGetDatas(pageNo, Config.LOAD_DATA);
          }
        }, 1000);
      }

    });
    leftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //跳转界面
//                Toast.makeText(AtyStaffOnly.this, "打开某条记录的单独详情" + id, Toast.LENGTH_SHORT).show();
      }
    });
  }

  public void setData() {
    doGetDatas(0, Config.REFRESH_DATA);
  }

  //模拟网络请求
  public void doGetDatas(int pageno, int state) {
    final List<Order> orderList = new ArrayList<>();
    final int mystate = state;
    new DownloadTakenOrders(Config.getCachedPhoneNum(this),
        new DownloadTakenOrders.SuccessCallback() {

      @Override
      public void onSuccess(ArrayList<Order> orders) {
        Log.i(TAG, "onsuccess");
        int id = 1;
        for (Order o : orders) {
          orderList.add(o);
          o.setId(id + "");
          id++;
          Log.i(TAG, "onsuccess" + id);

        }
        setDatas(orderList, mystate);
        if (mystate == Config.REFRESH_DATA) {
          pulltorefreshview.onHeaderRefreshFinish();
        } else {
          pulltorefreshview.onFooterLoadFinish();
        }
      }
    }, new DownloadTakenOrders.FailCallback() {

      @Override
      public void onFail() {
        Log.i(TAG, "onfail");

        Toast.makeText(AtyStaffOnly.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
      }
    });
//        for(int i=1+pageno*20;i<20*(pageno+1)+1;i++){
//            orderList.add(new Order(i + ""));
//        }

//        setDatas(orderList, state);
  }

  private void setDatas(List<Order> orderList, int type) {
    if (orderList.size() > 0) {
      List<TableModel> mDatas = new ArrayList<>();
      for (int i = 0; i < orderList.size(); i++) {
        Order order = orderList.get(i);
        TableModel tableMode = new TableModel();
//                tableMode.setOrgCode(order.getOrgCode());
        tableMode.setLeftTitle(order.getId());
        //列0内容
        if (order.getArriveAddress().equals("null") || order.getArriveAddress().equals("none")) {
          tableMode.setText0("无");
        } else {
          tableMode.setText0(order.getArriveAddress() + "");
        }
        //列1内容
        if (order.getArriveTime().equals("null") || order.getArriveTime().equals("none")) {
          tableMode.setText1("无");
        } else {
          tableMode.setText1(order.getArriveTime() + "");
        }
        //列2内容
        if (order.getSize().equals("null") || order.getSize().equals("none")) {
          tableMode.setText2("无");
        } else {
          switch (order.getSize()) {
            case "S":
              tableMode.setText2("小");
              break;
            case "M":
              tableMode.setText2("中");
              break;
            case "L":
              tableMode.setText2("大");
              break;
            default:
              tableMode.setText2(order.getSize() + "");
          }
        }

        //列3内容
        if (order.getNote().equals("null") || order.getNote().equals("none")) {
          tableMode.setText3("无");
        } else {
          tableMode.setText3(order.getNote() + "");
        }
        //列4内容
        if (order.getPickPoint().equals("null") || order.getPickPoint().equals("none") || order.getPickPoint() == null) {
          tableMode.setText4("无");
        } else {
          tableMode.setText4(order.getPickPoint() + "");//
        }
        //列5内容
        if (order.getPickNumber().equals("null") || order.getPickNumber().equals("none") || order.getPickNumber() == null) {
          tableMode.setText5("无");
        } else {
          tableMode.setText5(order.getPickNumber() + "");//
        }
        //列6内容
        if (order.getTrust_friend().equals("null") || order.getTrust_friend().equals("none") || order.getTrust_friend() == null) {
          tableMode.setText6("无");
        } else {
          tableMode.setText6(order.getTrust_friend() + "");//
        }
        //列7内容
        if (order.getOrderStatus().equals("null") || order.getOrderStatus().equals("none") || order.getOrderStatus() == null) {
          tableMode.setText7("无");
        } else {
          if (order.getOrderStatus().equals("0")) {
            tableMode.setText7("已结单");//
          } else {
            tableMode.setText7("派送中");//
          }
        }
        //列8内容
        if (order.getPhone().equals("null") || order.getPhone().equals("none") || order.getPhone() == null) {
          tableMode.setText8("无");
        } else {
          tableMode.setText8(order.getPhone() + "");//
        }
        //列9内容
        if (order.getOrderNumber().equals("null") || order.getOrderNumber().equals("none") || order.getOrderNumber() == null) {
          tableMode.setText9("无");
        } else {
          tableMode.setText9(order.getOrderNumber() + "");//
        }
        //列10内容
        if (order.getOrderTime().equals("null") || order.getOrderTime().equals("none") || order.getOrderTime() == null) {
          tableMode.setText10("无");
        } else {
          tableMode.setText10(order.getOrderTime() + "");//
        }
        //列11内容
        if (order.getTaker().equals("null") || order.getTaker().equals("none") || order.getTaker() == null) {
          tableMode.setText11("无");
        } else {
          if (order.getTaker().equals("0")) {
            tableMode.setText11("暂无");//
          } else {
            tableMode.setText11(order.getTaker() + "");//
          }
        }

        mDatas.add(tableMode);
      }
      boolean isMore;
      if (type == Config.LOAD_DATA) {
        isMore = true;
      } else {
        isMore = false;
      }
      mLeftAdapter.addData(mDatas, isMore);
      mRightAdapter.addData(mDatas, isMore);
      //加载数据成功，增加页数
      pageNo++;
      if (mDatas.size() <= mDatas.size()) {
        pulltorefreshview.setLoadMoreEnable(false);
      }
      mDatas.clear();
    } else {
      //数据为null
      if (type == Config.REFRESH_DATA) {
        mLeftAdapter.clearData(true);
        mRightAdapter.clearData(true);
        //显示数据为空的视图
        //                mEmpty.setShowErrorAndPic(getString(R.string.empty_null), 0);
      } else if (type == Config.LOAD_DATA) {
        Toast.makeText(mContext, "请求json失败", Toast.LENGTH_SHORT).show();
      }
    }
  }

}
