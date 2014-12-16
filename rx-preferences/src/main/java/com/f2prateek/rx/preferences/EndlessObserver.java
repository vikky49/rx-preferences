package com.f2prateek.rx.preferences;

import rx.Observer;

abstract class EndlessObserver<T> implements Observer<T> {
  @Override public void onCompleted() {
    // ignored
  }

  @Override public void onError(Throwable e) {
    // ignored
  }
}