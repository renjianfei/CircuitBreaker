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
   * 熔断后自动恢复时间间隔
   */
  private long recoverMillis;

  /**
   * 熔断恢复时间点
   */
  private volatile long recoverStamp = Long.MAX_VALUE;

  /**
   * 已采样数量
   */
  private AtomicInteger accessTimes = new AtomicInteger();

  /**
   * 失败数量
   */
  private volatile int failureTimes = 0;

  public SimpleCircuitBreaker(int accessWindow, int breakThreshold, long recover,
      TimeUnit timeUnit) {

    this.breakThreshold = breakThreshold;

    this.accessWindow = accessWindow;

    this.recoverMillis = timeUnit.toMillis(recover);

  }

  /**
   * 记录访问次数，CircuitService会在每次调用业务前访问该方法
   */
  @Override
  public void access() {

    if (accessTimes.incrementAndGet() <= accessWindow) {
      return;
    }

    if (isRecoverAllowed()) {
      recover();
      return;
    }

  }

  /**
   * 记录失败次数，CircuitService会在每次调用业务抛出异常后访问方法
   */
  @Override
  public synchronized void failure() {

    if (++failureTimes == breakThreshold) {

      recoverStamp = recoverMillis + nowMillis();

      System.out.println(String.format("SimpleCircuitBreaker: 熔断, 熔断恢复时间 [%d]", recoverStamp));


    }
  }

  /**
   * 返回当前熔断器的开启状态
   */
  @Override
  public boolean isOpen() {
    return breakThreshold <= failureTimes;
  }


  private synchronized void recover() {

    if (accessTimes.incrementAndGet() <= accessWindow) {
      return;
    }

    System.out.println(String.format("SimpleCircuitBreaker.recover(), timeMills:%d", nowMillis()));

    accessTimes.set(0);
    failureTimes = 0;
    this.recoverStamp = Long.MAX_VALUE;
  }

  private synchronized void halfRecover() {

    if (!isOpen()) {
      return;
    }

    System.out.println(String.format("SimpleCircuitBreaker.halfRecover(), timeMills:%d", nowMillis()));

    failureTimes = breakThreshold - 1;
    this.recoverStamp = Long.MAX_VALUE;
  }

  private boolean isRecoverAllowed() {
    return nowMillis() > recoverStamp;
  }

  private long nowMillis() {
    return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
  }

}
