package com.circuitbreak.core;

import com.circuitbreaker.core.breaker.CircuitBreaker;
import com.circuitbreaker.core.breaker.SimpleCircuitBreaker;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class SimpleCircuitBreakerTest {

  private CircuitBreaker breaker = new SimpleCircuitBreaker(50, 10, 1, TimeUnit.SECONDS);

  private TestBizService testBizService = new TestBizService(breaker);

  @DataProvider(name = "data")
  public Iterator<Object[]> providerData() {

    return new TestDataProvider(2,5000);
  }

  @Test(dataProvider = "data", threadPoolSize = 3, timeOut = 1000)
  public void testBreaker(int param) {

    try {
      final Boolean result = testBizService.execute(param);
    } catch (Exception e) {

    }

  }

}
