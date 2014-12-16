package com.f2prateek.rx.preferences;

import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

import static com.f2prateek.rx.preferences.Utils.assertNotNull;

public final class SharedPreferencesObservable {
  static final Map<SharedPreferences, Observable<String>> SHARED_PREFERENCES_OBSERVABLES =
      new HashMap<SharedPreferences, Observable<String>>();

  private SharedPreferencesObservable() {
    throw new AssertionError("No Instances");
  }

  public synchronized static Observable<String> observe(final SharedPreferences sharedPreferences) {
    assertNotNull(sharedPreferences);
    Observable<String> observable = SHARED_PREFERENCES_OBSERVABLES.get(sharedPreferences);
    if (observable == null) {
      observable = Observable.create(new Observable.OnSubscribe<String>() {
        @Override public void call(final Subscriber<? super String> subscriber) {
          final SharedPreferences.OnSharedPreferenceChangeListener listener =
              new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                    String key) {
                  subscriber.onNext(key);
                }
              };

          subscriber.add(Subscriptions.create(new Action0() {
            @Override public void call() {
              sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
              SHARED_PREFERENCES_OBSERVABLES.remove(sharedPreferences);
            }
          }));

          sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        }
      });
      SHARED_PREFERENCES_OBSERVABLES.put(sharedPreferences, observable);
    }
    return observable;
  }
}

