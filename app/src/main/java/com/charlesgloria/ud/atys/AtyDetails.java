package com.charlesgloria.ud.atys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.net.DownloadOneOrder;
import com.charlesgloria.ud.net.Order;
import com.charlesgloria.ud.net.UpdateOrder;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.net.DownloadHXFriends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import static com.charlesgloria.ud.Config.APP_ID;

//        订单号   order_number
//        下单时间 order_time
//        信任好友 trust_friend
//        快递体积 size(L M S)
//        收货地点 arrive_address
//        收货时间 arrive_time
//        快递点   pick_point
//        取货号   pick_number
//        派送员   taker
//        备注     note
//        状态     order_status(int)

public class AtyDetails extends AppCompatActivity {

    private TextView tv_orderNumber;
    private TextView tv_orderTime;
    private TextView tv_trustFriend;
    private TextView tv_size;
    private TextView tv_arriveAddress;
    private TextView tv_arriveTime;
    private TextView tv_pickPoint;
    private TextView tv_pickNumber;
    private TextView tv_taker;
    private TextView tv_note;
    private TextView tv_orderStatus;
    private TextView tv_change;
    private TextView tv_contact_taker;
    private TextView tv_orderPattern;

    private LinearLayout ll_orderPattern_temp;
    private LinearLayout ll_pickPattern_self;
    private LinearLayout ll_pickPattern_friend;

    private String phone;
    private String orderNumber;
    private String orderTime;
    private String pickPoint;
    private String size;
    private String amount;
    private String arriveTime;
    private String arriveAddress;
    private String trustFriend;
    private String note;
    private String taker;
    private String orderStatus;
    private String pickNumber;

    private boolean changed = false; //false:unchanged; true:changed
    private boolean trustOrderDetail = false; //false:ordinary order detail; true:trust order detail

    //UI组件初始化
    private void bindView() {
        tv_orderNumber = (TextView) findViewById(R.id.tv_atyDetails_orderNumber);
        tv_orderTime = (TextView) findViewById(R.id.tv_atyDetails_orderTime);
        tv_trustFriend = (TextView) findViewById(R.id.tv_atyDetails_trustFriend);
        tv_size = (TextView) findViewById(R.id.tv_atyDetails_size);
        tv_arriveAddress = (TextView) findViewById(R.id.tv_atyDetails_arriveAddress);
        tv_arriveTime = (TextView) findViewById(R.id.tv_atyDetails_arriveTime);
        tv_pickPoint = (TextView) findViewById(R.id.tv_atyDetails_pickPoint);
        tv_pickNumber = (TextView) findViewById(R.id.tv_atyDetails_pickNumber);
        tv_taker = (TextView) findViewById(R.id.tv_atyDetails_taker);
        tv_note = (TextView) findViewById(R.id.tv_atyDetails_note);
        tv_orderStatus = (TextView) findViewById(R.id.tv_atyDetails_orderStatus);
        tv_change = (TextView) findViewById(R.id.tv_atyDetails_change);
        tv_contact_taker = (TextView) findViewById(R.id.tv_atyDetails_contacttaker);
        tv_orderPattern = (TextView) findViewById(R.id.tv_atyDetails_orderPattern);
        ll_orderPattern_temp = (LinearLayout) findViewById(R.id.ll_atyDetails_orderPattern_temp);
        ll_pickPattern_self = (LinearLayout) findViewById(R.id.ll_atyDetails_pickPattern_self);
        ll_pickPattern_friend = (LinearLayout) findViewById(R.id.ll_atyDetails_pickPattern_friend);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aty_details);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        setIntent(intent);
        orderNumber = intent.getStringExtra("orderNumber");
        if (intent.getStringExtra("pattern").equals("trust orders")) {
            trustOrderDetail = true;
        }

        bindView();
        findViewById(R.id.iv_atyDetails_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);
            }
        });

        //---------------------状态栏透明 begin----------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = AtyDetails.this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        //---------------------状态栏透明 end----------------------------------------


        SharedPreferences sharedPreferences = AtyDetails.this.getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
        phone = sharedPreferences.getString(Config.KEY_PHONE_NUM, "");
        fresh();

        findViewById(R.id.btn_atyDetails_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AtyDetails.this, AtyGenCode.class);
                intent.putExtra("code", orderNumber);
                startActivity(intent);
                AtyDetails.this.overridePendingTransition(R.transition.switch_slide_in_right, R.transition.switch_still);
            }
        });

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String items[][] = new String[1][2];
                items[0] = new String[2];
                items[0][0] = "自己拿";
                items[0][1] = "信任好友代拿";
                dialogChoosePattern(items);
            }
        });

        tv_contact_taker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogChoice(taker);
            }
        });
    }

    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    private void dialogChoosePattern(final String[][] items) {
        final String[] pattern = new String[1];
        //单选对话窗口
        AlertDialog.Builder builder = new AlertDialog.Builder(this, 3);

        //定义标题样式
        TextView title = new TextView(this);
        title.setText("取货方式");
        title.setPadding(40, 10, 10, 10);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(getResources().getColor(R.color.text_clo));
        title.setTextSize(20);

        //设置图片
        Drawable drawable = getResources().getDrawable(R.drawable.item_trustfriend);
        drawable.setBounds(10, 10, drawable.getMinimumWidth(), drawable.getMinimumHeight());//这句一定要加
        title.setCompoundDrawables(drawable, null, null, null);//setCompoundDrawables用来设置图片显示在文本的哪一端
        title.setCompoundDrawablePadding(30);//设置文字和图片间距

        if (items[0].length == 0) {
            items[0] = new String[10];
            for (int i = 0; i < 10; i++) {
                items[0][i] = "" + i;
            }
        } else {
            for (int i = 0; i < items[0].length; i++) {
            }
        }
        //使用自定义title
        builder.setCustomTitle(title);
        try {
            // item[0] 是一个一维字符串数组，里面的元素都必须全部初始化，若有一个及以上元素为null，会抛出NullPointException异常
            builder.setSingleChoiceItems(items[0], -1,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

//                        Toast.makeText(AtyFetch.this, items[which],
//                                Toast.LENGTH_SHORT).show();
                            pattern[0] = items[0][which];
                        }
                    });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
//                    Toast.makeText(AtyFetch.this, trustfriend[0], Toast.LENGTH_SHORT)
//                            .show();
                    if (pattern[0].equals("自己拿")) {
                        trustFriend = "none";
                        changed = true;
                        Log.i("atydetails", changed + "");
                        new UpdateOrder(orderNumber, orderTime, trustFriend, size, arriveAddress, arriveTime, pickPoint, pickNumber, note, new UpdateOrder.SuccessCallback() {

                            @Override
                            public void onSuccess() {

                                Toast.makeText(AtyDetails.this, "修改成功！", Toast.LENGTH_LONG).show();
                                fresh();

                            }
                        }, new UpdateOrder.FailCallback() {

                            @Override
                            public void onFail() {
                                Toast.makeText(AtyDetails.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        new DownloadHXFriends(Config.getCachedPhoneNum(AtyDetails.this), new DownloadHXFriends.SuccessCallback() {
                            @Override
                            public void onSuccess(ArrayList<String> friendsName) {
                                Map<String, EaseUser> arrContacts = new HashMap<>();
                                String items[][] = new String[1][friendsName.size()];
                                items[0] = new String[friendsName.size()];
                                for (int i = 0; i < friendsName.size(); i++) {
                                    EaseUser user = new EaseUser(friendsName.get(i));
                                    arrContacts.put(user.getUsername(), user);
                                    String fname = arrContacts.get(user.getUsername()).getUsername();
                                    items[0][i] = fname;
                                }
                                dialogChooseFriend(items); // 单选
                            }
                        }, new DownloadHXFriends.FailCallback() {
                            @Override
                            public void onFail() {
                                Toast.makeText(AtyDetails.this, "获取好友列表失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.create().show();
    }

    private void dialogChooseFriend(final String[][] items) {
        final String[] trustfriend = new String[1];
        //单选对话窗口
        AlertDialog.Builder builder = new AlertDialog.Builder(this, 3);

        //定义标题样式
        TextView title = new TextView(this);
        title.setText("好友列表");
        title.setPadding(40, 10, 10, 10);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(getResources().getColor(R.color.text_clo));
        title.setTextSize(20);

        //设置图片
        Drawable drawable = getResources().getDrawable(R.drawable.item_trustfriend);
        drawable.setBounds(10, 10, drawable.getMinimumWidth(), drawable.getMinimumHeight());//这句一定要加
        title.setCompoundDrawables(drawable, null, null, null);//setCompoundDrawables用来设置图片显示在文本的哪一端
        title.setCompoundDrawablePadding(30);//设置文字和图片间距

        if (items[0].length == 0) {
            items[0] = new String[10];
            for (int i = 0; i < 10; i++) {
                items[0][i] = "" + i;
            }
        } else {
            for (int i = 0; i < items[0].length; i++) {
            }
        }
        //使用自定义title
        builder.setCustomTitle(title);
        try {
            // item[0] 是一个一维字符串数组，里面的元素都必须全部初始化，若有一个及以上元素为null，会抛出NullPointException异常
            builder.setSingleChoiceItems(items[0], -1,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

//                        Toast.makeText(AtyFetch.this, items[which],
//                                Toast.LENGTH_SHORT).show();
                            if (items[0][which].length() > 0) {
                                trustfriend[0] = items[0][which];
                            } else {
                                trustfriend[0] = "请选择信任好友";
                            }
                        }
                    });
            builder.setPositiveButton("信任TA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
//                    Toast.makeText(AtyFetch.this, trustfriend[0], Toast.LENGTH_SHORT)
//                            .show();
                    trustFriend = trustfriend[0];
                    changed = true;
                    new UpdateOrder(orderNumber, orderTime, trustFriend, size, arriveAddress, arriveTime, pickPoint, pickNumber, note, new UpdateOrder.SuccessCallback() {

                        @Override
                        public void onSuccess() {

                            Toast.makeText(AtyDetails.this, "修改成功！", Toast.LENGTH_LONG).show();
                            fresh();

                        }
                    }, new UpdateOrder.FailCallback() {

                        @Override
                        public void onFail() {
                            Toast.makeText(AtyDetails.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.create().show();
    }

    public void fresh() {
        new DownloadOneOrder(orderNumber, new DownloadOneOrder.SuccessCallback() {

            @Override
            public void onSuccess(ArrayList<Order> orders) {

                //        订单号   order_number
                //        下单时间 order_time
                //        信任好友 trust_friend
                //        快递体积 size(L M S)
                //        收货地点 arrive_address
                //        收货时间 arrive_time
                //        快递点   pick_point
                //        取货号   pick_number
                //        派送员   taker
                //        备注     note
                //        状态     order_status(int)

                for (Order o : orders) {
                    orderNumber = o.getOrderNumber();
                    orderTime = o.getOrderTime();
                    trustFriend = o.getTrust_friend();
                    size = o.getSize();
                    switch (o.getSize()) {
                        case "S":
                            tv_size.setText("小");
                            break;
                        case "M":
                            tv_size.setText("中");
                            break;
                        case "L":
                            tv_size.setText("大");
                            break;
                    }
                    arriveAddress = o.getArriveAddress();
                    arriveTime = o.getArriveTime();
                    pickPoint = o.getPickPoint();
                    pickNumber = o.getPickNumber();
                    if (o.getTaker().equals("0")) {
                        taker = "18752069878";
                    }
                    note = o.getNote();
                    if (o.getOrderStatus().equals("0")) {
                        orderStatus = "已结单";
                    } else {
                        orderStatus = "派送中";
                    }
                    if (note.equals("none")) {
                        note = "无";
                    }

                    tv_orderNumber.setText(orderNumber);
                    tv_orderTime.setText(orderTime);
                    tv_trustFriend.setText(trustFriend);

                    tv_arriveAddress.setText(arriveAddress);
                    tv_arriveTime.setText(arriveTime);
                    tv_pickPoint.setText(pickPoint);
                    tv_pickNumber.setText(pickNumber);
                    tv_taker.setText(taker);
                    tv_note.setText(note);
                    tv_orderStatus.setText(orderStatus);

                    if (pickPoint.equals("none") || pickPoint.equals("null") || pickPoint == null) {
                        //老用户
                        tv_orderPattern.setText("UD下单");
                        ll_orderPattern_temp.setVisibility(View.GONE);
                    } else {
                        //临时下单
                        tv_orderPattern.setText("临时下单");
                        ll_orderPattern_temp.setVisibility(View.VISIBLE);
                    }
                    if (trustFriend.equals("none") || trustFriend.equals("null") || trustFriend == null) {
                        //自己拿
                        ll_pickPattern_self.setVisibility(View.VISIBLE);
                        ll_pickPattern_friend.setVisibility(View.GONE);
                    } else {
                        //信任好友代拿
                        ll_pickPattern_self.setVisibility(View.GONE);
                        ll_pickPattern_friend.setVisibility(View.VISIBLE);
                    }

                    if(trustOrderDetail){
                        ll_orderPattern_temp.setVisibility(View.GONE);
                        tv_change.setVisibility(View.GONE);
                        ll_pickPattern_friend.setVisibility(View.GONE);
                    }

                }
            }
        }, new DownloadOneOrder.FailCallback() {

            @Override
            public void onFail() {
                Toast.makeText(AtyDetails.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void dialogChoice(final String taker) {

        //单选对话窗口
        AlertDialog.Builder builder = new AlertDialog.Builder(AtyDetails.this, 3);

        //定义标题样式
        TextView title = new TextView(AtyDetails.this);
        title.setText("快递员： " + taker );
        title.setPadding(10, 100, 10, 100);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(getResources().getColor(com.hyphenate.easeui.R.color.black_deep));
        title.setTextSize(18);

        //使用自定义title
        builder.setCustomTitle(title);
        builder.setCancelable(true);
        try {

            builder.setPositiveButton("拨打电话", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    callPhone("18752069878");
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.create().show();
    }

}
