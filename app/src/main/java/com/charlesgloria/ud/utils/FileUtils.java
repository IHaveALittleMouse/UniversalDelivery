package com.charlesgloria.ud.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/2/5 0005.
 */

public class FileUtils {
  private String path = Environment.getExternalStorageDirectory().toString() + "/image/Portrait";

  public FileUtils() {
    File file = new File(path);
    Log.i("filePath", path);
    /**
     *如果文件夹不存在就创建
     */
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  public String getPath() {
    return path;
  }

  /**
   * 创建一个文件
   *
   * @param FileName 文件名
   * @return
   */
  public File createFile(String FileName) {
    File file = new File(path, FileName);
    if (!file.exists()) {
      try {
        file.createNewFile();
        return file;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return file;
//        return new File(path, FileName);
  }
}
