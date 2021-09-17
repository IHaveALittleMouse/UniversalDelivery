package com.charlesgloria.ud.bean;

/**
 * Created by Administrator on 2018/3/5 0005.
 */

public class HXContact {
  private String nickname;
  private String portrait;
  private String username;


  public HXContact(String nickname) {
    this.nickname = nickname;
  }

  public HXContact(String nickname, String portrait) {
    this.nickname = nickname;
    this.portrait = portrait;
  }

  public HXContact(String username, String nickname, String portrait) {
    this.username = username;
    this.portrait = portrait;
    this.nickname = nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public void setPortrait(String portrait) {
    this.portrait = portrait;
  }

  public String getNickname() {
    return nickname;
  }

  public String getPortrait() {
    return portrait;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
