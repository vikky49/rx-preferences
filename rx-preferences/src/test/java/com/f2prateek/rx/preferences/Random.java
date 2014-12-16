package com.f2prateek.rx.preferences;

public final class Random {
  final static java.util.Random RANDOM = new java.util.Random();

  private Random() {
    throw new AssertionError("No Instances.");
  }

  static int nextInt() {
    return RANDOM.nextInt();
  }

  static boolean nextBoolean() {
    return RANDOM.nextBoolean();
  }
}
