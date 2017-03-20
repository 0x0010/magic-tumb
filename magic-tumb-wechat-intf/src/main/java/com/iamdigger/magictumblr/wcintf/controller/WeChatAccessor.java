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

        case EVENT:
          if("subscribe".equals(inTextMsg.getEvent())) {
            // 关注
            textReplyMsg.setContent(subscribeWelcome());
          } else {
            textReplyMsg.setContent("不支持的事件");
          }
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

  private String subscribeWelcome() {
    return
        "好吧，我已然如此低调，还是被你找到了。嗯～，追求真理的道路上，想必客官跟我一样，时而疲惫，身心乏力，感觉身体被掏空。 "
            + "出于对人类生命起源与进化的敬畏，魔法汤儿会不定期更新隐藏内容。\n"
            + "回复「魔法书」，即刻体验！";
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
      } else if (inText.equals("魔法书")) {
       String magicBook = "";
        magicBook += "❡初级魔法❡\n\n";
        magicBook += " ※ 月光林地中潜伏着被诅咒且扭曲的暗夜精灵撒特，她们驱赶着误食费伍德森林污水的熊怪来袭击这里的路人。这些熊怪有个可怕的「魔法溢出」技能实在是让人窒息。\n";
        magicBook += " ※ 玛法里奥·怒风，这位最伟大的德鲁伊，用其强大的力量击败了试图入侵魔法森林的燃烧军团。一副魔法卷轴，被遗忘在了燃烧军团溃败的战场中，其中的奥义，就连强大的德莱尼都无法解读。"
            + "最终玛法里奥将其永远留在了这个卡利姆多最安全的土地上，封印于魔法汤泉。因为魔法汤泉可以影射出这部卷轴的奥秘，那是一幅幅转瞬即逝的画面，让人过目难忘。玛法里奥只记得那部神秘卷轴的内容"
            + "是以「TUMBLR」开始的。\n";
        return magicBook;
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
