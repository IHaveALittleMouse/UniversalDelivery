package com.charlesgloria.ud.net;

import com.charlesgloria.ud.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class DownloadAddress {
  public DownloadAddress(String phone, final SuccessCallback successCallback,
                         final FailCallback failCallback) {
    new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {

      @Override
      public void onSuccess(String result) {
        try {
          JSONObject obj = new JSONObject(result);

          switch (obj.getInt(Config.KEY_STATUS)) {
            case Config.RESULT_STATUS_SUCCESS:
              if (successCallback != null) {
                successCallback.onSuccess(obj.getString(Config.KEY_ADDRESS_SCHOOL),
                    obj.getString(Config.KEY_ADDRESS_AREA),
                    obj.getString(Config.KEY_ADDRESS_BUILDING),
                    obj.getString(Config.KEY_ADDRESS_ROOM));
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
    }, Config.KEY_ACTION, Config.ACTION_DOWNLOAD_ADDRESS, Config.KEY_PHONE_NUM, phone);
  }

  public static interface SuccessCallback {
    void onSuccess(String school, String area, String building, String room);
  }

  public static interface FailCallback {
    void onFail();
  }
}
