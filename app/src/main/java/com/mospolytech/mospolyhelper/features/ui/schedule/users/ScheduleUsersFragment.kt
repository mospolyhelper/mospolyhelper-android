package com.mospolytech.mospolyhelper.features.ui.schedule.users

import android.os.Bundle
import android.view.*
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.BottomSheetScheduleUsersBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.TeacherScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.teacher.Teacher
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleIntent
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.features.utils.getColorStateList
import com.mospolytech.mospolyhelper.utils.safe
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScheduleUsersFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModel<ScheduleSourcesViewModel>()
    private val viewBinding by viewBinding(BottomSheetScheduleUsersBinding::bind)

    override fun getTheme(): Int  = R.style.CustomBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_schedule_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButton()
        bindViewModel()
    }

    private fun setButton() {
        viewBinding.buttonAddUser.setOnClickListener {
            findNavController().safe {
                navigate(ScheduleUsersFragmentDirections.actionScheduleUsersFragmentToScheduleIdsFragment())
            }
        }
    }

    private fun createChip(source: ScheduleSource): Chip {
        val chip = layoutInflater.inflate(
            R.layout.chip_schedule_user,
            viewBinding.chipgroupUsers,
            false
        ) as Chip
        chip.tag = source
        chip.text = if (source is TeacherScheduleSource) Teacher(source.title).getShortName() else source.title
        chip.chipIconTint = getColorStateList(R.color.chip_color_text)
        chip.setChipIconResource(
            if (source is StudentScheduleSource)
                R.drawable.ic_fluent_people_20_selector
            else
                R.drawable.ic_fluent_hat_graduation_20_selector
        )
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectScheduleSource(source)
            }
        }
        chip.setOnCreateContextMenuListener { menu, _, _ ->
            menu.add(getString(R.string.remove)).setOnMenuItemClickListener {
                if (it.title == getString(R.string.remove)) {
                    viewModel.removeUser(source)
                    return@setOnMenuItemClickListener true
                }
                false
            }
        }
        return chip
    }


    private fun setSavedUsers(sources: List<ScheduleSource>) {
        var checkedChip: Chip? = null
        viewBinding.chipgroupUsers.removeAllViews()
        for (user in sources) {
            val chip = createChip(user)
            viewBinding.chipgroupUsers.addView(chip)
            if (viewModel.selectedScheduleSource == user) {
                chip.id = View.generateViewId()
                viewBinding.chipgroupUsers.check(chip.id)
                checkedChip = chip
            }
        }
        checkedChip?.let {
            viewBinding.chipgroupUsers.post {
                viewBinding.root.smoothScrollTo(it.left - it.paddingLeft, it.top)
            }
        }
        viewModel.selectedScheduleSource.value?.let {
            updateCheckedUser(it)
        }
    }

    private fun updateCheckedUser(source: ScheduleSource) {
        val checkedChip = viewBinding.chipgroupUsers.children.firstOrNull {
            (it.tag as? ScheduleSource)?.idGlobal == source.idGlobal
        }
        checkedChip?.let {
            it.id = View.generateViewId()
            viewBinding.chipgroupUsers.check(it.id)
        }

    }

    private fun bindViewModel() {
        lifecycleScope.launchWhenResumed {
            viewModel.favoriteScheduleSources.collect {
                setSavedUsers(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.selectedScheduleSource.collect {
                it?.let {
                    updateCheckedUser(it)
                }
            }
        }
    }
}