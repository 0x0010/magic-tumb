package com.iamdigger.magictumblr.wcintf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sam
 * @since 3.0.0
 */
@RestController
public class WeChatAccessController {

  private static Logger logger = LoggerFactory.getLogger(WeChatAccessController.class);

  @RequestMapping(value = "/wechat/intf", method = RequestMethod.POST)
  public String portalInFirstTime(@RequestBody String requestBody) {
    logger.info("Request Body:{}", requestBody);
    return "";
  }
}
