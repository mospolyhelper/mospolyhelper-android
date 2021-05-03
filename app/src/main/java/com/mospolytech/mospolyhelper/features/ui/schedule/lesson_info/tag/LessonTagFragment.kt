package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.BottomSheetLessonTagBinding
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LessonTagFragment : BottomSheetDialogFragment() {

    private val viewModel  by sharedViewModel<LessonTagViewModel>()
    private val viewBinding by viewBinding(BottomSheetLessonTagBinding::bind)
    private val args: LessonTagFragmentArgs by navArgs()

    override fun getTheme(): Int  = R.style.CustomBottomSheetDialogTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.tag.value = args.tag
        viewModel.lesson.value = args.lesson
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_lesson_tag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.recyclerviewColors.adapter = LessonTagColorAdapter().apply {
            onItemChecked = {
                viewModel.checkedColor.value = LessonTagColors.values()[it]
            }
        }
        viewBinding.textviewTitle.text = "Create tag"

        bindViewModel()
    }

    private fun bindViewModel() {
        lifecycleScope.launchWhenResumed {
            viewModel.checkedColor.collect {
                with(viewBinding.recyclerviewColors.adapter as LessonTagColorAdapter) {
                    setChecked(it.ordinal)
                }
            }
        }
    }
}