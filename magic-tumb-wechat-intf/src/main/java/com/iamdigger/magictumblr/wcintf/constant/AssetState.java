package com.iamdigger.magictumblr.wcintf.constant;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.Getter;

/**
 * @author Sam
 * @since 3.0.0
 */
@Getter
public enum AssetState {

  INIT(1, new String[]{"迫不及待地等待被处理。。。"}),
  SUCCESS(2, new String[]{"功夫不负有心人，长使英雄泪满襟呐！"}),
  FAILED(3, new String[]{"我说我解析失败了，你信吗？"}),
  PROCESSING(4, new String[]{"正在处理"}),
  UNSUPPORTED_URL(5, new String[]{"暂不支持此URL的解析，你懂的。", "反正是什么都没发现。", "内容简直是一片空白，毫无价值。"}),
  NOT_URL(6, new String[]{"别逗，这就不是个URL！", "记住，URL一般以http或者https开头。"});

  static ThreadLocal<Random> threadLocalRandom = new ThreadLocal<>();
  Integer state;
  List<String> desc;

  AssetState(Integer state, String[] desc) {
    this.state = state;
    this.desc = Arrays.asList(desc);
  }

  public static AssetState valueOf(Integer state) {
    for (AssetState assetState : AssetState.values()) {
      if (assetState.state.equals(state)) {
        return assetState;
      }
    }
    throw new RuntimeException("Invalid state " + state);
  }

  public String randomDesc() {
    if (null == threadLocalRandom.get()) {
      threadLocalRandom.set(new Random());
    }
    return desc.get(threadLocalRandom.get().nextInt(desc.size()));
  }
}
