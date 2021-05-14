package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.BottomSheetLessonTagBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagMessages
import com.mospolytech.mospolyhelper.utils.RoundedBackgroundSpan
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LessonTagFragment : BottomSheetDialogFragment() {

    private val viewModel  by sharedViewModel<LessonTagViewModel>()
    private val viewBinding by viewBinding(BottomSheetLessonTagBinding::bind)
    private val args: LessonTagFragmentArgs by navArgs()

    override fun getTheme(): Int  = R.style.CustomBottomSheetDialogTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.lesson.value = args.lesson
        viewModel.dayOfWeek.value = args.dayOfWeek
        viewModel.order.value = args.order
        viewModel.tag.value = args.tag
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
        viewBinding.recyclerviewTags.adapter = LessonTagAdapter().apply {
            onTagCheckedListener = { tag: LessonTag, lesson: LessonTagKey, isChecked: Boolean ->
                lifecycleScope.launchWhenResumed {
                    viewModel.lessonTagCheckedChanged(tag, lesson, isChecked)
                }
            }
            onTagEditListener = {
                // TODO: set edit tag views
            }
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
                viewModel.checkedColor.value = LessonTagColors.values()[it]
            }
        }
        viewBinding.edittextTagTitle.addTextChangedListener {
            viewModel.title.value = it?.toString() ?: ""
        }
        viewBinding.buttonApply.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                viewModel.createTag()
            }
        }
        viewBinding.textviewTitle.text = "Create tag"
    }

    private fun switchViews(toTagList: Boolean) {
        if (toTagList) {
            viewBinding.linearlayoutTags.visibility = View.VISIBLE
            viewBinding.constraintlayoutEditTag.visibility = View.GONE
        } else {
            viewBinding.linearlayoutTags.visibility = View.GONE
            viewBinding.constraintlayoutEditTag.visibility = View.VISIBLE
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

    private var prevColor = LessonTagColors.ColorDefault
    private var currentTextColor = 0xffaaaaaa.toInt()
    private var currentColor = 0xffaaaaaa.toInt()

    private fun bindViewModel() {
        lifecycleScope.launchWhenResumed {
            viewModel.title.collect {
                viewBinding.textviewTagPreview.text = getFeaturesString(if (it.isEmpty()) getString(R.string.lesson_tag_example) else it, currentColor, currentTextColor)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.checkedColor.collect {
                with(viewBinding.recyclerviewColors.adapter as LessonTagColorAdapter) {
                    setChecked(it.ordinal)
                }
//                val colorAnimation1 = ValueAnimator.ofObject(
//                    ArgbEvaluator(),
//                    viewBinding.edittextTagTitle.hintTextColors?.defaultColor ?: 0xffaaaaaa.toInt(),
//                    requireContext().getColor(it.textColorId)
//                )
//                colorAnimation1.duration = 200 // milliseconds
//                colorAnimation1.addUpdateListener { animator ->
//                    viewBinding.edittextTagTitle.setHintTextColor(animator.animatedValue as Int)
//                }

                val colorAnimation2 = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    requireContext().getColor(prevColor.textColorId),
                    requireContext().getColor(it.textColorId)
                )
                colorAnimation2.duration = 200 // milliseconds
                colorAnimation2.addUpdateListener { animator ->
                    //viewBinding.edittextTagTitle.setTextColor(animator.animatedValue as Int)
                    currentTextColor = animator.animatedValue as Int
                    viewBinding.textviewTagPreview.text = getFeaturesString(
                        if (viewModel.title.value.isEmpty())
                            getString(R.string.lesson_tag_example)
                        else
                            viewModel.title.value,
                        currentColor,
                        currentTextColor
                    )
                }

                val colorAnimation3 = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    requireContext().getColor(prevColor.colorId),
                    requireContext().getColor(it.colorId)
                )
                colorAnimation3.duration = 200 // milliseconds
                colorAnimation3.addUpdateListener { animator ->
                    currentColor = animator.animatedValue as Int
                    viewBinding.textviewTagPreview.text = getFeaturesString(
                        if (viewModel.title.value.isEmpty())
                            getString(R.string.lesson_tag_example)
                        else
                            viewModel.title.value,
                        currentColor,
                        currentTextColor
                    )
                    //viewBinding.edittextTagTitle.backgroundTintList =  ColorStateList.valueOf(animator.animatedValue as Int)
                }

                AnimatorSet().apply {
                    playTogether(colorAnimation2, colorAnimation3)
                    start()
                }
                prevColor = it

//                viewBinding.edittextTagTitle.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(it.colorId))
//                viewBinding.edittextTagTitle.setTextColor(requireContext().getColor(it.textColorId))
//                viewBinding.edittextTagTitle.setHintTextColor(requireContext().getColor(it.textColorId))
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.tags.collect {
                setTagList(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.message.collect {
                if (it != null) {
                    when (it.text) {
                        LessonTagMessages.SuccessfulCreation -> {
                            switchViews(true)
                        }
                        LessonTagMessages.AlreadyExist -> viewBinding.textviewMessage.text = it.text.toString()
                        LessonTagMessages.EmptyTitle -> viewBinding.textviewMessage.text = it.text.toString()
                        else -> viewBinding.textviewMessage.text = it.text.toString()
                    }
                    viewBinding.textviewMessage.visibility = View.VISIBLE
                } else {
                    viewBinding.textviewMessage.text = ""
                    viewBinding.textviewMessage.visibility = View.GONE
                }
            }
        }
    }
}