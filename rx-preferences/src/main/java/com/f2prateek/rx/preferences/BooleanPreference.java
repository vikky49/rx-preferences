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

public class BooleanPreference {
  private final SharedPreferences sharedPreferences;
  private final String key;
  private final boolean defaultValue;

  public BooleanPreference(SharedPreferences preferences, String key) {
    this(preferences, key, false);
  }

  public BooleanPreference(SharedPreferences sharedPreferences, String key, boolean defaultValue) {
    this.sharedPreferences = assertNotNull(sharedPreferences);
    this.key = assertNotNullOrEmpty(key);
    this.defaultValue = defaultValue;
  }

  public boolean get() {
    return sharedPreferences.getBoolean(key, defaultValue);
  }

  public boolean isSet() {
    return sharedPreferences.contains(key);
  }

  public void set(boolean value) {
    sharedPreferences.edit().putBoolean(key, value).commit();
  }

  public void delete() {
    sharedPreferences.edit().remove(key).commit();
  }

  public Observable<BooleanPreference> asObservable() {
    return Observable.create(new OnSubscribeFromBooleanPreference(this));
  }

  static class OnSubscribeFromBooleanPreference
      implements Observable.OnSubscribe<BooleanPreference> {
    final BooleanPreference booleanPreference;

    OnSubscribeFromBooleanPreference(BooleanPreference booleanPreference) {
      this.booleanPreference = booleanPreference;
    }

    @Override public void call(final Subscriber<? super BooleanPreference> subscriber) {
      subscriber.onNext(booleanPreference);

      final Subscription subscription =
          SharedPreferencesObservable.observe(booleanPreference.sharedPreferences)
              .filter(new Func1<String, Boolean>() {
                @Override public Boolean call(String s) {
                  return booleanPreference.key.equals(s);
                }
              })
              .subscribe(new EndlessObserver<String>() {
                @Override public void onNext(String s) {
                  subscriber.onNext(booleanPreference);
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