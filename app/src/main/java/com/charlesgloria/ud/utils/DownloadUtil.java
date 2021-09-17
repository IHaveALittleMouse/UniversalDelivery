package com.charlesgloria.ud.utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2018/2/5 0005.
 */

public class DownloadUtil {
  /**
   * 从服务器下载文件
   *
   * @param URL 下载文件的地址
   * @param FileName 文件名字
   */
  public static final int DOWNLOAD_SUCC = 1;
  public static final int DOWNLOAD_FAIL = 2;

  public void downLoad(final String URL, final String FileName) {

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          URL url = new URL(URL);
          HttpURLConnection con = (HttpURLConnection) url.openConnection();
          con.setReadTimeout(5000);
          con.setConnectTimeout(5000);
          con.setRequestProperty("Charset", "UTF-8");
          con.setRequestMethod("GET");
          File outputFile = null;
          if (con.getResponseCode() == 200) {
            // http协议规定200为通信成功标志，通信成功
            Log.i("download conn", "successful");
            InputStream is = con.getInputStream();//获取输入流
            FileOutputStream fileOutputStream = null;//文件输出流
            if (is != null) {
              FileUtils fileUtils = new FileUtils();
              outputFile = fileUtils.createFile(FileName);
              fileOutputStream = new FileOutputStream(outputFile);// 指定文件保存路径，代码看下一步
              byte[] buf = new byte[1024];
              int ch;
              while ((ch = is.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
              }
            }
            if (fileOutputStream != null) {
              // 有输出流，写文件成功，下载成功，关闭输出流，向FragMe返回成功标志
              fileOutputStream.flush();
              fileOutputStream.close();
              Log.i("downloadFile", "successful");
              sendMessage(DOWNLOAD_SUCC, outputFile.getAbsolutePath());
            }
          }
        } catch (Exception e) {
          Log.i("downloadFile", "failed");
          sendMessage(DOWNLOAD_FAIL, "");
          e.printStackTrace();
        }
      }
    }).start();
  }

  private OnDownloadProcessListener onDownloadProcessListener;

  /**
   * 发送上传结果
   *
   * @param responseCode
   * @param responseMessage
   */
  private void sendMessage(int responseCode, String responseMessage) {
    onDownloadProcessListener.onDownloadDone(responseCode, responseMessage);
  }

  /**
   * 下面是一个自定义的回调函数，用到回调上传文件是否完成
   */
  public interface OnDownloadProcessListener {
    /**
     * 上传响应
     *
     * @param responseCode
     * @param message
     */
    void onDownloadDone(int responseCode, String message);
  }

  public void setOnDownloadProcessListener(OnDownloadProcessListener onDownloadProcessListener) {
    this.onDownloadProcessListener = onDownloadProcessListener;
  }
}
