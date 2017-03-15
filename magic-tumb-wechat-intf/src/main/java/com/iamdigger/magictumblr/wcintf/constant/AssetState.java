package com.iamdigger.magictumblr.wcintf.constant;

import lombok.Getter;

/**
 * @author Sam
 * @since 3.0.0
 */
@Getter
public enum AssetState {

  INIT(1, "初始化"),  SUCCESS(2, "解析成功"),  FAILED(3, "解析失败"), PROCESSING(4, "正在处理") ;

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
