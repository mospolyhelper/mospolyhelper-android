package com.mospolytech.mospolyhelper.features.ui.account.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.data.account.applications.api.ApplicationsHerokuClient
import com.mospolytech.mospolyhelper.data.account.applications.remote.ApplicationsRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.marks.api.MarksHerokuClient
import com.mospolytech.mospolyhelper.data.account.marks.remote.MarksRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.payments.api.PaymentsHerokuClient
import com.mospolytech.mospolyhelper.data.account.payments.remote.PaymentsRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.students.api.StudentsHerokuClient
import com.mospolytech.mospolyhelper.data.account.students.remote.StudentsRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.teachers.api.TeachersHerokuClient
import com.mospolytech.mospolyhelper.data.account.teachers.remote.TeachersRemoteDataSource
import com.mospolytech.mospolyhelper.utils.onFailure
import com.mospolytech.mospolyhelper.utils.onLoading
import com.mospolytech.mospolyhelper.utils.onSuccess
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class InfoFragment : Fragment() {

    private lateinit var infoText: TextView

    private val viewModel by viewModel<InfoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infoText = view.findViewById(R.id.textView)

        lifecycleScope.launchWhenResumed {
            viewModel.info.collect { result ->
                result.onSuccess {
                    infoText.text = it.toString()
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                }.onLoading {
                    Toast.makeText(context, "Loading", Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.async {
            viewModel.getInfo()
        }
    }
}