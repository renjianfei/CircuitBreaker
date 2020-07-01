package com.circuitbreak.core;

import java.util.Iterator;
import java.util.Random;

public class TestDataProvider implements Iterator<Object[]> {

  public TestDataProvider(int bound, int size) {
    this.bound = bound;
    this.size = size;
  }

  private Random random = new Random();

  private int bound;

  private int size;

  private int used;

  public boolean hasNext() {
    return used < size;
  }

  public Object[] next() {
    used++;
    return new Object[]{random.nextInt(bound)};
  }
}
