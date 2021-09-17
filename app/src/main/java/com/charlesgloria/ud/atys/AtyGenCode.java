package com.charlesgloria.ud.atys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.charlesgloria.ud.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class AtyGenCode extends AppCompatActivity {

  private ImageView code_view;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.aty_gen_code);

    getSupportActionBar().hide();

    Intent intent = getIntent();
    setIntent(intent);
    String code = intent.getStringExtra("code");

    //---------------------状态栏透明 begin----------------------------------------
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = AtyGenCode.this.getWindow();
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(Color.TRANSPARENT);
    }
    //---------------------状态栏透明 end----------------------------------------

    findViewById(R.id.code_back).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//                Intent i = new Intent(AtyGenCode.this, AtyMainFrame.class);
//                i.putExtra("page","order");
//                startActivity(i);
        finish();
        overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);
      }
    });

    code_view = (ImageView) findViewById(R.id.code);
    String contentEtString = code;
    if (TextUtils.isEmpty(contentEtString)) {
//            Toast.makeText(this, "contentEtString²»ÄÜÎª¿Õ", Toast.LENGTH_SHORT).show();
      return;
    }

    Bitmap bitmap = null;
    try {
      BitMatrix matrix = new QRCodeWriter().encode(contentEtString, BarcodeFormat.QR_CODE, 400, 400);
      bitmap = generateQRCode(matrix);
    } catch (WriterException e) {
      e.printStackTrace();
    }
    if (bitmap != null) {
      code_view.setImageBitmap(bitmap);
    }
  }

  private Bitmap generateQRCode(BitMatrix matrix) {
    int width = matrix.getWidth();
    int height = matrix.getHeight();
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    for (int x = 0; x < matrix.getHeight(); x++) {
      for (int y = 0; y < matrix.getWidth(); y++) {
        bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
      }
    }
    return bitmap;
  }
}
