package com.charlesgloria.ud.net;

import com.charlesgloria.ud.Config;

import org.json.JSONException;
import org.json.JSONObject;

//                手机号   phone
//                下单时间 order_time
//                信任好友 trust_friend
//                快递体积 size(L M S)
//                快递数量 amount
//                收货地点 arrive_address
//                收货时间 arrive_time
//                快递点   pick_point
//                快递号   pick_number
//                备注     note

public class UploadOrder {
  public UploadOrder(String phone,
                     String orderTime,
                     String trustFriend,
                     String size,
                     String amount,
                     String arriveAddress,
                     String arriveTime,
                     String pickPoint,
                     String pickNumber,
                     String note,
                     final SuccessCallback successCallback, final FailCallback failCallback) {
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
    }, Config.KEY_ACTION, Config.ACTION_UPLOAD_ORDER,
        Config.KEY_PHONE_NUM, phone, //item_phone
        Config.KEY_ORDER_TIME, orderTime,
        Config.KEY_TRUST_FRIEND, trustFriend,
        Config.KEY_SIZE, size,
        Config.KEY_AMOUNT, amount,
        Config.KEY_ARRIVE_ADDRESS, arriveAddress,
        Config.KEY_ARRIVE_TIME, arriveTime,
        Config.KEY_PICK_POINT, pickPoint,
        Config.KEY_PICK_NUMBER, pickNumber,
        Config.KEY_NOTE, note);
  }

  public static interface SuccessCallback {
    void onSuccess();
  }

  public static interface FailCallback {
    void onFail();
  }
}
