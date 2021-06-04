package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemLessonTagChoosingBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey

class LessonTagAdapter : RecyclerView.Adapter<LessonTagAdapter.ViewHolder>() {

    private var tags: List<LessonTag> = emptyList()
    private var lesson: LessonTagKey? = null

    var onTagCheckedListener: (tag: LessonTag, lesson: LessonTagKey, isChecked: Boolean) -> Unit = { _, _, _ -> }
    var onTagEditListener: (tag: LessonTag) -> Unit = { }
    var onTagRemoveListener: (tag: LessonTag) -> Unit = { }

    fun submitData(tags: List<LessonTag>, lesson: LessonTagKey) {
        this.tags = tags
        this.lesson = lesson
    }

    override fun getItemCount() = tags.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_lesson_tag_choosing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            bind(tags[position], lesson!!)
            this.onTagCheckedListener = this@LessonTagAdapter.onTagCheckedListener
            this.onTagEditListener = this@LessonTagAdapter.onTagEditListener
            this.onTagRemoveListener = this@LessonTagAdapter.onTagRemoveListener
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(ItemLessonTagChoosingBinding::bind)

        var onTagCheckedListener: (tag: LessonTag, lesson: LessonTagKey, isChecked: Boolean) -> Unit = { _, _, _ -> }
        var onTagEditListener: (tag: LessonTag) -> Unit = { }
        var onTagRemoveListener: (tag: LessonTag) -> Unit = { }

        fun bind(tag: LessonTag, lesson: LessonTagKey) {
            viewBinding.checkboxTag.text = tag.title
            viewBinding.root.backgroundTintList = ColorStateList.valueOf(itemView.context.getColor(tag.getColor().colorId))
            viewBinding.checkboxTag.setTextColor(itemView.context.getColor(tag.getColor().textColorId))
            viewBinding.checkboxTag.buttonTintList = ColorStateList.valueOf(itemView.context.getColor(tag.getColor().textColorId))
            viewBinding.root.setOnClickListener {
                onTagCheckedListener(tag, lesson, !viewBinding.checkboxTag.isChecked)
                viewBinding.checkboxTag.isChecked = !viewBinding.checkboxTag.isChecked
            }
            viewBinding.root.setOnCreateContextMenuListener { menu, _, _ ->
//                menu.add("Edit").setOnMenuItemClickListener {
//                    onTagEditListener(tag)
//                    true
//                }
                menu.add(itemView.context.getString(R.string.remove)).setOnMenuItemClickListener {
                    onTagRemoveListener(tag)
                    true
                }
            }
            viewBinding.checkboxTag.isChecked = lesson in tag.lessons
        }
    }
}