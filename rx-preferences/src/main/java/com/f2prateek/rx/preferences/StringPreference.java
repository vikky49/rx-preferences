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

public class StringPreference {
  private final SharedPreferences sharedPreferences;
  private final String key;
  private final String defaultValue;

  public StringPreference(SharedPreferences preferences, String key) {
    this(preferences, key, null);
  }

  public StringPreference(SharedPreferences sharedPreferences, String key, String defaultValue) {
    this.sharedPreferences = assertNotNull(sharedPreferences);
    this.key = assertNotNullOrEmpty(key);
    this.defaultValue = defaultValue;
  }

  public String get() {
    return sharedPreferences.getString(key, defaultValue);
  }

  public boolean isSet() {
    return sharedPreferences.contains(key);
  }

  public void set(String value) {
    sharedPreferences.edit().putString(key, value).commit();
  }

  public void delete() {
    sharedPreferences.edit().remove(key).commit();
  }

  public Observable<StringPreference> asObservable() {
    return Observable.create(new OnSubscribeFromStringPreference(this));
  }

  static class OnSubscribeFromStringPreference implements Observable.OnSubscribe<StringPreference> {
    final StringPreference stringPreference;

    OnSubscribeFromStringPreference(StringPreference stringPreference) {
      this.stringPreference = stringPreference;
    }

    @Override public void call(final Subscriber<? super StringPreference> subscriber) {
      subscriber.onNext(stringPreference);

      final Subscription subscription =
          SharedPreferencesObservable.observe(stringPreference.sharedPreferences)
              .filter(new Func1<String, Boolean>() {
                @Override public Boolean call(String s) {
                  return stringPreference.key.equals(s);
                }
              })
              .subscribe(new EndlessObserver<String>() {
                @Override public void onNext(String s) {
                  subscriber.onNext(stringPreference);
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