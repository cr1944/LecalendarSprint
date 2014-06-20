package ryan.lenovo.lecalendarsprint;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener {
    private static final String TAG = "SettingsActivity";
    public static final String KEY_SPRINT_NAMES = "pref_sprint_names";
    EditTextPreference mSprintNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mSprintNames = (EditTextPreference) findPreference(KEY_SPRINT_NAMES);
        mSprintNames.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, preference.getClass().getName() + ", newValue:" + newValue);
        if (preference == mSprintNames) {
            String s = (String) newValue;
            if (!s.contains("#")) {
                Toast.makeText(this, R.string.sprint_name_input_tips, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
