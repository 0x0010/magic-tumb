package com.iamdigger.magictumblr.wcintf.bean;

import java.sql.Timestamp;
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
public class MagicAssetDO {

  private Long id;
  private String assetId;
  private String committer;
  private String videoCode;
  private String imageCode;
  private String assetHash;
  private String assetContent;
  private Timestamp createTime;
  private Integer state;
}
