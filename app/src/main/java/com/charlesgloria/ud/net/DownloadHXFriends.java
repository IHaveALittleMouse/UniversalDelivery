package com.charlesgloria.ud.net;

import com.charlesgloria.ud.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/2/11 0011.
 */

public class DownloadHXFriends {
  public DownloadHXFriends(String myUsername,
                           final DownloadHXFriends.SuccessCallback successCallback,
                           final DownloadHXFriends.FailCallback failCallback) {

    new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {

      @Override
      public void onSuccess(String result) {
        try {
          JSONObject obj = new JSONObject(result);
          switch (obj.getInt(Config.KEY_STATUS)) {
            case Config.RESULT_STATUS_SUCCESS:
              if (successCallback != null) {
                ArrayList<String> friendsName = new ArrayList<>();
                JSONArray nameJSONArray = obj.getJSONArray(Config.KEY_HX_FRIENDSNAME);
                JSONObject nameObj;
                for (int i = 0; i < nameJSONArray.length(); i++) {
                  nameObj = nameJSONArray.getJSONObject(i);
                  friendsName.add(nameObj.getString(Config.KEY_HX_FRIENDNAME));
                }
                successCallback.onSuccess(friendsName);
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
    }, Config.KEY_ACTION, Config.ACTION_DOWNLOAD_HXFRIENDS, Config.KEY_HX_MYNAME, myUsername);

  }

  public static interface SuccessCallback {
    void onSuccess(ArrayList<String> friendsName);
  }

  public static interface FailCallback {
    void onFail();
  }
}
