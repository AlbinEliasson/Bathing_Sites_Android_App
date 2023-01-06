package se.miun.alel2104.dt031g.bathingsites

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ShowWeatherDialogFragment : DialogFragment() {

    // https://developer.android.com/guide/fragments/dialogs
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage("The weather is ok i guess")
            .setPositiveButton(getString(R.string.dialog_buttonText)) { _,_ -> }
            .create()

    companion object {
        const val TAG = "ShowWeatherDialog"
    }
}