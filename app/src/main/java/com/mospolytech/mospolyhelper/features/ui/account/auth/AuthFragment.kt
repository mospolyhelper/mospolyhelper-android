package com.mospolytech.mospolyhelper.features.ui.account.auth

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountAuthBinding
import com.mospolytech.mospolyhelper.utils.*
import io.ktor.client.features.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.UnknownHostException

class AuthFragment : Fragment(R.layout.fragment_account_auth) {

    private lateinit var loginText: TextView
    private lateinit var passwordText: TextView
    private lateinit var logInButton: Button
    private lateinit var logOutButton: Button
    private lateinit var progressAuth: ProgressBar
    private lateinit var authLayout: FrameLayout
    private lateinit var loginLayout: LinearLayout
    private lateinit var fioStudent: TextView
    private lateinit var avatarUser: ImageView

    private val viewBinding by viewBinding(FragmentAccountAuthBinding::bind)
    private val viewModel by viewModel<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginText = viewBinding.textLogin
        passwordText = viewBinding.textPassword
        logInButton = viewBinding.btnLogin
        logOutButton = viewBinding.btnLogout
        progressAuth = viewBinding.progressAuth
        authLayout = viewBinding.authLayout
        loginLayout = viewBinding.loginLayout
        fioStudent = viewBinding.fioStudent
        avatarUser = viewBinding.avatarUser

        createLayout()

        logInButton.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                viewModel.logIn(loginText.text.toString(), passwordText.text.toString()).collect {
                    it.onSuccess {
                        progressAuth.hide()
                        logInButton.show()
                        createLayout()
                    }.onFailure { error ->
                        progressAuth.hide()
                        logInButton.show()
                        when (error) {
                            is ClientRequestException -> {
                                when (error.response.status.value) {
                                    401 -> Toast.makeText(context, R.string.not_authorized, Toast.LENGTH_LONG).show()
                                    else -> Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show()
                                }
                            }
                            is UnknownHostException -> {
                                Toast.makeText(context, R.string.check_connection, Toast.LENGTH_LONG).show()
                            }
                            else -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                        }

                    }.onLoading {
                        progressAuth.show()
                        logInButton.hide()
                    }
                }
            }
        }

        logOutButton.setOnClickListener {
            viewModel.logOut()
            createLayout()
        }

    }

    private fun createLayout() {
        viewModel.getName()?.let {
            authLayout.show()
            loginLayout.hide()
            fioStudent.text = it
            Glide.with(this).load(viewModel.getAvatar()).into(avatarUser)
        } ?: let {
            authLayout.hide()
            loginLayout.show()
        }
    }

    override fun onDestroyView() {
        Glide.with(this).clear(avatarUser)
        super.onDestroyView()
    }

}