package com.charlesgloria.ud.net;

/**
 * Created by Administrator on 2017/11/12 0012.
 */

import com.charlesgloria.ud.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class Register {
  public Register(String username, String password, String phone, String email, String address,
                  final SuccessCallback successCallback, final FailCallback failCallback) {
    new NetConnection(Config.SERVER_URL, HttpMethod.POST, result -> {
      try {
        JSONObject obj = new JSONObject(result);

        switch (obj.getInt(Config.KEY_STATUS)) {
          case Config.RESULT_STATUS_SUCCESS:
            if (successCallback != null) {
              successCallback.onSuccess(obj.getString(Config.KEY_TOKEN));
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

    }, () -> {
      for (int i = 0; i < 14; i++) {
        System.out.println("here status!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      }
      if (failCallback != null) {
        failCallback.onFail();
      }
    }, Config.KEY_ACTION, Config.ACTION_REGIST, Config.KEY_USERNAME, username,
        Config.KEY_PASSWORD, password, Config.KEY_PHONE_NUM, phone, Config.KEY_EMAIL, email,
        Config.KEY_ADDRESS, address);
  }

  public interface SuccessCallback {
    void onSuccess(String token);
  }

  public interface FailCallback {
    void onFail();
  }
}
