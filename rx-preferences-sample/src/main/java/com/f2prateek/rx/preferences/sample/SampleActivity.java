package com.f2prateek.rx.preferences.sample;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.f2prateek.rx.preferences.BooleanPreference;
import rx.Observer;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.widget.Toast.LENGTH_SHORT;

public class SampleActivity extends Activity {

  BooleanPreference fooPreference;

  @InjectView(R.id.foo_value) TextView fooValue;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Views
    setContentView(R.layout.sample_activity);
    ButterKnife.inject(this);

    // Preferences
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);

    // foo
    fooPreference = new BooleanPreference(sharedPreferences, "foo");
    fooPreference.asObservable().subscribe(new Observer<BooleanPreference>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {

      }

      @Override public void onNext(BooleanPreference booleanPreference) {
        fooValue.setText(String.valueOf(booleanPreference.get()));
      }
    });
  }

  @OnClick({ R.id.foo }) public void greetingClicked(Button button) {
    fooPreference.set(!fooPreference.get());
    Toast.makeText(this, "Foo preference updated!", LENGTH_SHORT).show();
  }
}
