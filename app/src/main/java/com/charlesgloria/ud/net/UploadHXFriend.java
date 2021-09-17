package com.charlesgloria.ud.net;

import com.charlesgloria.ud.Config;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/2/11 0011.
 */

public class UploadHXFriend {
  public UploadHXFriend(String myUsername, String friendUsername,
                        final UploadHXFriend.SuccessCallback successCallback,
                        final UploadHXFriend.FailCallback failCallback) {

    new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {

      @Override
      public void onSuccess(String result) {
        try {
          JSONObject obj = new JSONObject(result);
          switch (obj.getInt(Config.KEY_STATUS)) {
            case Config.RESULT_STATUS_SUCCESS:
              if (successCallback != null) {
                successCallback.onSuccess();
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
    }, Config.KEY_ACTION, Config.ACTION_UPLOAD_HXFRIEND, Config.KEY_HX_MYNAME, myUsername,
        Config.KEY_HX_FRIENDNAME, friendUsername);

  }

  public static interface SuccessCallback {
    void onSuccess();
  }

  public static interface FailCallback {
    void onFail();
  }
}
