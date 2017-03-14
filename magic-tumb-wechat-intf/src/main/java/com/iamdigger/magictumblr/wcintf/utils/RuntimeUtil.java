package com.iamdigger.magictumblr.wcintf.utils;

/**
 * @author Sam
 * @since 3.0.0
 */
public class RuntimeUtil {

  public static String getUserHome() {
    return System.getProperty("user.home");
  }

  /**
   * 获取程序运行目录
   *
   * @return 程序运行目录
   */
  public static String getRunningPath() {
    return System.getProperty("user.dir");
  }
}
