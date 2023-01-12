package se.miun.alel2104.dt031g.bathingsites

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

/**
 * The settings activity where the weather link used to access weather data can be changed.
 * @author Albin Eliasson
 */
class SettingsActivity : AppCompatActivity() {

    /**
     * Overridden function to set content view and initialize the supportFragmentManager.
     */
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

    /**
     * Overridden function to make sure the "back navigation button" takes the user back
     * to the previous activity.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * The settingsFragment which makes sure the weather link gets saved and is usable in other
     * activities with PreferenceManager.
     */
    class SettingsFragment : PreferenceFragmentCompat() {

        /**
         * Overridden function to set the settings preference and initialize the weather link and
         * the weather link listener.
         */
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            initWeatherDataLinkPreference()
            initWeatherLinkPrefListener()
        }

        /**
         * The weather link key to access the weather link from SharedPreferences.
         */
        companion object {
            const val WEATHER_LINK_KEY = "weatherLink"
        }

        /**
         * Function to initialize the weather link in the EditTextPreference using
         * SharedPreferences.
         */
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

        /**
         * Function to initialize the weather link EditTextPreference change listener which
         * stores the new weather link if changed.
         */
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