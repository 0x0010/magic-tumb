package com.iamdigger.magictumblr.wcintf.constant;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Sam
 * @since 3.0.0
 */

@ConfigurationProperties(prefix = "i18n")
@Getter
public class I18nResource {

  private Map<String, String> message = new HashMap<>();
  private Map<String, String> exception = new HashMap<>();

  public String getMessage(String msgKey) {
    return message.get(msgKey);
  }

  public String getException(String errorCode) {
    return exception.get(errorCode);
  }
}
