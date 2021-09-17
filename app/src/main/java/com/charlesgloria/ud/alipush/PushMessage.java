package com.charlesgloria.ud.alipush;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.push.model.v20160801.PushNoticeToAndroidRequest;
import com.aliyuncs.push.model.v20160801.PushNoticeToAndroidResponse;
import com.aliyuncs.push.model.v20160801.PushRequest;
import com.aliyuncs.push.model.v20160801.PushResponse;

/**
 * 推送的OpenAPI文档 https://help.aliyun.com/document_detail/mobilepush/api-reference/openapi.html
 */
public class PushMessage {
    private static String accessKeyId = "LTAI18uxImuh3dfV";
    private static String accessKeySecret = "BhFyAA8Sf346snnQWSoNvpwbaL3zqN";
    private static Long appKey = 24779890L;
    private static String deviceIds = "";
    private IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
    private DefaultAcsClient client = new DefaultAcsClient(profile);
    private PushNoticeToAndroidRequest pushRequest = new PushNoticeToAndroidRequest();
    private PushRequest pR = new PushRequest();

    public void pushPR(String deviceId,String title,String body) throws  ClientException{
        pR.setAppKey(appKey);
        pR.setPushType("ANDROID");
        pR.setProtocol(ProtocolType.HTTPS);
        pR.setMethod(MethodType.POST);
        pR.setAndroidNotifyType("BOTH");
        pR.setTarget("DEVICE");
        pR.setTargetValue(deviceId);
        pR.setTitle(title);
        pR.setBody(body);

        try {
            PushResponse pushResponse = client.getAcsResponse(pR);
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }

    public void Push(String deviceId,String title,String body) throws ClientException {
        // 推送目标
//        pushRequest.setTarget("DEVICE"); //推送目标: DEVICE:按设备推送 ALIAS : 按别名推送 ACCOUNT:按帐号推送  TAG:按标签推送; ALL: 广播推送
//        pushRequest.setTargetValue(deviceIds); //根据Target来设定，如Target=DEVICE, 则对应的值为 设备id1,设备id2. 多个值使用逗号分隔.(帐号与设备有一次最多100个的限制)
        pushRequest.setAppKey(appKey);

        // 推送配置
        //推送内容需要保护，使用HTTPS协议
        pushRequest.setProtocol(ProtocolType.HTTPS);
        //推送内容较长，使用POST请求
        pushRequest.setMethod(MethodType.POST);
        pushRequest.setAppKey(appKey);
        pushRequest.setTarget("DEVICE");
        pushRequest.setTargetValue(deviceId);
        pushRequest.setTitle(title);
        pushRequest.setBody(body);
        pushRequest.setExtParameters("{\"key1\":\"value1\",\"api_name\":\"PushNoticeToAndroidRequest\"}");

        // 推送配置: Android
//        pushRequest.setAndroidNotifyType("BOTH");//通知的提醒方式 "VIBRATE" : 震动 "SOUND" : 声音 "BOTH" : 声音和震动 NONE : 静音
//        pushRequest.setAndroidNotificationBarType(1);//通知栏自定义样式0-100
//        pushRequest.setAndroidNotificationBarPriority(1);//通知栏自定义样式0-100
//        pushRequest.setAndroidOpenType("URL"); //点击通知后动作 "APPLICATION" : 打开应用 "ACTIVITY" : 打开AndroidActivity "URL" : 打开URL "NONE" : 无跳转
//        pushRequest.setAndroidOpenUrl("http://www.aliyun.com"); //Android收到推送后打开对应的url,仅当AndroidOpenType="URL"有效
//        pushRequest.setAndroidActivity("com.charlesgloria.ud.atys.AtyMainFrame"); // 设定通知打开的activity，仅当AndroidOpenType="Activity"有效
//        pushRequest.setAndroidMusic("default"); // Android通知音乐
//        pushRequest.setAndroidPopupActivity("com.charlesgloria.ud.atys.AtyMainFram");//设置该参数后启动辅助弹窗功能, 此处指定通知点击后跳转的Activity（辅助弹窗的前提条件：1. 集成第三方辅助通道；2. StoreOffline参数设为true）
//        pushRequest.setAndroidPopupTitle("Popup Title");
//        pushRequest.setAndroidPopupBody("Popup Body");
//        pushRequest.setAndroidExtParameters("{\"k1\":\"android\",\"k2\":\"v2\"}"); //设定通知的扩展属性。(注意 : 该参数要以 json map 的格式传入,否则会解析出错)
//        // 推送控制
//        Date pushDate = new Date(System.currentTimeMillis()); // 30秒之间的时间点, 也可以设置成你指定固定时间
//        String pushTime = ParameterHelper.getISO8601Time(pushDate);
//        pushRequest.setPushTime(pushTime); // 延后推送。可选，如果不设置表示立即推送
//        String expireTime = ParameterHelper.getISO8601Time(new Date(System.currentTimeMillis() + 12 * 3600 * 1000)); // 12小时后消息失效, 不会再发送
//        pushRequest.setExpireTime(expireTime);
//        pushRequest.setStoreOffline(true); // 离线消息是否保存,若保存, 在推送时候，用户即使不在线，下一次上线则会收到


        try {
            PushNoticeToAndroidResponse pushNoticeToAndroidResponse = client.getAcsResponse(pushRequest);
        } catch (ServerException e) {
            e.printStackTrace();
        }

    }

    public void PushToSelf(String deviceId,String title,String body) throws ClientException{
        pushRequest.setAppKey(appKey);
        // 推送配置
        //推送内容需要保护，使用HTTPS协议
        pushRequest.setProtocol(ProtocolType.HTTPS);
        //推送内容较长，使用POST请求
        pushRequest.setMethod(MethodType.POST);
        pushRequest.setAppKey(appKey);
        pushRequest.setTarget("DEVICE");
        pushRequest.setTargetValue(deviceId);
        pushRequest.setTitle(title);
        pushRequest.setBody(body);

        try {
            PushNoticeToAndroidResponse pushNoticeToAndroidResponse = client.getAcsResponse(pushRequest);
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }
}

