package com.iamdigger.magictumblr.wcintf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sam
 * @since 3.0.0
 */
@RestController
public class WeChatAccessController {

  private static Logger logger = LoggerFactory.getLogger(WeChatAccessController.class);

  @RequestMapping(value = "/wechat/intf", method = RequestMethod.GET)
  public String portalInFirstTime(@RequestParam String signature, @RequestParam String timestamp,
      @RequestParam String nonce, @RequestParam String echostr) {
    logger.info("signature:{}, timestamp:{}, nonce:{}, echostr:{}", signature, timestamp, nonce, echostr);
    return echostr;
  }
}
