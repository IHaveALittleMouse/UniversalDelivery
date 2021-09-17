package com.charlesgloria.ud.atys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.charlesgloria.ud.R;

public class AtyUnlog extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.aty_unlog);
    getSupportActionBar().hide();

    //---------------------状态栏透明 begin----------------------------------------
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = AtyUnlog.this.getWindow();
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(Color.TRANSPARENT);
    }
    //---------------------状态栏透明 end----------------------------------------

    findViewById(R.id.to_login).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(AtyUnlog.this, AtyLogin.class);
        startActivity(intent);
        overridePendingTransition(R.transition.switch_slide_in_right, R.transition.switch_still);
      }
    });

    findViewById(R.id.back_to_home).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
        overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);
      }
    });
  }
}
