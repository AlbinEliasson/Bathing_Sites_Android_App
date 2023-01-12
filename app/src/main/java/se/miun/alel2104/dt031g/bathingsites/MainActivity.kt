package se.miun.alel2104.dt031g.bathingsites

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import se.miun.alel2104.dt031g.bathingsites.fragments.BathingSitesFragment

class MainActivity : AppCompatActivity() {
    private var fm: FragmentManager? = null
    private var fragment: BathingSitesFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddBathingSiteButtonListener()

        fm = supportFragmentManager
        fragment = fm!!.findFragmentById(R.id.fragment_container_view) as BathingSitesFragment
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_setting -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        fragment?.initNumberOfBathingSites()
    }

    override fun onPause() {
        super.onPause()
        fragment?.releaseDatabase()
    }

    private fun initAddBathingSiteButtonListener() {
        val addBathingSiteButton: View = findViewById(R.id.add_bathing_site_button)
        addBathingSiteButton.setOnClickListener {
            startActivity(Intent(this, AddBathingSiteActivity::class.java))
        }
    }
}