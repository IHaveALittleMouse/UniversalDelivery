package com.charlesgloria.ud.net;

import com.charlesgloria.ud.Config;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/2/5 0005.
 */

public class DownloadPortrait {
  public DownloadPortrait(String phone, final DownloadPortrait.SuccessCallback successCallback,
                          final DownloadPortrait.FailCallback failCallback) {
    new NetConnection(Config.SERVER_URL_DOWNLOADPORTRAIT, HttpMethod.POST,
        new NetConnection.SuccessCallback() {

      @Override
      public void onSuccess(String result) {
        try {
          JSONObject obj = new JSONObject(result);

          switch (obj.getInt(Config.KEY_STATUS)) {
            case Config.RESULT_STATUS_SUCCESS:
              if (successCallback != null) {
                successCallback.onSuccess(obj.getString(Config.KEY_PORTRAIT));
              }
              break;
            case Config.RESULT_STATUS_INVALID_TOKEN:
              if (successCallback != null) {
                successCallback.onSuccess(obj.getString(Config.KEY_PORTRAIT));
              }
              break;
            default:
              if (failCallback != null) {
                failCallback.onFail();
              }
              break;
          }
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
    }, new NetConnection.FailCallback() {

      @Override
      public void onFail() {
        if (failCallback != null) {
          failCallback.onFail();
        }
      }
    }, Config.KEY_ACTION, Config.ACTION_DOWNLOAD_PORTRAIT, Config.KEY_PHONE_NUM, phone);
  }

  public static interface SuccessCallback {
    void onSuccess(String portrait);
  }

  public static interface FailCallback {
    void onFail();
  }
}
