package se.miun.alel2104.dt031g.bathingsites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.*

class AddBathingSiteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bathing_site)
    }

    override fun onStart() {
        super.onStart()
        initCurrentDateInForm()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_bathing_site_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val bathingSiteName = findViewById<EditText>(R.id.bathingSiteInputName)
        val bathingSiteDescription = findViewById<EditText>(R.id.bathingSiteInputDescription)
        val bathingSiteAddress = findViewById<EditText>(R.id.bathingSiteInputAddress)
        val bathingSiteLatitude = findViewById<EditText>(R.id.bathingSiteInputLatitude)
        val bathingSiteLongitude = findViewById<EditText>(R.id.bathingSiteInputLongitude)
        val bathingSiteGrade = findViewById<RatingBar>(R.id.bathingSiteInputGrade)
        val bathingSiteWaterTmp = findViewById<EditText>(R.id.bathingSiteInputWaterTemp)
        val bathingSiteWaterTmpDate = findViewById<EditText>(R.id.bathingSiteInputDateTemp)

        when (item.itemId) {
            R.id.add_bathing_site_save_button -> {

                if (bathingSiteName.length() == 0) {
                    bathingSiteName.error = getString(R.string.bathing_site_name_error)

                } else if (bathingSiteAddress.length() == 0) {
                    if (bathingSiteLatitude.length() == 0 || bathingSiteLongitude.length() == 0) {

                        bathingSiteAddress.error = getString(R.string.bathing_site_location_error)
                        bathingSiteLatitude.error = getString(R.string.bathing_site_location_error)
                        bathingSiteLongitude.error = getString(R.string.bathing_site_location_error)
                    } else {
                        resetErrorMessages(listOf(bathingSiteAddress, bathingSiteLatitude,
                            bathingSiteLongitude))

                        createAlertDialog(bathingSiteName, bathingSiteDescription,
                            bathingSiteAddress, bathingSiteLatitude, bathingSiteLongitude,
                            bathingSiteGrade, bathingSiteWaterTmp, bathingSiteWaterTmpDate)
                    }

                } else {
                    createAlertDialog(bathingSiteName, bathingSiteDescription,
                        bathingSiteAddress, bathingSiteLatitude, bathingSiteLongitude,
                        bathingSiteGrade, bathingSiteWaterTmp, bathingSiteWaterTmpDate)
                }
            }

            R.id.add_bathing_site_clear_button -> {
                // Reset the rating bar
                if (bathingSiteGrade.rating != 0.0F) {
                    bathingSiteGrade.rating = 0.0F
                }

                clearFormInputs(listOf(bathingSiteName, bathingSiteDescription,
                    bathingSiteAddress, bathingSiteLatitude, bathingSiteLongitude,
                    bathingSiteWaterTmp))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clearFormInputs(formList: List<EditText>) {
        formList.forEach {
            if (it.length() != 0) {
                it.text.clear()
            }
        }
        // Reset the water temp date to current date
        initCurrentDateInForm()
    }

    private fun resetErrorMessages(formList: List<EditText>) {
        formList.forEach {
            it.error = null
        }
    }

    private fun initCurrentDateInForm() {
        val bathingSiteWaterTmpDate = findViewById<EditText>(R.id.bathingSiteInputDateTemp)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val formattedDate = formatter.format(Date())

        bathingSiteWaterTmpDate.setText(formattedDate)
    }

    private fun createAlertDialog(name: EditText, description: EditText, address: EditText,
                                  latitude: EditText, longitude: EditText, grade: RatingBar,
                                  waterTmp: EditText, date: EditText) {

        val space = getString(R.string.space)
        val newLine = getString(R.string.new_line)
        val formInfo = getString(R.string.edit_text_bathing_site_name) + space +
                name.text + newLine +
                getString(R.string.edit_text_bathing_site_description) + space +
                description.text + newLine +
                getString(R.string.edit_text_bathing_site_address) + space +
                address.text + newLine +
                getString(R.string.edit_text_bathing_site_latitude) + space +
                latitude.text + newLine +
                getString(R.string.edit_text_bathing_site_longitude) + space +
                longitude.text + newLine +
                getString(R.string.edit_text_bathing_site_grade) + space +
                grade.rating + newLine +
                getString(R.string.edit_text_bathing_site_water_temperature) + space +
                waterTmp.text + newLine +
                getString(R.string.edit_text_bathing_site_water_temperature_date) + space +
                date.text

        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(getString(R.string.dialog_title))
        alertBuilder.setMessage(formInfo)
        alertBuilder.setPositiveButton(R.string.dialog_buttonText, null)

        val alertDialog: AlertDialog = alertBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}