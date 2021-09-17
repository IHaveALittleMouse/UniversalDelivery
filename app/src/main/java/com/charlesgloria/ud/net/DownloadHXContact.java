package com.charlesgloria.ud.net;

import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.bean.HXContact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/5 0005.
 */

public class DownloadHXContact {
  public DownloadHXContact(String myUsername,
                           final DownloadHXContact.SuccessCallback successCallback,
                           final DownloadHXContact.FailCallback failCallback) {

    new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {

      @Override
      public void onSuccess(String result) {
        try {
          JSONObject obj = new JSONObject(result);
          switch (obj.getInt(Config.KEY_STATUS)) {
            case Config.RESULT_STATUS_SUCCESS:
              if (successCallback != null) {
                String nickname = obj.getString(Config.KEY_HX_NICKNAME);
                String portrait = obj.getString(Config.KEY_PORTRAIT);
                HXContact hxContact = new HXContact(nickname, portrait);
                // 只传回portrait 和 nickname，username在调用时已知
                successCallback.onSuccess(hxContact);
              }
              break;

            case Config.RESULT_STATUS_INVALID_TOKEN:
              if (failCallback != null) {
                failCallback.onFail();
              }
              break;

            default:
              if (failCallback != null) {
                failCallback.onFail();
              }
              break;
          }
        } catch (JSONException e) {
          e.printStackTrace();
          if (failCallback != null) {
            failCallback.onFail();
          }
        }
      }
    }, new NetConnection.FailCallback() {

      @Override
      public void onFail() {
        if (failCallback != null) {
          failCallback.onFail();
        }
      }
    }, Config.KEY_ACTION, Config.ACTION_DOWNLOAD_HX_CONTACT, Config.KEY_HX_USERNAME, myUsername);

  }

  public static interface SuccessCallback {
    void onSuccess(HXContact hxContact);
  }

  public static interface FailCallback {
    void onFail();
  }
}
