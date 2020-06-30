package com.circuitbreaker.core.breaker;

/**
 * 熔断器策略类定义
 */
public interface CircuitBreaker {

  /**
   * 记录访问次数，CircuitService会在每次调用业务前访问该方法
   */
  void access();

  /**
   * 记录失败次数，CircuitService会在每次调用业务抛出异常后访问方法
   */
  void failure();

  /**
   * 返回当前熔断器的开启状态
   */
  boolean isOpen();

}
