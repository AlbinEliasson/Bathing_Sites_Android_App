package se.miun.alel2104.dt031g.bathingsites

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Represents the activity for adding new bathing sites.
 * @author Albin Eliasson
 */
class AddBathingSiteActivity : AppCompatActivity() {

    /**
     * Overridden function to set content view.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bathing_site)
    }

    /**
     * Overridden function which releases the database when activity is to be destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        AppDataBase.getDatabase(this).destroy()
    }
}