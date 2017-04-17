package kazmina.testapp.translator.preference;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import kazmina.testapp.translator.R;

/**
 * настройки приложения
 */

public class PreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    public static PreferenceFragment getInstance(){
        return new PreferenceFragment();
    }
}
