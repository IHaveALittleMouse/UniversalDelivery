package com.charlesgloria.ud.atys;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.widget.SoftHideKeyBoardUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.charlesgloria.ud.Config.APP_ID;

public class AtyMail extends AppCompatActivity {

    private Spinner sp_arriveAddress;
    private Spinner sp_arriveTime;
    private EditText et_note;
    private RadioGroup rg_size;

    private String size;
    private String amount;
    private String arriveTime;
    private String arriveAddress;
    private String note;

    private final String TAG = "AtyMail";

    //UI组件初始化（绑定）
    private void bindView() {
        sp_arriveAddress = findViewById(R.id.sp_atyMail_arriveAddress);
        sp_arriveTime = findViewById(R.id.sp_atyMail_arriveTime);
        et_note = findViewById(R.id.et_atyMail_note);
        rg_size = findViewById(R.id.rg_atyMail_size);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.aty_mail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        SoftHideKeyBoardUtil.assistActivity(this);
        bindView();

        // 绑定回退按钮事件
        findViewById(R.id.iv_atyMail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);
            }
        });

        //---------------------状态栏透明 begin----------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = AtyMail.this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        //---------------------状态栏透明 end----------------------------------------

        //----------------------------收货地点 begin---------------------------------
        // 新建data_list存放数据
        List<String> data_list;
        data_list = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
        String abr = sharedPreferences.getString(Config.KEY_SAVED_ADDRESS, "");
        data_list.add(abr);
        data_list.add("明德楼");
        data_list.add("文德楼");
        data_list.add("信息中心");

        ArrayAdapter<String> arr_adapter;
        //适配器
        arr_adapter = new ArrayAdapter<>(this, R.layout.item_spinner, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        //加载适配器
        Log.i(TAG,arr_adapter+"");
        sp_arriveAddress.setAdapter(arr_adapter);
        sp_arriveAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                arriveAddress = (String) sp_arriveAddress.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //----------------------------收货地点 end---------------------------------

        //----------------------------收货时间 begin---------------------------------
        //数据
        data_list = new ArrayList<>();
        data_list.add("18：30~20：30");
        data_list.add("20：30~22：00");

        //适配器
        arr_adapter = new ArrayAdapter<>(this, R.layout.item_spinner, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        //加载适配器
        sp_arriveTime.setAdapter(arr_adapter);
        sp_arriveTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                arriveTime = (String) sp_arriveTime.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //----------------------------收货时间 end---------------------------------

        findViewById(R.id.btn_atyMail_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                下单时间 order_time
//                信任好友 trust_friend
//                快递体积 size(L M S)
//                快递数量 amount(int)
//                收货地点 arrive_address
//                收货时间 arrive_time
//                快递点   pick_point
//                取货号   pick_number
//                备注     note

                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String orderTime = sDateFormat.format(new java.util.Date());

                // 获得phoneNum
                note = et_note.getText().toString();
                if (note.equals("")) {
                    note = "none";
                }

//                RadioButton rb_pickPattern = (RadioButton)findViewById(rg_pickPattern.getCheckedRadioButtonId());
//                String pickPattern = rb_pickPattern.getText().toString();

//                if (rg_pickPattern.getCheckedRadioButtonId() == R.id.rb_atyFetch_pickPattern_friend) {
//                    trustFriend = "CHARLES";
//                } else trustFriend = "none";

                switch (rg_size.getCheckedRadioButtonId()) {
                    case R.id.rb_atyFetch_size_small:
                        size = "S";
                        break;
                    case R.id.rb_atyFetch_size_medium:
                        size = "M";
                        break;
                    case R.id.rb_atyFetch_size_large:
                        size = "L";
                        break;
                }
//                if (rg_orderPattern.getCheckedRadioButtonId() == R.id.rb_atyFetch_orderPattern_temp) {
//                    pickNumber = et_pickNumber.getText().toString();
//                } else {
//                    pickNumber = "none";
//                }
//
//                amount = et_amount.getText().toString();
//                SharedPreferences sharedPreferences = getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
//                String phone = sharedPreferences.getString(Config.KEY_PHONE_NUM, "");
//
//                if (rg_orderPattern.getCheckedRadioButtonId() == R.id.rb_atyFetch_orderPattern_temp && (pickNumber.equals("") || pickNumber == null)) {
//                    Toast.makeText(AtyFetch.this, "取货号不能为空！", Toast.LENGTH_LONG).show();
//                } else {
//                    new UploadOrder(phone, orderTime, trustFriend, size, amount, arriveAddress, arriveTime, pickPoint, pickNumber, note, new UploadOrder.SuccessCallback() {
//
//                        @Override
//                        public void onSuccess() {
//
//                            //-------------------下单成功 给自己发一条推送-----------------------
//                            SharedPreferences sharedPreferences = AtyFetch.this.getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
//                            final String deviceId = sharedPreferences.getString(Config.KEY_DEVICEID, "");
//
//                            Runnable networkTask = new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    // TODO
//                                    // 在这里进行 http request.网络请求相关操作
//                                    PushMessage pushMessage = new PushMessage();
//                                    try {
//                                        pushMessage.PushToSelf(Config.getCachedDeviceID(getApplicationContext()), "下单成功！", "UDers正在努力派送中…");
//                                    } catch (ClientException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            };
//                            Thread thread = new Thread(networkTask);
//                            thread.start();
//                            //---------------------------推送结束-----------------------------
//
//                            Toast.makeText(AtyFetch.this, "提交成功！", Toast.LENGTH_LONG).show();
//                            Intent i = new Intent(AtyFetch.this, AtyMainFrame.class);
//                            i.putExtra("page", "order");
//                            startActivity(i);
//                            finish();
//
//                        }
//                    }, new UploadOrder.FailCallback() {
//
//                        @Override
//                        public void onFail() {
//                            Toast.makeText(AtyFetch.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
            }
        });

    }

}
