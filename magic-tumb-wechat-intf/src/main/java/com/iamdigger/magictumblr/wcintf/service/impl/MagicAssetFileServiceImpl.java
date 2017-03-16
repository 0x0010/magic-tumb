package com.iamdigger.magictumblr.wcintf.service.impl;

import com.google.common.io.Files;
import com.iamdigger.magictumblr.wcintf.constant.I18nResource;
import com.iamdigger.magictumblr.wcintf.exception.MagicException;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetFileService;
import com.iamdigger.magictumblr.wcintf.utils.RuntimeUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Sam
 * @since 3.0.0
 */
@Component
@EnableConfigurationProperties(I18nResource.class)
public class MagicAssetFileServiceImpl implements MagicAssetFileService {

  @Resource
  private I18nResource i18nResource;

  @Override
  public String saveToDisk(String committer, String content) {
    String assetId = UUID.randomUUID().toString().replaceAll(Pattern.quote("-"), "");
    try {
      final String finalFileName = String
          .format("%s/%s.mt", RuntimeUtil.getRunningPath(), assetId);
      final String shadowFile = String
          .format("%s/%s.mt.mid", RuntimeUtil.getRunningPath(), assetId);

      BufferedWriter bw = Files.newWriter(new File(shadowFile), Charset.forName("UTF-8"));
      bw.write(assetId);
      bw.newLine();
      bw.write(committer);
      bw.newLine();
      bw.write(content);
      bw.flush();
      bw.close();
      Files.move(new File(shadowFile), new File(finalFileName));
    } catch (IOException e) {
      throw new MagicException(i18nResource.getException("e0002"));
    }
    return assetId;
  }
}
