package com.iamdigger.magictumblr.wcintf.controller;

import com.iamdigger.magictumblr.wcintf.bean.MagicAssetDO;
import com.iamdigger.magictumblr.wcintf.bean.TextMsg;
import com.iamdigger.magictumblr.wcintf.constant.AssetState;
import com.iamdigger.magictumblr.wcintf.constant.I18nResource;
import com.iamdigger.magictumblr.wcintf.constant.MsgType;
import com.iamdigger.magictumblr.wcintf.exception.MagicException;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetFileService;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetService;
import com.iamdigger.magictumblr.wcintf.utils.SerializeUtil;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
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
  @Resource
  MagicAssetService magicAssetService;
  @Resource(type = I18nResource.class)
  private I18nResource i18nResource;
  @Resource
  private MagicAssetFileService magicAssetFileService;

  @RequestMapping(value = "/wechat/intf", method = RequestMethod.POST)
  @ResponseBody
  public String wechatPortal(@RequestBody String requestBody) {
    String replyMsg, fromUser = null, toUser = null;
    try {
      logger.info("Received Message:{}", requestBody);
      TextMsg inTextMsg = SerializeUtil.textFromXml(requestBody);
      fromUser = inTextMsg.getFromUserName();
      toUser = inTextMsg.getToUserName();
      MsgType msgType = MsgType.fromType(inTextMsg.getMsgType());

      TextMsg textReplyMsg = buildNoContentText(toUser, fromUser);
      switch (msgType) {
        case TEXT:
          textReplyMsg
              .setContent(dispatchTextMsg(inTextMsg.getFromUserName(), inTextMsg.getContent()));
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

  private String dispatchTextMsg(String committer, String inText) {
    // 预处理文本：删除消息前后空白字符
    inText = inText.trim();
    try {
      // 查询
      if (inText.equalsIgnoreCase("q")) {
        List<MagicAssetDO> magicAssetDOS = magicAssetService.queryAssetByCommitter(committer, 0, 2);
        if (null == magicAssetDOS || magicAssetDOS.size() <= 0) {
          return i18nResource.getMessage("queryEmpty");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(i18nResource.getMessage("assetFoundTitle")).append("\n");
        for (MagicAssetDO magicAssetDO : magicAssetDOS) {
          AssetState state = AssetState.valueOf(magicAssetDO.getState());
          sb.append("\n");
          sb.append(String
              .format(i18nResource.getMessage("assetFoundTime"), magicAssetDO.getCreateTime()))
              .append("\n");
          sb.append(String
              .format(i18nResource.getMessage("assetFoundContent"), magicAssetDO.getAssetContent()))
              .append("\n");
          sb.append(String.format(i18nResource.getMessage("assetFoundResult"), state.randomDesc()));
          if (state == AssetState.SUCCESS) {
            sb.append("\n").append(String
                .format(i18nResource.getMessage("assetFoundSurprise"), magicAssetDO.getVideoCode()))
                .append("\n");
          }
          sb.append("\n");
        }
        return sb.toString();
      } else {
        // 非指令文本，直接写入文件，等待入库
        magicAssetFileService.saveToDisk(committer, inText);
        return i18nResource.getMessage("contentReceived1") + "\n" + i18nResource
            .getMessage("contentReceived2");
      }

/*
      OperationType ot = OperationType.of("");
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
      return dispatchResult;*/
    } catch (RuntimeException re) {
      logger.error("", re);
      if (re instanceof MagicException) {
        return ((MagicException) re).getErrorMsg();
      }
    }
    return i18nResource.getMessage("dizzy");
  }
}
