package com.charlesgloria.ud.net;

public class Message {

  private final String msg;
  private final String msgId;
  private final String phone_md5;

  public Message(String msgId, String msg, String phone_md5) {
    this.msgId = msgId;
    this.msg = msg;
    this.phone_md5 = phone_md5;
  }

  public String getMsg() {
    return msg;
  }

  public String getMsgId() {
    return msgId;
  }

  public String getPhone_md5() {
    return phone_md5;
  }
}
