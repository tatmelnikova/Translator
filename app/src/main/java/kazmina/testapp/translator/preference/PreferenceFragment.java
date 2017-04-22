package kazmina.testapp.translator.preference;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;


import kazmina.testapp.translator.R;

/**
 * настройки приложения
 */

public class PreferenceFragment extends PreferenceFragmentCompat{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        Preference preference = getPreferenceManager().findPreference("settings_about");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://translate.yandex.ru/"));
                startActivity(intent);
                return true;
            }
        });

    }

    public static PreferenceFragment getInstance(){
        return new PreferenceFragment();
    }

}
