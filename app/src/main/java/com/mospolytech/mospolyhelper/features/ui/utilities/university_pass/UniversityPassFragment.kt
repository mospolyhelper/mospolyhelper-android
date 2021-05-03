package com.mospolytech.mospolyhelper.features.ui.utilities.university_pass

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.features.services.UniversityPassService
import kotlinx.coroutines.delay
import java.util.*


class UniversityPassFragment : Fragment() {

    private val SERVICE_STATE = "state"
    private var currentDialog: AlertDialog? = null
    private lateinit var passSwitch: SwitchCompat
    private lateinit var techInfoTextView: TextView
    private lateinit var logTextView: TextView
    private lateinit var dataSource: SharedPreferencesDataSource


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_university_pass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!requireActivity().packageManager
                .hasSystemFeature("android.hardware.nfc.hce")
        ) {
            notifyAndQuit()
        }
        super.onViewCreated(view, savedInstanceState)

        passSwitch = view.findViewById(R.id.switch_pass)
        techInfoTextView = view.findViewById(R.id.text_tech_info)
        logTextView = view.findViewById(R.id.text_log)

        passSwitch.setOnCheckedChangeListener { _, isChecked ->
            setServiceState(isChecked)
        }

        dataSource = SharedPreferencesDataSource(PreferenceManager.getDefaultSharedPreferences(context))

        logTextView.text = dataSource.get("UniversityPassLog", "")
        lifecycleScope.launchWhenResumed {
            while (true) {
                delay(5000)
                logTextView.text = dataSource.get("UniversityPassLog", "")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activateDeactivate()
    }

    override fun onDestroy() {
        if (currentDialog != null && currentDialog!!.isShowing) {
            currentDialog!!.dismiss()
        }
        super.onDestroy()
    }

    private fun notifyAndQuit() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("string.hce_not_supported_title")
        builder.setMessage("string.hce_not_supported_message")
        builder.setPositiveButton("string.hce_not_supported_ok_button") { _, _ ->
            requireActivity().finish()
        }
        builder.setCancelable(false)
        currentDialog = builder.show()
    }

    private fun activateDeactivate() {
        val androidId =
            Settings.Secure.getString(requireActivity().contentResolver, "android_id").toUpperCase(Locale.US)
        val serviceActivated = requireActivity().getSharedPreferences(
            requireActivity().packageName + "_preferences", 0
        ).getBoolean(SERVICE_STATE, true)

        techInfoTextView.text = getString(R.string.user_id, androidId)
        passSwitch.isChecked = serviceActivated
    }

    private fun setServiceState(state: Boolean) {
        val intent = Intent(context, UniversityPassService::class.java)
        intent.putExtra(SERVICE_STATE, state)
        requireActivity().startService(intent)
    }
}