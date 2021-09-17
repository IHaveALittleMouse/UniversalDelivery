package com.charlesgloria.ud.net;

//        手机号   phone
//        订单号   order_number
//        下单时间 order_time
//        信任好友 trust_friend
//        快递体积 size(L M S)
//        收货地点 arrive_address
//        收货时间 arrive_time
//        快递点   pick_point
//        取货号   pick_number
//        派送员   taker
//        备注     note
//        状态     order_status(int)

import com.charlesgloria.ud.bean.BaseBean;

public class Order extends BaseBean {

  private String id;

  private String orderNumber;
  private String phone;
  private String pickPoint;
  private String arriveAddress;
  private String note;
  private String orderStatus;
  private String orderTime;
  private String pickNumber;
  private String taker;
  private String trust_friend;
  private String size;
  private String arriveTime;

  public Order(String phone,
               String orderNum,
               String orderTime,
               String trustFriend,
               String size,
               String arriveAddress,
               String arriveTime,
               String pickPoint,
               String pickNumber,
               String taker,
               String note,
               String orderStatus) {
    this.orderNumber = orderNum;
    this.phone = phone;
    this.pickPoint = pickPoint;
    this.pickNumber = pickNumber;
    this.arriveAddress = arriveAddress;
    this.note = note;
    this.orderStatus = orderStatus;
    this.orderTime = orderTime;
    this.taker = taker;
    this.trust_friend = trustFriend;
    this.size = size;
    this.arriveTime = arriveTime;
  }

  public Order(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getArriveAddress() {
    return arriveAddress;
  }

  public String getNote() {
    return note;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public String getPhone() {
    return phone;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public String getPickPoint() {
    return pickPoint;
  }

  public String getOrderTime() {
    return orderTime;
  }

  public String getPickNumber() {
    return pickNumber;
  }

  public String getTaker() {
    return taker;
  }

  public String getTrust_friend() {
    return trust_friend;
  }

  public String getSize() {
    return size;
  }

  public String getArriveTime() {
    return arriveTime;
  }
}
