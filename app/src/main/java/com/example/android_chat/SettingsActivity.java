package com.example.android_chat;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection deprecation
        addPreferencesFromResource(R.xml.preferences);
    }
}
