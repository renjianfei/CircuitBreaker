package com.circuitbreaker.core.breaker;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 熔断器策略默认实现类
 */
public class SimpleCircuitBreaker implements CircuitBreaker {

  /**
   * 错误熔断阈值
   */
  private int breakThreshold;

  /**
   * 采样窗口
   */
  private int accessWindow;

  /**
   * 熔断后自动恢复时间
   */
  private long recoverMillis;


  /**
   * 已采样数量
   */
  private AtomicInteger accessTimes = new AtomicInteger();

  /**
   * 失败数量
   */
  private AtomicInteger failureTimes = new AtomicInteger();

  public SimpleCircuitBreaker(int accessWindow, int breakThreshold, long recover, TimeUnit timeUnit) {

    this.breakThreshold = breakThreshold;

    this.accessWindow = accessWindow;

    this.recoverMillis =
        timeUnit.toMillis(recover) + LocalDateTime.now().toInstant(ZoneOffset.of("+8"))
            .toEpochMilli();

  }

  /**
   * 记录访问次数，CircuitService会在每次调用业务前访问该方法
   */
  @Override
  public void access() {

    if (accessTimes.incrementAndGet() <= accessWindow) {
      return;
    }

    if (!isOpen()) {
      recover();
      return;
    }

    if (isRecoverAllowed()) {

      halfRecover();
    }

  }

  /**
   * 记录失败次数，CircuitService会在每次调用业务抛出异常后访问方法
   */
  @Override
  public void failure() {

    failureTimes.incrementAndGet();

  }

  /**
   * 返回当前熔断器的开启状态
   */
  @Override
  public boolean isOpen() {
    return breakThreshold <= failureTimes.get();
  }


  private synchronized void recover() {

    if (accessTimes.incrementAndGet() <= accessWindow) {
      return;
    }

    accessTimes.set(1);
    failureTimes.set(1);
  }

  private synchronized void halfRecover() {

    if (!isOpen()) {
      return;
    }

    failureTimes.decrementAndGet();
  }

  private boolean isRecoverAllowed() {
    return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() > recoverMillis;
  }

}
