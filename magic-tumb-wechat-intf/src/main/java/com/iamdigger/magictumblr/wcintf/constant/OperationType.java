package com.iamdigger.magictumblr.wcintf.constant;

import lombok.Getter;

/**
 * @author Sam
 * @since 3.0.0
 */
@Getter
public enum OperationType {
  //Query
  Q('Q'),

  // Get
  G('G'),

  // Help
  H('H');

  char operation;

  OperationType(char ch) {
    this.operation = ch;
  }

  public static OperationType of(char ch) {
    for (OperationType operationType : OperationType.values()) {
      if (ch == operationType.getOperation()) {
        return operationType;
      }
    }
    throw new RuntimeException("Unknown operation type of " + ch);
  }
}
