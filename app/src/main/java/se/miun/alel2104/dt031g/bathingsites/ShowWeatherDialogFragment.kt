package se.miun.alel2104.dt031g.bathingsites

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment


class ShowWeatherDialogFragment : DialogFragment() {

    // https://developer.android.com/guide/fragments/dialogs
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val weatherData = requireArguments().getString(AddBathingSiteFragment.WEATHER_INFO_KEY)
        val weatherIconBitmap: Bitmap? =
            requireArguments().getParcelable(AddBathingSiteFragment.WEATHER_ICON_KEY)
        val weatherIconDrawable = BitmapDrawable(requireContext().resources, weatherIconBitmap)

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.weather_info_dialog_title))
            .setIcon(weatherIconDrawable)
            .setMessage(weatherData)
            .setPositiveButton(getString(R.string.dialog_buttonText)) { _, _ -> }
            .create()
    }

    companion object {
        const val TAG = "ShowWeatherDialog"
    }
}