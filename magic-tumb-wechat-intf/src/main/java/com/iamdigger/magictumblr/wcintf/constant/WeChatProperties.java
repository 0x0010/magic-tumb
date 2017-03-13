package com.iamdigger.magictumblr.wcintf.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Sam
 * @since 3.0.0
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {
  private String appId;
  private String appSecret;
}
