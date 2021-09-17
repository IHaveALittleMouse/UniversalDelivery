package com.charlesgloria.ud.atys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.net.DownloadOneOrder;
import com.charlesgloria.ud.net.Order;
import com.charlesgloria.ud.net.UpdateOrder;

import java.util.ArrayList;
import java.util.List;

import static com.charlesgloria.ud.Config.APP_ID;

public class AtyUpdateOrder extends AppCompatActivity {

  private Spinner point_spinner;
  private Spinner loc_spinner;
  private EditText note_edittext;
  private EditText takenum_edittext;
  private List<String> data_list;
  private ArrayAdapter<String> arr_adapter;

  private String point;
  private String order_num;
  private String loc;
  private String note;
  private String takenum;
  private String phone;
  private String taker;
  private String date;
  private String status;

  //UI组件初始化
  private void bindView() {
    point_spinner = (Spinner) findViewById(R.id.update_point_spinner);
    loc_spinner = (Spinner) findViewById(R.id.update_loc_spinner);
    note_edittext = (EditText) findViewById(R.id.update_note);
    takenum_edittext = (EditText) findViewById(R.id.tv_update_order_takenum);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.aty_update_order);
    getSupportActionBar().hide();
    bindView();

    //---------------------状态栏透明 begin----------------------------------------
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = AtyUpdateOrder.this.getWindow();
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(Color.TRANSPARENT);
    }
    //---------------------状态栏透明 end----------------------------------------

    findViewById(R.id.iv_update_order_back).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
        overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);
      }
    });

    Intent intent = getIntent();
    setIntent(intent);
    order_num = intent.getStringExtra("order_num");

    //数据
    data_list = new ArrayList<String>();
    data_list.add("北门盘锦花园新生活");
    data_list.add("北门盘锦花园内右拐第七家");
    data_list.add("小东门外菜鸟驿站");
    data_list.add("中苑老食堂菜鸟驿站");

    //适配器 android.R.layout.simple_spinner_item
    arr_adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, data_list);
    //设置样式 android.R.layout.simple_spinner_dropdown_item
    arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
    //加载适配器
    point_spinner.setAdapter(arr_adapter);

    //数据
    data_list = new ArrayList<String>();
    SharedPreferences sharedPreferences = getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
    String abr = sharedPreferences.getString(Config.KEY_SAVED_ADDRESS, "");
    data_list.add(abr);
    data_list.add("明德楼");
    data_list.add("文德楼");
    data_list.add("信息中心");

    //适配器
    arr_adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, data_list);
    //设置样式
    arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
    //加载适配器
    loc_spinner.setAdapter(arr_adapter);

    point_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        point = (String) point_spinner.getSelectedItem();
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });

    loc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        loc = (String) loc_spinner.getSelectedItem();
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });


    new DownloadOneOrder(order_num, new DownloadOneOrder.SuccessCallback() {

      @Override
      public void onSuccess(ArrayList<Order> orders) {

        for (Order o : orders) {
          order_num = o.getOrderNumber();
          point = o.getPickPoint();
          takenum = o.getPickNumber();
          loc = o.getArriveAddress();
          note = o.getNote();
          date = o.getOrderTime();
          phone = o.getPhone();
          taker = o.getTaker();
          status = o.getOrderStatus();
          if (note.equals("none")) {
            note = "无";
          }
          setInfo(point, takenum, loc, note);
        }

      }
    }, new DownloadOneOrder.FailCallback() {

      @Override
      public void onFail() {
        Toast.makeText(AtyUpdateOrder.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
      }
    });

    findViewById(R.id.update_summit).setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
//                takenum = takenum_edittext.getText().toString();
//                note = note_edittext.getText().toString();
//                new UpdateOrder(phone,taker,order_num,point,takenum,loc, note,date,status, new
//                UpdateOrder.SuccessCallback() {
//
//                    @Override
//                    public void onSuccess() {
//
//
//                        Toast.makeText(AtyUpdateOrder.this, "修改成功", Toast.LENGTH_LONG).show();
//                        Intent i = new Intent(AtyUpdateOrder.this, AtyMainFrame.class);
//                        i.putExtra("page","order");
//                        startActivity(i);
//                        AtyUpdateOrder.this.overridePendingTransition(R.transition
//                        .switch_slide_in_right, R.transition.switch_still);
//                    }
//                }, new UpdateOrder.FailCallback() {
//
//                    @Override
//                    public void onFail() {
//                        Toast.makeText(AtyUpdateOrder.this, R.string.fail_to_commit, Toast
//                        .LENGTH_LONG).show();
//                    }
//                });
      }
    });


  }

  void setInfo(String point, String takenum, String loc, String note) {
    SpinnerAdapter apsAdapter1 = point_spinner.getAdapter(); //得到SpinnerAdapter对象
    int k1 = apsAdapter1.getCount();
    for (int i = 0; i < k1; i++) {
      if (point.equals(apsAdapter1.getItem(i).toString())) {
//                spinner.setSelection(i,true);// 默认选中项
        point_spinner.setSelection(i);// 默认选中项
        break;
      }
    }
    SpinnerAdapter apsAdapter2 = loc_spinner.getAdapter(); //得到SpinnerAdapter对象
    int k2 = apsAdapter2.getCount();
    for (int i = 0; i < k2; i++) {
      if (loc.equals(apsAdapter2.getItem(i).toString())) {
//                spinner.setSelection(i,true);// 默认选中项
        loc_spinner.setSelection(i);// 默认选中项
        break;
      }
    }
    takenum_edittext.setText(takenum);
    note_edittext.setText(note);
  }

}
