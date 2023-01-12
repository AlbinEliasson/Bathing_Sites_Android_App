package se.miun.alel2104.dt031g.bathingsites

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import se.miun.alel2104.dt031g.bathingsites.fragments.BathingSitesFragment

/**
 * The main starting activity which lets users navigate to add and view information about the
 * saved bathing-sites.
 * @author Albin Eliasson
 */
class MainActivity : AppCompatActivity() {
    private var fm: FragmentManager? = null
    private var fragment: BathingSitesFragment? = null

    /**
     * Overridden function to set content view, initialize the add bathing-site button and the
     * initiation of the bathingSiteFragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddBathingSiteButtonListener()

        fm = supportFragmentManager
        fragment = fm!!.findFragmentById(R.id.fragment_container_view) as BathingSitesFragment
    }

    /**
     * Overridden function to inflate the main menu containing the settings button.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Overridden function to start the settings-activity when pressed from menu.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_setting -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Overridden function to initialize the number of added bathing-sites when the
     * activity resumes.
     */
    override fun onResume() {
        super.onResume()
        fragment?.initNumberOfBathingSites()
    }

    /**
     * Overridden function to release the database in the AppDataBase singleton when the activity
     * is paused.
     */
    override fun onPause() {
        super.onPause()
        fragment?.releaseDatabase()
    }

    /**
     * Function to initialize the floating add bathing-site button listener which starts the
     * AddBathingSiteActivity.
     */
    private fun initAddBathingSiteButtonListener() {
        val addBathingSiteButton: View = findViewById(R.id.add_bathing_site_button)
        addBathingSiteButton.setOnClickListener {
            startActivity(Intent(this, AddBathingSiteActivity::class.java))
        }
    }
}