package com.mospolytech.mospolyhelper.features.ui.utilities.university_pass

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.databinding.FragmentUniversityPassBinding
import com.mospolytech.mospolyhelper.features.services.UniversityPassService
import kotlinx.coroutines.delay
import java.util.*


class UniversityPassFragment : Fragment(R.layout.fragment_university_pass) {

    private val SERVICE_STATE = "state"
    private var currentDialog: AlertDialog? = null
    private lateinit var dataSource: SharedPreferencesDataSource


    private val viewBinding by viewBinding(FragmentUniversityPassBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!requireActivity().packageManager
                .hasSystemFeature("android.hardware.nfc.hce")
        ) {
            notifyAndQuit()
        }
        super.onViewCreated(view, savedInstanceState)


        viewBinding.switchPass.setOnCheckedChangeListener { _, isChecked ->
            setServiceState(isChecked)
        }

        dataSource = SharedPreferencesDataSource(PreferenceManager.getDefaultSharedPreferences(context))

        viewBinding.textLog.text = dataSource.get("UniversityPassLog", "")
        lifecycleScope.launchWhenResumed {
            while (true) {
                delay(5000)
                viewBinding.textLog.text = dataSource.get("UniversityPassLog", "")
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

        viewBinding.textTechInfo.text = getString(R.string.user_id, androidId)
        viewBinding.switchPass.isChecked = serviceActivated
    }

    private fun setServiceState(state: Boolean) {
        val intent = Intent(context, UniversityPassService::class.java)
        intent.putExtra(SERVICE_STATE, state)
        requireActivity().startService(intent)
    }
}