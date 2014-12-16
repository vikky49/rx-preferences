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
import static com.f2prateek.rx.preferences.Random.nextString;
import static com.f2prateek.rx.preferences.TestUtils.verifyNoMoreInteractionsWithObserver;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.shadows.ShadowPreferenceManager.getDefaultSharedPreferences;

@RunWith(RobolectricTestRunner.class) //
public class StringPreferenceTest {
  SharedPreferences sharedPreferences;
  StringPreference stringPreference;

  @Before public void setUp() {
    sharedPreferences = getDefaultSharedPreferences(Robolectric.application);
    sharedPreferences.edit().clear().commit();
    stringPreference = new StringPreference(sharedPreferences, "bar");
  }

  @Test public void subscriberIsInvoked() {
    Observer<StringPreference> observer = mock(Observer.class);
    stringPreference.asObservable().subscribe(observer); // #1: Initial Value
    sharedPreferences.edit().putBoolean("bar", nextBoolean()).commit(); // #2
    stringPreference.set(nextString()); // #3

    verify(observer, times(3)).onNext(stringPreference);
  }

  @Test public void unsubscribedSubscriberIsNotInvoked() {
    Observer<StringPreference> observer = mock(Observer.class);
    Subscription subscription =
        stringPreference.asObservable().subscribe(new TestObserver<StringPreference>(observer));
    InOrder inOrder = inOrder(observer);
    inOrder.verify(observer, times(1)).onNext(stringPreference);
    subscription.unsubscribe();
    sharedPreferences.edit().putString("bar", nextString()).commit();
    sharedPreferences.edit().putString("bar", nextString()).commit();
    stringPreference.set(nextString());
    verifyNoMoreInteractionsWithObserver(inOrder, observer);
  }
}
