package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag

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
        holder.bind(tags[position], lesson)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(ItemLessonTagChoosingBinding::bind)

        fun bind(tag: LessonTag, lesson: LessonTagKey?) {
            viewBinding.checkboxTag.text = tag.title
            viewBinding.checkboxTag.isChecked = lesson in tag.lessons
        }
    }
}