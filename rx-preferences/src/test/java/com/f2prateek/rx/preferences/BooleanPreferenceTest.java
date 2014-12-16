package com.f2prateek.rx.preferences;

import android.content.SharedPreferences;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import rx.Observer;
import rx.Subscription;
import rx.observers.TestObserver;

import static com.f2prateek.rx.preferences.Random.nextBoolean;
import static com.f2prateek.rx.preferences.TestUtils.verifyNoMoreInteractionsWithObserver;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.shadows.ShadowPreferenceManager.getDefaultSharedPreferences;

@RunWith(RobolectricTestRunner.class) //
public class BooleanPreferenceTest {
  SharedPreferences sharedPreferences;
  BooleanPreference booleanPreference;

  @Before public void setUp() {
    sharedPreferences = getDefaultSharedPreferences(Robolectric.application);
    sharedPreferences.edit().clear().commit();
    booleanPreference = new BooleanPreference(sharedPreferences, "foo");
  }

  @Test public void subscriberIsInvoked() {
    Observer<BooleanPreference> observer = mock(Observer.class);
    booleanPreference.asObservable().subscribe(observer); // #1: Initial Value
    sharedPreferences.edit().putBoolean("foo", nextBoolean()).commit(); // #2
    booleanPreference.set(nextBoolean()); // #3

    verify(observer, times(3)).onNext(booleanPreference);
  }

  @Test public void unsubscribedSubscriberIsNotInvoked() {
    Observer<BooleanPreference> observer = mock(Observer.class);
    Subscription subscription =
        booleanPreference.asObservable().subscribe(new TestObserver<BooleanPreference>(observer));
    InOrder inOrder = inOrder(observer);
    inOrder.verify(observer, times(1)).onNext(booleanPreference);
    subscription.unsubscribe();
    sharedPreferences.edit().putBoolean("foo", nextBoolean()).commit();
    sharedPreferences.edit().putBoolean("foo", nextBoolean()).commit();
    verifyNoMoreInteractionsWithObserver(inOrder, observer);
  }
}
