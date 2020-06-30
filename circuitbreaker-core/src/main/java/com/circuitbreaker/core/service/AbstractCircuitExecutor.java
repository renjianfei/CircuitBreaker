package com.circuitbreaker.core.service;

import com.circuitbreaker.core.breaker.CircuitBreaker;

/**
 * 熔断执行器，使用CircuitBreaker对业务进行熔断
 * @param <Req> 业务请求参数
 * @param <Res> 业务响应参数
 */
public abstract class AbstractCircuitExecutor<Req, Res> {

  private CircuitBreaker breaker;

  public AbstractCircuitExecutor(CircuitBreaker breaker) {

    this.breaker = breaker;

  }

  /**
   * 模板方法，内部调用业务层实现的invoke方法或fallBack方法
   * @return
   * @throws Exception
   */
  public Res execute(Req request) throws Exception {

    if (breaker.isOpen()) {
      return fallBack(request);
    }

    Res result;

    try {

      breaker.access();

      result = invoke(request);

    } catch (Exception e) {

      breaker.failure();

      if (breaker.isOpen()) {
        return fallBack(request);
      }

      throw e;
    }

    return result;
  }

  /**
   * 业务层覆盖发方法，为熔断时执行的业务方法
   * @param request
   * @return
   * @throws Exception
   */
  abstract protected Res invoke(Req request) throws Exception;

  /**
   * 业务层覆盖该方法，熔断后会执行该方法
   * @param request
   * @return
   */
  abstract protected Res fallBack(Req request);

}
