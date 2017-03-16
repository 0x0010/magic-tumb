package com.iamdigger.magictumblr.wcintf.controller;

import com.iamdigger.magictumblr.wcintf.bean.MagicAssetDO;
import com.iamdigger.magictumblr.wcintf.bean.TextMsg;
import com.iamdigger.magictumblr.wcintf.constant.AssetState;
import com.iamdigger.magictumblr.wcintf.constant.I18nResource;
import com.iamdigger.magictumblr.wcintf.constant.MsgType;
import com.iamdigger.magictumblr.wcintf.constant.OperationType;
import com.iamdigger.magictumblr.wcintf.exception.MagicException;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetFileService;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetService;
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
public class WeChatAccessor {

  private static Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("GMT+8")));
  private static Logger logger = LoggerFactory.getLogger(WeChatAccessor.class);
  @Resource(type = I18nResource.class)
  private I18nResource i18nResource;

  @Resource
  private MagicAssetService magicAssetService;

  @Resource
  private MagicAssetFileService magicAssetFileService;

  @RequestMapping(value = "/wechat/intf", method = RequestMethod.POST)
  @ResponseBody
  public String wechatPortal(@RequestBody String requestBody) {
    String replyMsg, fromUser = null, toUser = null;
    try {
      TextMsg inTextMsg = SerializeUtil.textFromXml(requestBody);
      fromUser = inTextMsg.getFromUserName();
      toUser = inTextMsg.getToUserName();
      logger.info("Received Message:{}", inTextMsg.toString());
      MsgType msgType = MsgType.fromType(inTextMsg.getMsgType());

      TextMsg textReplyMsg = buildNoContentText(toUser, fromUser);
      switch (msgType) {
        case TEXT:
          textReplyMsg.setContent(dispatchTextMsg(inTextMsg.getContent()));
          break;
        default:
          throw new RuntimeException("Unsupported message type");
      }
      replyMsg = SerializeUtil.textToXml(textReplyMsg);
    } catch (Exception e) {
      logger.error("System error", e);
      replyMsg = sysErrorTextReply(toUser, fromUser);
    }
    return replyMsg;
  }

  private TextMsg buildNoContentText(String fromUser, String toUser) {
    TextMsg textReplyMsg = new TextMsg();
    textReplyMsg.setToUserName(toUser);
    textReplyMsg.setFromUserName(fromUser);
    textReplyMsg.setCreateTime(calendar.getTimeInMillis());
    textReplyMsg.setMsgType(MsgType.TEXT.getType());
    return textReplyMsg;
  }

  private String sysErrorTextReply(String fromUser, String toUser) {
    TextMsg textReplyMsg = buildNoContentText(fromUser, toUser);
    textReplyMsg.setContent(i18nResource.getMessage("sysError"));
    return SerializeUtil.textToXml(textReplyMsg);
  }

  private String dispatchTextMsg(String inText) {
    // 预处理文本：删除消息前后空白字符
    inText = inText.trim();
    char ch = inText.charAt(0);
    try {
      OperationType ot = OperationType.of(ch);
      String dispatchResult = "";
      switch (ot) {
        case G:
          if (inText.length() <= 2) {
            throw new MagicException(String.format(i18nResource.getException("e0001"), inText));
          }
          String assetCode = magicAssetFileService.saveToDisk(inText.substring(2, inText.length()));
          dispatchResult = String.format(i18nResource.getMessage("urlReceived"), assetCode);
          break;
        case Q:
          if (inText.length() <= 2) {
            throw new MagicException(String.format(i18nResource.getException("e0001"), inText));
          }
          MagicAssetDO mad = magicAssetService.queryMagicAsset(inText.substring(2, inText.length()));
          if(null != mad) {
            AssetState assetState = AssetState.valueOf(mad.getState());
            switch (assetState) {
              case SUCCESS:
                dispatchResult = String.format(i18nResource.getMessage("videoCodeFound"), mad.getVideoCode());
                break;
              case FAILED:
                dispatchResult = i18nResource.getMessage("urlParseFailed");
                break;
              case PROCESSING:
                dispatchResult = i18nResource.getMessage("underProcess");
                break;
              case UNSUPPORTED_URL:
                dispatchResult = String.format(i18nResource.getMessage("unsupportedUrl"), mad.getOriginalUrl());
                break;
              case INIT:
                dispatchResult = i18nResource.getMessage("waitForParse");
                break;
              default:
                dispatchResult = i18nResource.getMessage("dizzy");
            }
          } else {
            dispatchResult = i18nResource.getMessage("noAssetFound");
          }
          break;
        case H:
          break;
        default:
          break;
      }
      return dispatchResult;
    } catch (RuntimeException re) {
      if (re instanceof MagicException) {
        return ((MagicException) re).getErrorMsg();
      }
      logger.error("", re);
    }
    return "魔法君已收到消息。";
  }
}
