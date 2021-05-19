package com.mospolytech.mospolyhelper.features.ui.account.auth

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountAuthBinding
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthFragment : Fragment(R.layout.fragment_account_auth) {

    private lateinit var loginText: TextView
    private lateinit var passwordText: TextView
    private lateinit var saveLoginCheckBox: CheckBox
    private lateinit var savePasswordCheckBox: CheckBox
    private lateinit var logInButton: Button
    private lateinit var logOutButton: Button
    private lateinit var progressAuth: ProgressBar

    private val viewBinding by viewBinding(FragmentAccountAuthBinding::bind)
    private val viewModel by viewModel<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginText = viewBinding.textLogin
        passwordText = viewBinding.textPassword
        saveLoginCheckBox = viewBinding.checkboxSaveLogin
        savePasswordCheckBox = viewBinding.checkboxSavePassword
        logInButton = viewBinding.btnLogin
        logOutButton = viewBinding.btnLogout
        progressAuth = viewBinding.progressAuth

        loginText.text = viewModel.login.value
        passwordText.text = viewModel.password.value
        saveLoginCheckBox.isChecked = viewModel.saveLogin.value
        savePasswordCheckBox.isChecked = viewModel.savePassword.value

        loginText.addTextChangedListener {
            viewModel.login.value = it.toString()
        }
        passwordText.addTextChangedListener {
            viewModel.password.value = it.toString()
        }
        saveLoginCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveLogin.value = isChecked
        }
        savePasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.savePassword.value = isChecked
        }

        logInButton.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                viewModel.logIn().collect {
                    it.onSuccess {
                        logOutButton.show()
                        progressAuth.hide()
                        logInButton.show()
                        Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                    }.onFailure {
                        progressAuth.hide()
                        logInButton.show()
                        logOutButton.hide()
                        Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    }.onLoading {
                        progressAuth.show()
                        logInButton.hide()
                        logOutButton.hide()
                        Toast.makeText(context, "Loading", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        logOutButton.setOnClickListener {
            viewModel.logOut()
        }
    }


}