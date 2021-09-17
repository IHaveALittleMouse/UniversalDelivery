package com.charlesgloria.ud.net;

/**
 * Created by Administrator on 2017/11/12 0012.
 */

import com.charlesgloria.ud.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadToken {
  public UploadToken(String token, final SuccessCallback successCallback,
                     final FailCallback failCallback) {
    new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {

      @Override
      public void onSuccess(String result) {
        try {
          JSONObject obj = new JSONObject(result);

          switch (obj.getInt(Config.KEY_STATUS)) {
            // 用户已登录
            case Config.RESULT_STATUS_SUCCESS:
              if (successCallback != null) {
                successCallback.onSuccess();
              }
              break;
            // 用户未登录
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
    }, Config.KEY_ACTION, Config.ACTION_UPLOAD_TOKEN, Config.KEY_TOKEN, token);
  }

  public static interface SuccessCallback {
    void onSuccess();
  }

  public static interface FailCallback {
    void onFail();
  }
}
