package com.charlesgloria.ud.atys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.net.DownloadAddress;
import com.charlesgloria.ud.net.UploadAddress;
import com.charlesgloria.ud.widget.SoftHideKeyBoardUtil;

import java.util.ArrayList;
import java.util.List;

import static com.charlesgloria.ud.Config.APP_ID;

/**
 * Created by Administrator on 2017/11/7 0007.
 */

public class AtyAddressMng extends Activity {

  private Spinner area_spinner;
  private Spinner building_spinner;
  private EditText room_et;
  private List<String> data_list;
  private ArrayAdapter<String> arr_adapter;
  private TextView address;
  private String school = "中国UD大学";
  private String area = "";
  private String building = "";
  private String room = "";

  //UI组件初始化
  private void bindView() {

    area_spinner = (Spinner) findViewById(R.id.sp_mng_area);
    building_spinner = (Spinner) findViewById(R.id.sp_mng_building);
    room_et = (EditText) findViewById(R.id.et_mng_room);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Config.loginStatus == 0) {
      setContentView(R.layout.aty_unlog);
      findViewById(R.id.to_login).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(AtyAddressMng.this, AtyLogin.class);
          startActivity(intent);
          overridePendingTransition(R.transition.switch_slide_in_right, R.transition.switch_still);
        }
      });

      findViewById(R.id.back_to_home).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent i = new Intent(AtyAddressMng.this, AtyMainFrame.class);
          i.putExtra("page", "me");
          startActivity(i);
          overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);
        }
      });
    } else {
      setContentView(R.layout.aty_address_mng);
      //键盘不覆盖，需放在setContentView之后
      SoftHideKeyBoardUtil.assistActivity(this);

      //---------------------状态栏透明 begin----------------------------------------
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Window window = AtyAddressMng.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
      }
      //---------------------状态栏透明 end----------------------------------------

      bindView();
      findViewById(R.id.iv_mng_back).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          finish();
          overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);
        }
      });

      //重新定位学校点击事件
//            findViewById(R.id.btn_Relocate).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(AtyAddressMng.this,AtyLocateSchool.class);
//                    startActivity(i);
//                }
//            });

      //数据
      data_list = new ArrayList<String>();
      data_list.add("东区");
      data_list.add("中区");
      data_list.add("西区");

      //适配器
      arr_adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, data_list);
      //设置样式
      arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
      //加载适配器
      area_spinner.setAdapter(arr_adapter);

      //数据
      data_list = new ArrayList<String>();
      data_list.add("20栋");
      data_list.add("21栋");
      data_list.add("22栋");
      data_list.add("23栋");
      data_list.add("24栋");
      data_list.add("25栋");
      data_list.add("26栋");
      data_list.add("27栋");
      data_list.add("28栋");
      data_list.add("29栋");
      //适配器
      arr_adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, data_list);
      //设置样式
      arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
      //加载适配器
      building_spinner.setAdapter(arr_adapter);

      //show current address!!!!
      address = (TextView) findViewById(R.id.tv_mng_address);
      // 获得phoneNum
      SharedPreferences sharedPreferences = getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
      String phone = sharedPreferences.getString(Config.KEY_PHONE_NUM, "");
      new DownloadAddress(phone, new DownloadAddress.SuccessCallback() {

        @Override
        public void onSuccess(String school, String area, String building, String room) {
          address.setText(school + area + building + room);
          setSpinner(area, building, room);
        }
      }, new DownloadAddress.FailCallback() {

        @Override
        public void onFail() {
          Toast.makeText(AtyAddressMng.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
        }
      });


      //change address!!!!
      area_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
          area = (String) area_spinner.getSelectedItem();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
      });

      building_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
          building = (String) building_spinner.getSelectedItem();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
      });
      findViewById(R.id.btn_mng_submit).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          room = room_et.getText().toString();
          // 获得phoneNum
          SharedPreferences sharedPreferences = getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
          String phone = sharedPreferences.getString(Config.KEY_PHONE_NUM, "");
          String abr = area + building + room;
          Config.cacheAddress(AtyAddressMng.this, abr);
          new UploadAddress(phone, school, area, building, room,
              new UploadAddress.SuccessCallback() {

            @Override
            public void onSuccess() {

              Toast.makeText(AtyAddressMng.this, "修改成功！", Toast.LENGTH_LONG).show();
              Intent i = new Intent(AtyAddressMng.this, AtyAddressMng.class);
              startActivity(i);
              finish();

            }
          }, new UploadAddress.FailCallback() {

            @Override
            public void onFail() {
              Toast.makeText(AtyAddressMng.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
            }
          });
        }
      });
    }
  }


  void setSpinner(String area, String building, String room) {
    SpinnerAdapter apsAdapter1 = area_spinner.getAdapter(); //得到SpinnerAdapter对象
    int k1 = apsAdapter1.getCount();
    for (int i = 0; i < k1; i++) {
      if (area.equals(apsAdapter1.getItem(i).toString())) {
//                spinner.setSelection(i,true);// 默认选中项
        area_spinner.setSelection(i);// 默认选中项
        break;
      }
    }
    SpinnerAdapter apsAdapter2 = building_spinner.getAdapter(); //得到SpinnerAdapter对象
    int k2 = apsAdapter2.getCount();
    for (int i = 0; i < k2; i++) {
      if (building.equals(apsAdapter2.getItem(i).toString())) {
//                spinner.setSelection(i,true);// 默认选中项
        building_spinner.setSelection(i);// 默认选中项
        break;
      }
    }
    room_et.setText(room);
  }
}
