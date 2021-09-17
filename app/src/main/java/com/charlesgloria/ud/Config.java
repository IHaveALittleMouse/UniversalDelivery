package com.charlesgloria.ud;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import java.util.Map;

public class Config {

  public static final String SERVER_URL = "http://101.132.190.102:8080/TestServer/api.jsp";
  public static final String SERVER_URL_DOWNLOADPORTRAIT = "http://101.132.190" +
      ".102:8080/TestServer/DownloadPics.jsp";
  public static final String SERVER_URL_UPLOADPORTRAIT = "http://101.132.190" +
      ".102:8080/TestServer/UploadPics.jsp";
  public static final String SERVER_URL_UPLOADPORTRAITNAME = "http://101.132.190" +
      ".102:8080/TestServer/UploadPicsName.jsp";
  public static final String SERVER_URL_PORTRAITPATH = "http://101.132.190" +
      ".102:8080/TestServer/img/";

  public static final String PORTRAITPATH = "portrait_path";

  public static final String KEY_DEVICEID = "deviceid";
  public static final String KEY_TOKEN = "token";
  public static final String KEY_ACTION = "action";
  public static final String KEY_PHONE_NUM = "item_phone";
  public static final String KEY_PHONE_MD5 = "phone_md5";
  public static final String KEY_STATUS = "status";
  public static final String KEY_CODE = "key_code";
  public static final String KEY_CONTACTS = "contacts";
  public static final String KEY_PAGE = "page";
  public static final String KEY_PERPAGE = "perpage";
  public static final String KEY_TIMELINE = "timeline";
  public static final String KEY_ORDERS = "orders";
  public static final String KEY_MSG_ID = "msgId";
  public static final String KEY_MSG = "msg";
  public static final String KEY_COMMENTS = "items";
  public static final String KEY_CONTENT = "content";
  public static final String KEY_ADDRESS = "address";
  public static final String KEY_USERNAME = "username";
  public static final String KEY_PASSWORD = "password";
  public static final String KEY_EMAIL = "item_email";

  public static final String KEY_ADDRESS_SCHOOL = "address_school";
  public static final String KEY_ADDRESS_AREA = "address_area";
  public static final String KEY_ADDRESS_BUILDING = "address_building";
  public static final String KEY_ADDRESS_ROOM = "address_room";

  public static final String KEY_ORDER_NUMBER = "order_number";
  public static final String KEY_TAKER = "taker";
  public static final String KEY_ARRIVE_ADDRESS = "arrive_address";
  public static final String KEY_NOTE = "note";
  public static final String KEY_ORDER_TIME = "order_time";
  public static final String KEY_TRUST_FRIEND = "trust_friend";
  public static final String KEY_SIZE = "size";
  public static final String KEY_AMOUNT = "amount";
  public static final String KEY_ARRIVE_TIME = "arrive_time";
  public static final String KEY_PICK_POINT = "pick_point";
  public static final String KEY_PICK_NUMBER = "pick_number";
  public static final String KEY_ORDER_STATUS = "order_status";

  public static final String KEY_PORTRAIT = "portrait";

  public static final String KEY_HX_PORTRAIT = "hx_portrait";
  public static final String KEY_HX_NICKNAME = "hx_nickname";
  public static final String KEY_HX_USERNAME = "hx_username";
  public static final String KEY_HX_PASSWORD = "hx_password";
  public static final String KEY_HX_MYNAME = "hx_myname";
  public static final String KEY_HX_FRIENDNAME = "hx_friendname";
  public static final String KEY_HX_FRIENDSNAME = "hx_friendsname";
  public static final String KEY_HX_UNRADMSGCOUNT = "hx_unreadmsgcount";

  public static final int REQUEST_READ_PHONE_STATE = 1;
  public static final int RESULT_STATUS_SUCCESS = 1;
  public static final int RESULT_STATUS_FAIL = 0;
  public static final int RESULT_STATUS_INVALID_TOKEN = 2;
  //上拉加载更多
  public static final int LOAD_DATA = 2;
  //下拉刷新
  public static final int REFRESH_DATA = 1;

  public static final int HX_UNRADMSGCOUNT = 4;


  public static final String APP_ID = "com.charles.secret";
  public static final String CHARSET = "utf-8";

  public static final String ACTION_GET_CODE = "send_pass";
  public static final String ACTION_LOGIN = "login";
  public static final String ACTION_HXLOGIN = "hx_login";

  public static final String ACTION_UPLOAD_ADDRESS = "upload_address";
  public static final String ACTION_UPLOAD_PORTRAIT = "upload_portraitname";
  public static final String ACTION_UPLOAD_CONTACTS = "upload_contacts";
  public static final String ACTION_UPLOAD_ORDER = "upload_order";
  public static final String ACTION_UPLOAD_TOKEN = "upload_token";
  public static final String ACTION_UPLOAD_HXFRIEND = "upload_hxfriend";
  public static final String ACTION_UPLOAD_DEVICEID = "upload_deviceid";
  public static final String ACTION_UPLOAD_HX_CONTACT = "upload_hx_contact";

  public static final String ACTION_UPDATE_ORDER = "update_order";
  public static final String ACTION_UPDATE_HX_CONTACT = "update_hx_contact";

  public static final String ACTION_DOWNLOAD_PORTRAIT = "download_portrait";
  public static final String ACTION_DOWNLOAD_ADDRESS = "download_address";
  public static final String ACTION_DOWNLOAD_ORDERS = "download_orders";
  public static final String ACTION_DOWNLOAD_WAITING_ORDERS = "download_waiting_orders";
  public static final String ACTION_DOWNLOAD_TRUST_ORDERS = "download_trust_orders";
  public static final String ACTION_DOWNLOAD_TAKEN_ORDERS = "download_taken_orders";
  public static final String ACTION_DOWNLOAD_ONE_ORDER = "download_one_order";
  public static final String ACTION_DOWNLOAD_HXFRIENDS = "download_hxfriends";
  public static final String ACTION_DOWNLOAD_HX_CONTACT = "download_hx_contact";
  public static final String ACTION_DELETE_ORDER = "delete_order";
  public static final String ACTION_DELETE_HXFRIEND = "delete_hx_friend";
  public static final String ACTION_TIMELINE = "timeline";
  public static final String ACTION_PUBLISH = "publish";
  public static final String ACTION_GET_COMMENT = "get_comment";
  public static final String ACTION_REGIST = "regist";
  public static final String ACTION_COMPLETE_ORDER = "complete_order";

  public static final int DELAYMILLIS = 500;

  public static final String KEY_SAVED_ADDRESS = "saved_address";

  public static final int ACTIVITY_RESULT_NEED_REFRESH = 10000;

  public static final int UnreadMsgCount = 0;

  //if user has already login , then loginStatue = 1
  public static int loginStatus = 0;

  public static Map<String, String> contactPortraitList;

  public static String getCachedToken(Context context) {
    return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
        .getString(KEY_TOKEN, null);
  }

  public static void cacheToken(Context context, String token) {
    Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
        .edit();
    e.putString(KEY_TOKEN, token);
    e.commit();
  }

  public static void cacheAddress(Context context, String address) {
    Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
        .edit();
    e.putString(KEY_SAVED_ADDRESS, address);
    e.commit();
  }

  public static String getCachedPhoneNum(Context context) {
    return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
        .getString(KEY_PHONE_NUM, null);
  }

  public static void cachePhoneNum(Context context, String phoneNum) {
    Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
        .edit();
    e.putString(KEY_PHONE_NUM, phoneNum);
    e.commit();
  }

  public static void cacheDeviceID(Context context, String deviceID) {
    Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
        .edit();
    e.putString(KEY_DEVICEID, deviceID);
    e.commit();
  }

  public static String getCachedDeviceID(Context context) {
    return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
        .getString(KEY_DEVICEID, null);
  }

  public static void cachePortraitPath(Context context, String path) {
    Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
        .edit();
    e.putString(PORTRAITPATH, path);
    e.apply();
  }

  public static String getCachedPortraitPath(Context context) {
    return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
        .getString(PORTRAITPATH, null);
  }

  public static String getCachedPreference(Context context, String preference) {
    return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
        .getString(preference, null);
  }

  public static void cachePreference(Context context, String preference, String value) {
    Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
        .edit();
    e.putString(preference, value);
    e.apply();
  }

  public static void setContactPortraitList() {
//    contactPortraitList = com.hyphenate.easeui.Config.getContactPortraitList();
  }

  public static String getContactPortrait(String key) {
    if (contactPortraitList != null) {
      return contactPortraitList.get(key);
    }
    return null;
  }
}
