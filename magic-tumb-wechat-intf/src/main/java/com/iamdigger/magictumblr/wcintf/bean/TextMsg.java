package com.iamdigger.magictumblr.wcintf.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sam
 * @since 3.0.0
 */

@Getter
@Setter
@ToString
public class TextMsg {
  private String toUserName;
  private String fromUserName;
  private Long createTime;
  private String msgType;
  private String content;
  private Long msgId;
}
