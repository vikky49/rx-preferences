package com.f2prateek.rx.preferences;

import android.content.SharedPreferences;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

import static com.f2prateek.rx.preferences.Utils.assertNotNull;
import static com.f2prateek.rx.preferences.Utils.assertNotNullOrEmpty;

public class IntPreference {
  private final SharedPreferences sharedPreferences;
  private final String key;
  private final int defaultValue;

  public IntPreference(SharedPreferences preferences, String key) {
    this(preferences, key, 0);
  }

  public IntPreference(SharedPreferences sharedPreferences, String key, int defaultValue) {
    this.sharedPreferences = assertNotNull(sharedPreferences);
    this.key = assertNotNullOrEmpty(key);
    this.defaultValue = defaultValue;
  }

  public int get() {
    return sharedPreferences.getInt(key, defaultValue);
  }

  public boolean isSet() {
    return sharedPreferences.contains(key);
  }

  public void set(int value) {
    sharedPreferences.edit().putInt(key, value).commit();
  }

  public void delete() {
    sharedPreferences.edit().remove(key).commit();
  }

  public Observable<IntPreference> asObservable() {
    return Observable.create(new OnSubscribeFromIntPreference(this));
  }

  static class OnSubscribeFromIntPreference implements Observable.OnSubscribe<IntPreference> {
    final IntPreference intPreference;

    OnSubscribeFromIntPreference(IntPreference intPreference) {
      this.intPreference = intPreference;
    }

    @Override public void call(final Subscriber<? super IntPreference> subscriber) {
      subscriber.onNext(intPreference);

      final Subscription subscription =
          SharedPreferencesObservable.observe(intPreference.sharedPreferences)
              .filter(new Func1<String, Boolean>() {
                @Override public Boolean call(String s) {
                  return intPreference.key.equals(s);
                }
              })
              .subscribe(new EndlessObserver<String>() {
                @Override public void onNext(String s) {
                  subscriber.onNext(intPreference);
                }
              });

      subscriber.add(Subscriptions.create(new Action0() {
        @Override public void call() {
          subscription.unsubscribe();
        }
      }));
    }
  }
}