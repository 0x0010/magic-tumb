package com.iamdigger.magictumblr.wcintf.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Sam
 * @since 3.0.0
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "qiniu")
public class QiniuAccess {

  private Boolean enableBackup;
  private String dbUrl;
  private String bucket;
  private String accessKey;
  private String secretKey;
}
