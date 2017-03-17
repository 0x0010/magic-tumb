package com.iamdigger.magictumblr.wcintf.utils;

import com.iamdigger.magictumblr.wcintf.bean.TextMsg;
import com.thoughtworks.xstream.XStream;

/**
 * @author Sam
 * @since 3.0.0
 */
public class SerializeUtil {

  private final static Object IN_TEXT_LOCK = new Object();
  private static XStream textXStream = null;

  private static void initTextStream() {
    if (null == textXStream) {
      synchronized (IN_TEXT_LOCK) {
        if (null == textXStream) {
          textXStream = new XStream();
          textXStream.alias("xml", TextMsg.class);
          textXStream.aliasField("ToUserName", TextMsg.class, "toUserName");
          textXStream.aliasField("FromUserName", TextMsg.class, "fromUserName");
          textXStream.aliasField("CreateTime", TextMsg.class, "createTime");
          textXStream.aliasField("MsgType", TextMsg.class, "msgType");
          textXStream.aliasField("MsgId", TextMsg.class, "msgId");
          textXStream.aliasField("Content", TextMsg.class, "content");

          textXStream.aliasField("Event", TextMsg.class, "event");
          textXStream.aliasField("EventKey", TextMsg.class, "eventKey");
        }
      }
    }
  }

  public static TextMsg textFromXml(String xml) {
    initTextStream();
    return (TextMsg) textXStream.fromXML(xml);
  }

  public static String textToXml(TextMsg text) {
    initTextStream();
    return textXStream.toXML(text);
  }
}
