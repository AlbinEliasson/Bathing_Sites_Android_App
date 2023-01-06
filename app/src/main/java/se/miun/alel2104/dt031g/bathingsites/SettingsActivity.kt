package se.miun.alel2104.dt031g.bathingsites

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            initWeatherDataLinkPreference()
            initWeatherLinkPrefListener()
        }

        companion object {
            const val WEATHER_LINK_KEY = "weatherLink"
        }

        private fun initWeatherDataLinkPreference() {
            val weatherLinkPref = findPreference<EditTextPreference>(
                getString(R.string.store_weather_link_key))
            val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context)

            if (weatherLinkPref != null) {
                weatherLinkPref.text = preferences.getString(WEATHER_LINK_KEY, getString(
                    R.string.store_weather_link))
            }
        }

        private fun initWeatherLinkPrefListener() {
            val weatherLinkPref = findPreference<EditTextPreference>(
                getString(R.string.store_weather_link_key))
            val preferences: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)
            val sharedEditor = preferences.edit()

            if (weatherLinkPref != null) {
                weatherLinkPref.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        sharedEditor.putString(WEATHER_LINK_KEY, newValue as String?).apply()
                        true
                    }
            }
        }
    }
}