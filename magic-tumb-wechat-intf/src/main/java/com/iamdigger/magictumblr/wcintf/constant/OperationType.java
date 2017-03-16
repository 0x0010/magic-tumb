package com.iamdigger.magictumblr.wcintf.constant;

import lombok.Getter;

/**
 * @author Sam
 * @since 3.0.0
 */
@Getter
public enum OperationType {
  //Query
  Q("Q+"),

  // Get
  G("G+"),

  // Help
  H("H+");

  String operation;

  OperationType(String ch) {
    this.operation = ch;
  }

  public static OperationType of(String ch) {
    for (OperationType operationType : OperationType.values()) {
      if (operationType.getOperation().equalsIgnoreCase(ch)) {
        return operationType;
      }
    }
    throw new RuntimeException("Unknown operation type of " + ch);
  }
}
