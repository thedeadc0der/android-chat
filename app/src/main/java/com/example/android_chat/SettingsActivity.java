package com.example.android_chat;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    // A déclarer dans le MANIFESTE !
    // Cf. <activity android:name=".SettingsActivity"></activity>
    // dans la balise <application>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Utiliser des fragments plutot qu'une activité 'préférences'
        //noinspection deprecation
        addPreferencesFromResource(R.xml.preferences);
    }
}
