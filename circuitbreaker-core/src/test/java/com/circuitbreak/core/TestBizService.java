package com.circuitbreak.core;

import com.circuitbreaker.core.breaker.CircuitBreaker;
import com.circuitbreaker.core.service.AbstractCircuitExecutor;

public class TestBizService extends AbstractCircuitExecutor<Integer, Boolean> {

  public TestBizService(CircuitBreaker breaker) {
    super(breaker);
  }

  @Override
  protected Boolean invoke(Integer request) throws Exception {

    System.out.println(String.format("Breaker状态：连通, request:%d", request));

    if (request == 0) {
      throw new Exception("业务异常");
    }

    return false;
  }

  @Override
  protected Boolean fallBack(Integer request) {
    System.out.println(String.format("Breaker状态：断开, request:%d", request));
    return true;
  }

}
