package com.iamdigger.magictumblr.wcintf.constant;

import lombok.Getter;

/**
 * @author Sam
 * @since 3.0.0
 */
@Getter
public enum AssetState {

  INIT(1, "迫不及待地等待被处理。。。"),
  SUCCESS(2, "功夫不负有心人，长使英雄泪满襟呐！"),
  FAILED(3, "我说我解析失败了，你信吗？"),
  PROCESSING(4, "正在处理"),
  UNSUPPORTED_URL(5, "暂不支持此URL的解析，你懂的。"),
  NOT_URL(6, "别逗，这就不是个URL！");

  Integer state;
  String desc;

  AssetState(Integer state, String desc) {
    this.state = state;
    this.desc = desc;
  }

  public static AssetState valueOf(Integer state) {
    for (AssetState assetState : AssetState.values()) {
      if (assetState.state.equals(state)) {
        return assetState;
      }
    }
    throw new RuntimeException("Invalid state " + state);
  }
}
