package com.seebaldtart.projectnewsapp;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);
            EditTextPreference userInput = (EditTextPreference) findPreference(getString(R.string.user_input_key));
            userInput.setText(QueryUtils.getUserInput());
            Preference limit = findPreference(getString(R.string.settings_limit_key));
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(userInput, QueryUtils.getUserInput());
            bindPreferenceSummaryToValue(limit);
            bindPreferenceSummaryToValue(orderBy);
        }
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
        private void bindPreferenceSummaryToValue(Preference preference) {
            if (preference != null) {
                preference.setOnPreferenceChangeListener(this);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                String preferenceString = sharedPreferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            }
        }
        private void bindPreferenceSummaryToValue(EditTextPreference preference, String newValue) {
            if (preference != null) {
                preference.setOnPreferenceChangeListener(this);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                String preferenceString = sharedPreferences.getString(newValue, "");
                onPreferenceChange(preference, preferenceString);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}