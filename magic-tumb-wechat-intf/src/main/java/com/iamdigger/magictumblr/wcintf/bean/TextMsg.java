package com.iamdigger.magictumblr.wcintf.bean;

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

  private String event;
  private String eventKey;
}
