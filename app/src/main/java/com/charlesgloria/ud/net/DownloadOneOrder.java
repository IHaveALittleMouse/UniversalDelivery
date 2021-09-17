package com.charlesgloria.ud.net;

import com.charlesgloria.ud.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DownloadOneOrder {
  public DownloadOneOrder(String orderNumber, final SuccessCallback successCallback,
                          final FailCallback failCallback) {
    new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {

      @Override
      public void onSuccess(String result) {
        try {
          JSONObject obj = new JSONObject(result);

          switch (obj.getInt(Config.KEY_STATUS)) {
            case Config.RESULT_STATUS_SUCCESS:
              if (successCallback != null) {
                ArrayList<Order> orders = new ArrayList<Order>();
                JSONArray orderJsonArray = obj
                    .getJSONArray(Config.KEY_ORDERS);
                JSONObject orderObj;
                for (int i = 0; i < orderJsonArray.length(); i++) {
                  orderObj = orderJsonArray.getJSONObject(i);
                  orders.add(new Order(
                          orderObj.getString(Config.KEY_PHONE_NUM),
                          orderObj.getString(Config.KEY_ORDER_NUMBER),
                          orderObj.getString(Config.KEY_ORDER_TIME),
                          orderObj.getString(Config.KEY_TRUST_FRIEND),
                          orderObj.getString(Config.KEY_SIZE),
                          orderObj.getString(Config.KEY_ARRIVE_ADDRESS),
                          orderObj.getString(Config.KEY_ARRIVE_TIME),
                          orderObj.getString(Config.KEY_PICK_POINT),
                          orderObj.getString(Config.KEY_PICK_NUMBER),
                          orderObj.getString(Config.KEY_TAKER),
                          orderObj.getString(Config.KEY_NOTE),
                          orderObj.getString(Config.KEY_ORDER_STATUS)
                      )
                  );
                }
                successCallback.onSuccess(orders);
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
    }, Config.KEY_ACTION, Config.ACTION_DOWNLOAD_ONE_ORDER, Config.KEY_ORDER_NUMBER, orderNumber);
  }

  public static interface SuccessCallback {
    void onSuccess(ArrayList<Order> orders);
  }

  public static interface FailCallback {
    void onFail();
  }
}
