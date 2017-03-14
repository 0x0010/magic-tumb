package com.iamdigger.magictumblr.wcintf.service.impl;

import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetFileService;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * @author Sam
 * @since 3.0.0
 */
@Component
public class MagicAssetFileServiceImpl implements MagicAssetFileService {

  private DateFormatter dateFormatter = new DateFormatter("yyMMddHHmmssSSS");
  private Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
  private Random random = new Random();

  @Override
  public String saveToDisk(String url) {
    return dateFormatter.print(calendar.getTime(), Locale.CHINA) + (random.nextInt(10000) + 10000);
  }
}
