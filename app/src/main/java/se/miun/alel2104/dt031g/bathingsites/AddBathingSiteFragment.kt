package se.miun.alel2104.dt031g.bathingsites

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddBathingSiteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddBathingSiteFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_bathing_site, container, false)
    }

    /**
     * Initializes the logic after onCreate.
     */
    override fun onStart() {
        super.onStart()
        initCurrentDateInForm()
    }

    /**
     * Initializes the clear and add button menu.
     * @param menu the menu to be inflated.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_bathing_site_menu, menu)
    }

    /**
     * Checks if the clear/save button is pressed and clears all the inputs from the add
     * bathing site inputs or saves the inputs if all mandatory information is entered.
     * @param item the menu items, save or clear buttons.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val bathingSiteName = view?.findViewById<EditText>(R.id.bathingSiteInputName)
        val bathingSiteDescription = view?.findViewById<EditText>(R.id.bathingSiteInputDescription)
        val bathingSiteAddress = view?.findViewById<EditText>(R.id.bathingSiteInputAddress)
        val bathingSiteLatitude = view?.findViewById<EditText>(R.id.bathingSiteInputLatitude)
        val bathingSiteLongitude = view?.findViewById<EditText>(R.id.bathingSiteInputLongitude)
        val bathingSiteGrade = view?.findViewById<RatingBar>(R.id.bathingSiteInputGrade)
        val bathingSiteWaterTmp = view?.findViewById<EditText>(R.id.bathingSiteInputWaterTemp)
        val bathingSiteWaterTmpDate = view?.findViewById<EditText>(R.id.bathingSiteInputDateTemp)

        when (item.itemId) {
            R.id.add_bathing_site_save_button -> {

                if (bathingSiteName!!.length() == 0) {
                    bathingSiteName.error = getString(R.string.bathing_site_name_error)

                } else if (bathingSiteAddress!!.length() == 0) {
                    if (bathingSiteLatitude!!.length() == 0 || bathingSiteLongitude!!.length() == 0) {

                        bathingSiteAddress.error = getString(R.string.bathing_site_location_error)
                        bathingSiteLatitude.error = getString(R.string.bathing_site_location_error)
                        bathingSiteLongitude!!.error = getString(R.string.bathing_site_location_error)
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
                if (bathingSiteGrade!!.rating != 0.0F) {
                    bathingSiteGrade.rating = 0.0F
                }

                clearFormInputs(listOf(bathingSiteName, bathingSiteDescription,
                    bathingSiteAddress, bathingSiteLatitude, bathingSiteLongitude,
                    bathingSiteWaterTmp))
            }

            R.id.add_bathing_site_weather_option -> {
                ShowWeatherDialogFragment().show(childFragmentManager,
                    ShowWeatherDialogFragment.TAG)
            }

            R.id.add_bathing_site_settings_option -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
    * Loops the edit text inputs and clears the input if they contain information.
    * @param formList the list of EditText.
    */
    private fun clearFormInputs(formList: List<EditText?>) {
        formList.forEach {
            if (it!!.length() != 0) {
                it.text.clear()
            }
        }
        // Reset the water temp date to current date
        initCurrentDateInForm()
    }

    /**
     * Loops the edit text inputs and clear the error messages.
     * @param formList the list of EditText.
     */
    private fun resetErrorMessages(formList: List<EditText?>) {
        formList.forEach {
            it!!.error = null
        }
    }

    /**
     * Initializes the current date to the water temp date input.
     */
    private fun initCurrentDateInForm() {
        val bathingSiteWaterTmpDate = view?.findViewById<EditText>(R.id.bathingSiteInputDateTemp)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val formattedDate = formatter.format(Date())

        bathingSiteWaterTmpDate!!.setText(formattedDate)
    }

    /**
     * Creates an alert dialog displaying the information entered from the add bathing site inputs.
     * @param name the EditText for the name of the bathing site.
     * @param description the EditText for the description of the bathing site.
     * @param address the EditText for the address of the bathing site.
     * @param latitude the EditText for the latitude of the bathing site.
     * @param longitude the EditText for the longitude of the bathing site.
     * @param grade the RatingBar for the grade of the bathing site.
     * @param waterTmp the EditText for the water temperature of the bathing site.
     * @param date the EditText for the date of the water temperature of the bathing site.
     */
    private fun createAlertDialog(name: EditText?, description: EditText?, address: EditText?,
                                  latitude: EditText?, longitude: EditText?, grade: RatingBar?,
                                  waterTmp: EditText?, date: EditText?) {

        val space = getString(R.string.space)
        val newLine = getString(R.string.new_line)
        val formInfo = getString(R.string.edit_text_bathing_site_name) + space +
                name!!.text + newLine +
                getString(R.string.edit_text_bathing_site_description) + space +
                description!!.text + newLine +
                getString(R.string.edit_text_bathing_site_address) + space +
                address!!.text + newLine +
                getString(R.string.edit_text_bathing_site_latitude) + space +
                latitude!!.text + newLine +
                getString(R.string.edit_text_bathing_site_longitude) + space +
                longitude!!.text + newLine +
                getString(R.string.edit_text_bathing_site_grade) + space +
                grade!!.rating + newLine +
                getString(R.string.edit_text_bathing_site_water_temperature) + space +
                waterTmp!!.text + newLine +
                getString(R.string.edit_text_bathing_site_water_temperature_date) + space +
                date!!.text

        val alertBuilder = context?.let { AlertDialog.Builder(it) }
        alertBuilder?.setTitle(getString(R.string.dialog_title))
        alertBuilder?.setMessage(formInfo)
        alertBuilder?.setPositiveButton(R.string.dialog_buttonText, null)

        val alertDialog: AlertDialog = alertBuilder!!.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddBathingSiteFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddBathingSiteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}