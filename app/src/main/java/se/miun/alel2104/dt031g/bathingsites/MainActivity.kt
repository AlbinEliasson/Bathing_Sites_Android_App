package se.miun.alel2104.dt031g.bathingsites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddBathingSiteButtonListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initAddBathingSiteButtonListener() {
        val addBathingSiteButton: View = findViewById(R.id.add_bathing_site_button)
        addBathingSiteButton.setOnClickListener {
            startActivity(Intent(this, AddBathingSiteActivity::class.java))
        }
    }
}