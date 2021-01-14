package com.mospolytech.mospolyhelper.features.ui.account.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mospolytech.mospolyhelper.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class StudentsFragment : Fragment() {

    private val viewModel  by viewModel<StudentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_students, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.textView)
//        val api = StudentsHerokuClient()
//        GlobalScope.async(Dispatchers.Main) {
//            textView.text = api.getStudents("181-721", 1)
//                .replace(",", ",\n    ")
//                .replace("{", "{\n    ")
//                .replace("}", "\n}")
//                .replace(":", " : ")
//                .replace("},\n    ", "},\n")
//        }


    }
}