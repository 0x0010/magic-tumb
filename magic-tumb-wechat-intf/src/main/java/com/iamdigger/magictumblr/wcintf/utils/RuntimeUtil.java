package com.iamdigger.magictumblr.wcintf.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Sam
 * @since 3.0.0
 */
public class RuntimeUtil {

  public static Date getCurrentDateTime() {
    return Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).getTime();
  }

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
