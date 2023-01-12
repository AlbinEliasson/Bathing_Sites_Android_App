package se.miun.alel2104.dt031g.bathingsites.fragments

import android.content.DialogInterface
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import org.json.JSONObject
import se.miun.alel2104.dt031g.bathingsites.AppDataBase
import se.miun.alel2104.dt031g.bathingsites.R
import se.miun.alel2104.dt031g.bathingsites.SettingsActivity
import se.miun.alel2104.dt031g.bathingsites.bathingSiteEntity.BathingSite
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
 * @author Albin Eliasson
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
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    /**
     * Overridden function to inflate the fragment layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_bathing_site, container, false)
    }

    /**
     * Overridden function to initialize the current date to the water temperature date editText and
     * the progressbar used when fetching weather data after onCreate.
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

                        saveSiteToDatabase(bathingSiteName, bathingSiteDescription,
                            bathingSiteAddress, bathingSiteLatitude, bathingSiteLongitude,
                            bathingSiteGrade, bathingSiteWaterTmp, bathingSiteWaterTmpDate)
                    }

                } else {
                    saveSiteToDatabase(bathingSiteName, bathingSiteDescription,
                        bathingSiteAddress, bathingSiteLatitude, bathingSiteLongitude,
                        bathingSiteGrade, bathingSiteWaterTmp, bathingSiteWaterTmpDate)
                }
            }

            R.id.add_bathing_site_clear_button -> {
                clearFormInputs(listOf(bathingSiteName, bathingSiteDescription,
                    bathingSiteAddress, bathingSiteLatitude, bathingSiteLongitude,
                    bathingSiteWaterTmp), bathingSiteGrade)
            }

            R.id.add_bathing_site_weather_option -> {
                getWeatherInfo(createWeatherLink(
                    bathingSiteAddress, bathingSiteLatitude, bathingSiteLongitude))
            }

            R.id.add_bathing_site_settings_option -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Function to initialize the weather dialog fragment with the weather information.
     */
    private fun showFragmentDialog() {
        val weatherDialogFragment: DialogFragment = ShowWeatherDialogFragment()
        val args = Bundle()

        args.putString(WEATHER_INFO_KEY, weatherInfoToDisplay)
        args.putParcelable(WEATHER_ICON_KEY, weatherImage)
        weatherDialogFragment.arguments = args
        weatherDialogFragment.show(childFragmentManager, weatherDialogFragment.tag)
    }

    /**
     * Function to create the full weather link utilizing the link from settings and
     * address/longitude/latitude.
     * @param address the address users have input.
     * @param latitude the latitude the users have input.
     * @param longitude the longitude the users have input.
     */
    private fun createWeatherLink(
        address: EditText?, latitude: EditText?, longitude: EditText?): String {
        var finalWeatherLink = ""
        val weatherLinkAddressExtra = "?q="
        val weatherLinkLatitudeExtra = "?lat="
        val weatherLinkLongitudeExtra = "&lon="
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            context)
        val settingWeatherLink = preferences.getString(
            SettingsActivity.SettingsFragment.WEATHER_LINK_KEY, getString(
                R.string.store_weather_link
            ))

        if (latitude!!.text.isNotEmpty() && longitude!!.text.isNotEmpty()) {
            finalWeatherLink = settingWeatherLink + weatherLinkLatitudeExtra +
                        latitude.text + weatherLinkLongitudeExtra + longitude.text

        } else if (address!!.text.isNotEmpty()) {
            val addressSplit = address.text?.split(" ")

            if (addressSplit!!.size == 1) {
                finalWeatherLink = if (removeReplaceRegex(addressSplit[0]).isNotEmpty()) {
                    settingWeatherLink + weatherLinkAddressExtra +
                            removeReplaceRegex(addressSplit[0])
                } else {
                    ""
                }

            } else if (addressSplit.size == 2) {
                finalWeatherLink = if (removeReplaceRegex(addressSplit[1]).isNotEmpty()) {
                    settingWeatherLink + weatherLinkAddressExtra +
                            removeReplaceRegex(addressSplit[1])
                } else {
                    ""
                }
            }

        } else {
            finalWeatherLink = ""
        }
        return finalWeatherLink
    }

    /**
     * Function to remove/replace unwanted characters from the address.
     */
    private fun removeReplaceRegex(link: String): String {
        var finalLink = link
        val regexCapitalizeA = Regex("[ÁÀÂÃÄÅ]")
        val regexLowerCaseA = Regex("[áàâäãå]")
        val regexCapitalizeO = Regex("[ÓÒÔÖÕØ]")
        val regexLowerCaseO = Regex("[óòôöõø]")
        val regexRemoveNonAlphaChar = Regex("[^A-Z a-z]")

        finalLink = regexCapitalizeA.replace(finalLink, "A")
        finalLink = regexLowerCaseA.replace(finalLink, "a")
        finalLink = regexCapitalizeO.replace(finalLink, "O")
        finalLink = regexLowerCaseO.replace(finalLink, "o")
        finalLink = regexRemoveNonAlphaChar.replace(finalLink, "")

        return finalLink
    }

    /**
     * Function to access the weather info from the provided link.
     * https://stackoverflow.com/questions/62260002/send-get-request-in-kotlin-android
     * @param weatherLink the weather link.
     */
    private fun getWeatherInfo(weatherLink: String) {
        var allWeatherInfo = ""
        if (weatherLink.isEmpty()) {
            Toast.makeText(context, getString(R.string.cant_show_weather), Toast.LENGTH_LONG).show()

        } else {
            showDownloadProgressBar(true)

            applicationScope.launch {
                try { //disable the strict mode otherwise perform this operation on netWork Thread
                    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                    StrictMode.setThreadPolicy(policy)

                    // GET Request
                    val url =
                        URL(weatherLink)
                    val connection = withContext(Dispatchers.IO) {
                        url.openConnection()
                    }
                    connection.doOutput = true
                    // Get the response
                    val inputStreamReader = BufferedReader(
                        InputStreamReader(withContext(Dispatchers.IO) {
                            connection.inputStream
                        })
                    )
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
    }

    /**
     * Function to access the image from the weather image url.
     * https://www.tabnine.com/code/java/methods/android.graphics.drawable.Drawable/createFromStream
     * @param imageUrl the url to the image.
     */
    private fun getDrawable(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())

            image
        } catch (e: IOException) {
            println(e)
            null
        }
    }

    /**
     * Function to parse the information fetched from the weather link.
     * @param weatherData the json object containing the weather information.
     */
    private fun parseWeatherInfo(weatherData: JSONObject) {
        var currentWeatherDesc = ""
        var weatherIcon = ""

        if (weatherData.getString("cod") == "404") {
            showDownloadProgressBar(false)

            requireActivity().runOnUiThread {
                Toast.makeText(
                    context, getString(
                        R.string.cant_show_weather
                    ),
                    Toast.LENGTH_LONG).show()
            }

        } else {
            val mainWeatherInfo = weatherData.getJSONObject("main")
            val currentWeatherTmp = mainWeatherInfo.getString("temp") +
                    getString(R.string.degree_sign)

            val weatherArray = weatherData.getJSONArray("weather")
            for (i in 0 until weatherArray.length()) {
                currentWeatherDesc = weatherArray.getJSONObject(i).getString("description")
                weatherIcon = weatherArray.getJSONObject(i).getString("icon")
            }
            weatherInfoToDisplay =
                currentWeatherDesc + getString(R.string.new_line) + currentWeatherTmp
            weatherImage = getDrawable(getString(R.string.store_weather_image_link, weatherIcon))

            showDownloadProgressBar(false)
            showFragmentDialog()
        }
    }

    /**
     * Function to show the progressbar while fetching weather data.
     * @param show true if progressbar is to be shown.
     */
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
    private fun clearFormInputs(formList: List<EditText?>, grade: RatingBar?) {
        formList.forEach {
            if (it!!.length() != 0) {
                it.text.clear()
            }
        }
        // Reset the rating bar
        if (grade!!.rating != 0.0F) {
            grade.rating = 0.0F
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
     * Function to save the bathing-site to the database.
     * @param name the EditText for the name of the bathing site.
     * @param description the EditText for the description of the bathing site.
     * @param address the EditText for the address of the bathing site.
     * @param latitude the EditText for the latitude of the bathing site.
     * @param longitude the EditText for the longitude of the bathing site.
     * @param grade the RatingBar for the grade of the bathing site.
     * @param waterTmp the EditText for the water temperature of the bathing site.
     * @param date the EditText for the date of the water temperature of the bathing site.
     */
    private fun saveSiteToDatabase(name: EditText?, description: EditText?, address: EditText?,
                                  latitude: EditText?, longitude: EditText?, grade: RatingBar?,
                                  waterTmp: EditText?, date: EditText?) {
        applicationScope.launch {
            val dataBase = context?.let { AppDataBase.getDatabase(it) }
            val bathingSiteDao = dataBase?.BathingSiteDao()
            val latitudeDouble: Double?
            val longitudeDouble: Double?

            if (bathingSiteDao != null) {

                if (latitude!!.text.isNotEmpty() && longitude!!.text.isNotEmpty()) {
                    latitudeDouble = latitude.text.toString().toDouble()
                    longitudeDouble = longitude.text.toString().toDouble()

                    if (bathingSiteDao.exists(latitudeDouble, longitudeDouble)) {
                        createSaveAlertDialog(false, getString(
                            R.string.site_already_exists))

                    } else {
                        val newBathingSite = BathingSite(
                            name?.text.toString(), description?.text.toString(),
                            address?.text.toString(), latitudeDouble,
                            longitudeDouble, grade?.rating,
                            waterTmp?.text.toString().toDoubleOrNull(), date?.text.toString()
                        )

                        bathingSiteDao.insertAll(newBathingSite)

                        clearFormInputs(listOf(
                            name, description, address, latitude, longitude, waterTmp), grade)

                        createSaveAlertDialog(true, getString(R.string.successful_save))
                    }

                } else {
                    val newBathingSite = BathingSite(
                        name?.text.toString(), description?.text.toString(),
                        address?.text.toString(), latitude.text.toString().toDoubleOrNull(),
                        longitude?.text.toString().toDoubleOrNull(), grade?.rating,
                        waterTmp?.text.toString().toDoubleOrNull(), date?.text.toString()
                    )

                    bathingSiteDao.insertAll(newBathingSite)

                    clearFormInputs(listOf(
                        name, description, address, latitude, longitude, waterTmp), grade)

                    createSaveAlertDialog(true, getString(R.string.successful_save))
                }
            }
        }
    }

    /**
     * Function to display the save bathing-site to database success alertDialog. If successful
     * save the activity is to be finished.
     */
    private fun createSaveAlertDialog(successful: Boolean, message: String) {
        val alertBuilder = AlertDialog.Builder(requireContext())
        alertBuilder.setTitle(getString(R.string.dialog_title))
        alertBuilder.setMessage(message)

        if (successful) {
            alertBuilder.setPositiveButton(R.string.dialog_buttonText) { _: DialogInterface?, _: Int ->
                if (alertDialog != null) {
                    alertDialog!!.dismiss()
                }
                activity?.finish()
            }
        } else {
            alertBuilder.setPositiveButton(R.string.dialog_buttonText, null)
        }

        requireActivity().runOnUiThread {
            alertDialog = alertBuilder.create()
            alertDialog!!.setCancelable(false)
            alertDialog!!.show()
        }
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