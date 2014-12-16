package com.f2prateek.rx.preferences;

import android.content.SharedPreferences;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

import static com.f2prateek.rx.preferences.Random.nextBoolean;
import static com.f2prateek.rx.preferences.Random.nextInt;
import static com.f2prateek.rx.preferences.TestUtils.verifyNoMoreInteractionsWithObserver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.robolectric.shadows.ShadowPreferenceManager.getDefaultSharedPreferences;

@RunWith(RobolectricTestRunner.class) //
public class SharedPreferencesObservableTest {
  SharedPreferences sharedPreferences;
  Observable<String> observable;

  @Before public void setUp() {
    sharedPreferences = getDefaultSharedPreferences(Robolectric.application);
    sharedPreferences.edit().clear().commit();
    SharedPreferencesObservable.SHARED_PREFERENCES_OBSERVABLES.clear();
    observable = SharedPreferencesObservable.observe(sharedPreferences);
  }

  @Test public void instancesAreCached() {
    assertThat(SharedPreferencesObservable.SHARED_PREFERENCES_OBSERVABLES).hasSize(1)
        .containsEntry(sharedPreferences, observable);
  }

  @Test public void subscriberIsInvoked() {
    Observer<String> observer = mock(Observer.class);
    observable.subscribe(observer);
    InOrder inOrder = inOrder(observer);
    inOrder.verify(observer, never()).onNext(any(String.class));
    sharedPreferences.edit().putBoolean("foo", nextBoolean()).commit();
    inOrder.verify(observer, times(1)).onNext("foo");
    sharedPreferences.edit().putString("bar", "baz").commit();
    inOrder.verify(observer, times(1)).onNext("bar");
  }

  @Test public void unsubscribedSubscriberIsNotInvoked() {
    Observer<String> observer = mock(Observer.class);
    Subscription subscription = observable.subscribe(observer);
    subscription.unsubscribe();
    sharedPreferences.edit().putFloat("qux", nextInt()).commit();
    verifyNoMoreInteractionsWithObserver(inOrder(observer), observer);
  }
}