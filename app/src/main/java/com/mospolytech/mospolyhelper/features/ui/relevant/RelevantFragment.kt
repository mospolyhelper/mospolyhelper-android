package com.mospolytech.mospolyhelper.features.ui.relevant

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentRelevantBinding
import com.mospolytech.mospolyhelper.features.ui.schedule.LessonAdapter
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class RelevantFragment : Fragment(R.layout.fragment_relevant) {

    private val viewModel  by viewModel<RelevantViewModel>()
    private val viewBinding by viewBinding(FragmentRelevantBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLessonList()
    }

    private fun setLessonList() {
        var listAdapter: LessonAdapter? = null

        viewBinding.includePageSchedule.recyclerviewLessons.layoutManager = LinearLayoutManager(context)
            .apply { recycleChildrenOnDetach = true }
        viewBinding.includePageSchedule.recyclerviewLessons.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN &&
                    rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING
                ) {
                    rv.stopScroll()
                }
                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
        })
        viewBinding.includePageSchedule.recyclerviewLessons.itemAnimator = null


        lifecycleScope.launchWhenResumed {
            viewModel.getSchedule().collect {
            }
        }
    }
}