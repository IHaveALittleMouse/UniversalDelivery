package com.charlesgloria.ud.atys;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.charlesgloria.ud.R;

/**
 * Created by Administrator on 2017/11/25 0025.
 */

public class AtyLocateSchool extends Activity {

  private ListView listSchool;  // ListView控件
  private TextView txtSchool;
  private TextView txtLocation;
  //private ImageView

  private void bindView() {
    txtSchool = (TextView) findViewById(R.id.aty_locate_address_school);
    txtLocation = (TextView) findViewById(R.id.aty_locate_address_tv_current_location);
  }

  private void addItem() {
  }

  private void deleteItem() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  private void requestLocation() {
  }

  private void initLocation() {
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                         int[] grantResults) {
    if (requestCode == 1) {
      if (grantResults.length > 0) {
        for (int result : grantResults) {
          if (result != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
            finish();
            return;
          }
        }
        requestLocation();
      } else {
        Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
        finish();
      }
    }
  }

}
