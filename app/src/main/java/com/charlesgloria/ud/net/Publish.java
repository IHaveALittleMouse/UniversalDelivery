package com.charlesgloria.ud.net;

import com.charlesgloria.ud.Config;

import org.json.JSONObject;

public class Publish {

  public Publish(String phone_md5, String token, String msg,
                 final SuccessCallback successCallback,
                 final FailCallback failCallback) {
    new NetConnection(
        Config.SERVER_URL,
        HttpMethod.POST,
        result -> {
          try {
            JSONObject jsonObject = new JSONObject(result);

            switch (jsonObject.getInt(Config.KEY_STATUS)) {
              case Config.RESULT_STATUS_SUCCESS:
                if (successCallback != null) {
                  successCallback.onSuccess();
                }
                break;
              case Config.RESULT_STATUS_INVALID_TOKEN:
                if (failCallback != null) {
                  failCallback
                      .onFail(Config.RESULT_STATUS_INVALID_TOKEN);
                }
                break;
              default:
                if (failCallback != null) {
                  failCallback
                      .onFail(Config.RESULT_STATUS_FAIL);
                }
                break;
            }
          } catch (Exception e) {
            e.printStackTrace();

            if (failCallback != null) {
              failCallback.onFail(Config.RESULT_STATUS_FAIL);
            }
          }
        }, () -> {
      if (failCallback != null) {
        failCallback.onFail(Config.RESULT_STATUS_FAIL);
      }
    }, Config.KEY_ACTION, Config.ACTION_PUBLISH,
        Config.KEY_PHONE_MD5, phone_md5, Config.KEY_TOKEN, token,
        Config.KEY_MSG, msg);
  }

  public interface SuccessCallback {
    void onSuccess();
  }

  public interface FailCallback {
    void onFail(int errorCode);
  }
}
