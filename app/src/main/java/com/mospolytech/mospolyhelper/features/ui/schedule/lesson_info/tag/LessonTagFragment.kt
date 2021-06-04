package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.BottomSheetLessonTagBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagException
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagMessages
import com.mospolytech.mospolyhelper.features.utils.RoundedBackgroundSpan
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LessonTagFragment : BottomSheetDialogFragment() {

    private val viewModel  by sharedViewModel<LessonTagViewModel>()
    private val viewBinding by viewBinding(BottomSheetLessonTagBinding::bind)
    private val args: LessonTagFragmentArgs by navArgs()

    override fun getTheme(): Int  = R.style.CustomBottomSheetDialogTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setArgs(args.lesson, args.dayOfWeek, args.order)
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

        setNewTagButton()
        setTagList(emptyList())
        setTagCreationViews()

        bindViewModel()
    }

    private fun setNewTagButton() {
        viewBinding.buttonNewTag.setOnClickListener {
            switchViews(false)
        }
    }

    private fun setTagList(tags: List<LessonTag>) {
        viewBinding.textviewTagListMessage.text = if (tags.isEmpty())
            getString(R.string.schedule_lesson_tags_empty)
        else
            getString(R.string.schedule_lesson_tags_select)

        viewBinding.recyclerviewTags.adapter = LessonTagAdapter().apply {
            onTagCheckedListener = { tag: LessonTag, lesson: LessonTagKey, isChecked: Boolean ->
                lifecycleScope.launchWhenResumed {
                    viewModel.lessonTagCheckedChanged(tag, lesson, isChecked)
                }
            }
            onTagEditListener = { }
            onTagRemoveListener = {
                lifecycleScope.launchWhenResumed {
                    viewModel.removeTag(it.title)
                }
            }

            val lesson = viewModel.lesson.value
            val dayOfWeek = viewModel.dayOfWeek.value
            val order = viewModel.order.value
            if (lesson != null && dayOfWeek != null && order != null) {
                submitData(tags, LessonTagKey.fromLesson(
                    lesson,
                    dayOfWeek,
                    order
                ))
            }
        }
    }

    private fun setTagCreationViews() {
        viewBinding.recyclerviewColors.adapter = LessonTagColorAdapter().apply {
            onItemChecked = {
                viewModel.onColorChecked(LessonTagColors.values()[it])
            }
        }
        viewBinding.edittextTagTitle.addTextChangedListener {
            viewModel.onTitleChanged(it?.toString() ?: "")
        }
        viewBinding.buttonApply.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                viewModel.createTag()
            }
        }
        viewBinding.buttonCancel.setOnClickListener {
            switchViews(true)
        }
        viewBinding.textviewTitle.text = getString(R.string.create_tag)
    }

    private fun switchViews(toTagList: Boolean) {
        if (toTagList) {
            viewBinding.linearlayoutTags.show()
            viewBinding.constraintlayoutEditTag.gone()
        } else {
            viewBinding.linearlayoutTags.gone()
            viewBinding.constraintlayoutEditTag.show()
        }
    }

    private fun getFeaturesString(title: String, color: Int, textColor: Int): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        builder.append(
            "\u00A0",
            RoundedBackgroundSpan(
                backgroundColor = color,
                textColor = textColor,
                text = title
            ),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return builder
    }

    private fun bindViewModel() {
        lifecycleScope.launchWhenResumed {
            viewModel.title.collect {
                viewBinding.textviewTagPreview.text =
                    getFeaturesString(if (it.isEmpty()) getString(R.string.schedule_lesson_tag_example) else it, currentColor, currentTextColor)
                if (viewBinding.edittextTagTitle.text.toString() != it) {
                    viewBinding.edittextTagTitle.setText(it)
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.checkedColor.collect {
                with(viewBinding.recyclerviewColors.adapter as LessonTagColorAdapter) {
                    setChecked(it.ordinal)
                }
                animateCheckedColor(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.errorMessages.collect {
                if (it is LessonTagException) {
                    when (it.resultMessage) {
                        LessonTagMessages.AlreadyExist ->
                            viewBinding.textviewMessage.text = getString(R.string.tag_with_title_exist)
                        LessonTagMessages.EmptyTitle ->
                            viewBinding.textviewMessage.text = getString(R.string.tag_with_empty_title)
                        else -> viewBinding.textviewMessage.text =
                            it.resultMessage.toString()
                    }
                    viewBinding.textviewMessage.show()
                } else {
                    viewBinding.textviewMessage.text = ""
                    viewBinding.textviewMessage.gone()
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.tags.collect {
                it.onSuccess {
                    switchViews(true)
                    setTagList(it)
                    viewModel.resetTagData()
                }
            }
        }
    }

    private var prevColor = LessonTagColors.ColorDefault
    private var currentTextColor = 0xffaaaaaa.toInt()
    private var currentColor = 0xffaaaaaa.toInt()
    private var currentAnimation: AnimatorSet? = null

    private fun animateCheckedColor(color: LessonTagColors) {
        val colorAnimation2 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            requireContext().getColor(prevColor.textColorId),
            requireContext().getColor(color.textColorId)
        )
        colorAnimation2.duration = 200 // milliseconds
        colorAnimation2.addUpdateListener { animator ->
            currentTextColor = animator.animatedValue as Int
            viewBinding.textviewTagPreview.text = getFeaturesString(
                if (viewModel.title.value.isEmpty())
                    getString(R.string.schedule_lesson_tag_example)
                else
                    viewModel.title.value,
                currentColor,
                currentTextColor
            )
        }

        val colorAnimation3 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            requireContext().getColor(prevColor.colorId),
            requireContext().getColor(color.colorId)
        )
        colorAnimation3.duration = 200 // milliseconds
        colorAnimation3.addUpdateListener { animator ->
            currentColor = animator.animatedValue as Int
            viewBinding.textviewTagPreview.text = getFeaturesString(
                if (viewModel.title.value.isEmpty())
                    getString(R.string.schedule_lesson_tag_example)
                else
                    viewModel.title.value,
                currentColor,
                currentTextColor
            )
        }

        currentAnimation = AnimatorSet().apply {
            playTogether(colorAnimation2, colorAnimation3)
            start()
        }
        prevColor = color
    }

    override fun onStop() {
        currentAnimation?.apply {
            removeAllListeners()
            cancel()
        }
        super.onStop()
    }
}