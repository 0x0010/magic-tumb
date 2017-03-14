package com.iamdigger.magictumblr.wcintf.exception;

import lombok.Getter;

/**
 * @author Sam
 * @since 3.0.0
 */
@Getter
public class MagicException extends RuntimeException {
  private String errorMsg;
  public MagicException(String errorMsg) {
    super(errorMsg);
    this.errorMsg = errorMsg;
  }
}
