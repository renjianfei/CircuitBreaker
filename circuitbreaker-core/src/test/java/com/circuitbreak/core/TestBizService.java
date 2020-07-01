package com.circuitbreak.core;

import com.circuitbreaker.core.breaker.CircuitBreaker;
import com.circuitbreaker.core.service.AbstractCircuitExecutor;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TestBizService extends AbstractCircuitExecutor<Integer, Boolean> {

  public TestBizService(CircuitBreaker breaker) {
    super(breaker);
  }

  @Override
  protected Boolean invoke(Integer request) throws Exception {

    System.out.println(String.format("Breaker状态：连通, request:%d", request));

    long stop = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() + 30;

    while (stop < LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()) {

    }

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
