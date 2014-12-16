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

public class FloatPreference {
  private final SharedPreferences sharedPreferences;
  private final String key;
  private final float defaultValue;

  public FloatPreference(SharedPreferences preferences, String key) {
    this(preferences, key, 0f);
  }

  public FloatPreference(SharedPreferences sharedPreferences, String key, float defaultValue) {
    this.sharedPreferences = assertNotNull(sharedPreferences);
    this.key = assertNotNullOrEmpty(key);
    this.defaultValue = defaultValue;
  }

  public float get() {
    return sharedPreferences.getFloat(key, defaultValue);
  }

  public boolean isSet() {
    return sharedPreferences.contains(key);
  }

  public void set(float value) {
    sharedPreferences.edit().putFloat(key, value).commit();
  }

  public void delete() {
    sharedPreferences.edit().remove(key).commit();
  }

  public Observable<FloatPreference> asObservable() {
    return Observable.create(new OnSubscribeFromFloatPreference(this));
  }

  static class OnSubscribeFromFloatPreference implements Observable.OnSubscribe<FloatPreference> {
    final FloatPreference floatPreference;

    OnSubscribeFromFloatPreference(FloatPreference floatPreference) {
      this.floatPreference = floatPreference;
    }

    @Override public void call(final Subscriber<? super FloatPreference> subscriber) {
      subscriber.onNext(floatPreference);

      final Subscription subscription =
          SharedPreferencesObservable.observe(floatPreference.sharedPreferences)
              .filter(new Func1<String, Boolean>() {
                @Override public Boolean call(String s) {
                  return floatPreference.key.equals(s);
                }
              })
              .subscribe(new EndlessObserver<String>() {
                @Override public void onNext(String s) {
                  subscriber.onNext(floatPreference);
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