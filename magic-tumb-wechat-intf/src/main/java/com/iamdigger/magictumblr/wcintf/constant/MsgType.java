package com.iamdigger.magictumblr.wcintf.constant;

import lombok.Getter;

/**
 * @author Sam
 * @since 3.0.0
 */
@Getter
public enum MsgType {
  TEXT("text"), EVENT("event");

  String type;

  MsgType(String type) {
    this.type = type;
  }

  public static MsgType fromType(String type) {
    MsgType[] msgTypes = MsgType.values();
    for (MsgType msgType : msgTypes) {
      if (msgType.getType().equals(type)) {
        return msgType;
      }
    }
    throw new RuntimeException("Unsupported message type " + type);
  }
}
