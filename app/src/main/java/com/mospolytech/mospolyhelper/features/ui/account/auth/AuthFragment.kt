package com.mospolytech.mospolyhelper.features.ui.account.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.android.synthetic.main.fragment_account_auth.*
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthFragment : Fragment() {

    private lateinit var loginText: TextView
    private lateinit var passwordText: TextView
    private lateinit var saveLoginCheckBox: CheckBox
    private lateinit var savePasswordCheckBox: CheckBox
    private lateinit var logInButton: Button
    private lateinit var logOutButton: Button

    private val viewModel by viewModel<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginText = view.findViewById(R.id.text_login)
        passwordText = view.findViewById(R.id.text_password)
        saveLoginCheckBox = view.findViewById(R.id.checkbox_save_login)
        savePasswordCheckBox = view.findViewById(R.id.checkbox_save_password)
        logInButton = view.findViewById(R.id.btn_login)
        logOutButton = view.findViewById(R.id.btn_logout)

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
                        progress_auth.hide()
                        logInButton.show()
                        Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                    }.onFailure {
                        progress_auth.hide()
                        logInButton.show()
                        logOutButton.hide()
                        Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    }.onLoading {
                        progress_auth.show()
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