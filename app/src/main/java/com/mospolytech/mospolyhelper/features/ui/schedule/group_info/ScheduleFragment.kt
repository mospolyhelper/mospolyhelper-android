package com.mospolytech.mospolyhelper.features.ui.schedule.group_info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.data.schedule.api.ScheduleClient
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleTeacherRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteTeacherDataSource
import com.mospolytech.mospolyhelper.data.schedule.repository.ScheduleRepositoryImpl
import com.mospolytech.mospolyhelper.databinding.FragmentLessonsBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentSchedule
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleAdapter
import io.ktor.client.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

class ScheduleFragment : Fragment(R.layout.fragment_lessons) {
    private val viewBinding by viewBinding(FragmentLessonsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        with(viewBinding) {
//            runBlocking {
//                ScheduleRepositoryImpl(
//                    ScheduleLocalDataSource(),
//                    ScheduleRemoteDataSource(ScheduleClient(HttpClient()), ScheduleRemoteConverter()),
//                    ScheduleRemoteTeacherDataSource(ScheduleClient(HttpClient()), ScheduleTeacherRemoteConverter())
//                ).getSchedule(StudentSchedule("181-721", "181-721"), false).collect {
//                    viewpagerSchedule.adapter = ScheduleAdapter(
//                        it,
//                        emptyList(),
//                        emptyMap(),
//                        false,
//                        true,
//                        false,
//                        false,
//                        false,
//                        true
//                    )
//                }
//            }
//
//        }
    }
}