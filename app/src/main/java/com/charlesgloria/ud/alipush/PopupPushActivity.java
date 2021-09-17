package com.charlesgloria.ud.alipush;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.PopupWindow;


import java.util.Map;

public class PopupPushActivity extends Activity {
    static final String TAG = "PopupPushActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * 实现通知打开回调方法，获取通知相关信息
     * @param title     标题
     * @param summary   内容
     * @param extMap    额外参数
     */
//    @Override
//    protected void onSysNoticeOpened(String title, String summary, Map<String, String> extMap) {
//        Log.d("OnMiPushSysNoticeOpened, title: " + title + ", content: " + summary + ", extMap: " + extMap);
//    }
}