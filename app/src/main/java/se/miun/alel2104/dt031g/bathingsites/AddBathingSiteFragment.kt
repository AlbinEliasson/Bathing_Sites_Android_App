package se.miun.alel2104.dt031g.bathingsites

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
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
    private var weatherData: JSONObject? = null
    private var weatherImage: Bitmap? = null
    private val job = SupervisorJob()
    private val applicationScope = CoroutineScope(Dispatchers.IO + job)
    private var weatherInfoToDisplay = ""
    private var progressBar: ProgressBar? = null

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
        progressBar = view?.findViewById(R.id.download_progress_Bar)
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
                getWeatherInfo()
            }

            R.id.add_bathing_site_settings_option -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFragmentDialog() {
        val weatherDialogFragment: DialogFragment = ShowWeatherDialogFragment()
        val args = Bundle()

        args.putString(WEATHER_INFO_KEY, weatherInfoToDisplay)
        args.putParcelable(WEATHER_ICON_KEY, weatherImage)
        weatherDialogFragment.arguments = args
        weatherDialogFragment.show(childFragmentManager, "TAG")
    }

    //https://stackoverflow.com/questions/62260002/send-get-request-in-kotlin-android
    private fun getWeatherInfo() {
        showDownloadProgressBar(true)

        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            context)
        val weatherLink = preferences.getString(
            SettingsActivity.SettingsFragment.WEATHER_LINK_KEY, getString(
                R.string.store_weather_link))
        var allWeatherInfo = ""

        applicationScope.launch {
            try { //disable the strict mode otherwise perform this operation on netWork Thread
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)

                // GET Request
                val url = URL("https://dt031g.programvaruteknik.nu/bathingsites/weather.php?q=Stockholm")
                val connection = withContext(Dispatchers.IO) {
                    url.openConnection()
                }
                connection.doOutput = true
                // Get the response
                val inputStreamReader = BufferedReader(
                    InputStreamReader(withContext(Dispatchers.IO) {
                    connection.inputStream
                }))
                var line: String

                while (withContext(Dispatchers.IO) {
                        inputStreamReader.readLine()
                    }.also { line = it } != null) {
                    // Process line...
                    allWeatherInfo += "$allWeatherInfo$line"
                }
                withContext(Dispatchers.IO) {
                    inputStreamReader.close()
                }

            } catch (e: Exception) {
                println("Error $e")

            } finally {
                weatherData = JSONObject(allWeatherInfo)

                weatherData?.let { parseWeatherInfo(it) }
            }
        }
    }

    //https://www.tabnine.com/code/java/methods/android.graphics.drawable.Drawable/createFromStream
    private fun getDrawable(imageUrl: String): Bitmap? {
        return try {
//            val inputStream: InputStream = requireContext().assets.open(source)
//            val d = Drawable.createFromStream(inputStream, null)
//            d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
            val url = URL(imageUrl)
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())

            image
        } catch (e: IOException) {
            // prevent a crash if the resource still can't be found
            println(e)
            null
        }
    }

    private fun parseWeatherInfo(weatherData: JSONObject) {
        var currentWeatherDesc = ""
        var weatherIcon = ""
        val mainWeatherInfo = weatherData.getJSONObject("main")
        val currentWeatherTmp = mainWeatherInfo.getString("temp")

        val weatherArray = weatherData.getJSONArray("weather")
        for (i in 0 until weatherArray.length()) {
            currentWeatherDesc = weatherArray.getJSONObject(i).getString("description")
            weatherIcon = weatherArray.getJSONObject(i).getString("icon")
        }
        weatherInfoToDisplay = currentWeatherDesc + getString(R.string.new_line) + currentWeatherTmp
        weatherImage = getDrawable(getString(R.string.store_weather_image_link, weatherIcon))

        showDownloadProgressBar(false)
        showFragmentDialog()
    }

    private fun showDownloadProgressBar(show: Boolean) {
        requireActivity().runOnUiThread {
            if (show) {
                progressBar!!.visibility = View.VISIBLE
            } else {
                progressBar!!.visibility = View.INVISIBLE
            }
        }
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
        const val WEATHER_INFO_KEY = "infoKey"
        const val WEATHER_ICON_KEY = "iconKey"

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