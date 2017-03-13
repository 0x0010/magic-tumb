package com.iamdigger.magictumblr.wcintf.controller;

import com.iamdigger.magictumblr.wcintf.bean.TextMsg;
import com.iamdigger.magictumblr.wcintf.constant.I18nResource;
import com.iamdigger.magictumblr.wcintf.constant.MsgType;
import com.iamdigger.magictumblr.wcintf.utils.SerializeUtil;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sam
 * @since 3.0.0
 */
@RestController
@EnableConfigurationProperties({I18nResource.class})
public class WeChatAccessController {

  private static Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("GMT+8")));

  @Resource(type = I18nResource.class)
  private I18nResource i18nResource;

  private static Logger logger = LoggerFactory.getLogger(WeChatAccessController.class);

  @RequestMapping(value = "/wechat/intf", method = RequestMethod.POST)
  @ResponseBody
  public String portalInFirstTime(@RequestBody String requestBody) {
    String replyMsg, fromUser = null, toUser = null;
    try {
      TextMsg inTextMsg = SerializeUtil.textFromXml(requestBody);
      fromUser = inTextMsg.getFromUserName();
      toUser = inTextMsg.getToUserName();
      MsgType msgType = MsgType.fromType(inTextMsg.getMsgType());
      switch (msgType) {
        case TEXT:
          TextMsg textReplyMsg = new TextMsg();
          textReplyMsg.setToUserName(fromUser);
          textReplyMsg.setFromUserName(toUser);
          textReplyMsg.setCreateTime(calendar.getTimeInMillis());
          textReplyMsg.setContent(String.format(i18nResource.getMessage("urlReceived"), 12311));
          textReplyMsg.setMsgType(MsgType.TEXT.getType());
          replyMsg = SerializeUtil.textToXml(textReplyMsg);
          break;
        default:
          throw new RuntimeException("Unsupported message type");
      }
    } catch (Exception e) {
      replyMsg = sysErrorTextReply(fromUser, toUser);
    }
    return replyMsg;
  }

  private String sysErrorTextReply(String fromUser, String toUser) {
    TextMsg textReplyMsg = new TextMsg();
    textReplyMsg.setToUserName(fromUser);
    textReplyMsg.setFromUserName(toUser);
    textReplyMsg.setCreateTime(calendar.getTimeInMillis());
    textReplyMsg.setContent(i18nResource.getMessage("sysError"));
    textReplyMsg.setMsgType(MsgType.TEXT.getType());
    return SerializeUtil.textToXml(textReplyMsg);
  }
}
