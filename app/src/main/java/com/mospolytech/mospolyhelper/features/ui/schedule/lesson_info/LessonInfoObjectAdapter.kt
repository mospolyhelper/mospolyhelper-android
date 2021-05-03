package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemLessonInfoObjectBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.LessonInfoObject


class LessonInfoObjectAdapter(
    private val lessonInfoObjects: List<LessonInfoObject>,
    private val onItemClick: (group: String) -> Unit = { }
) : RecyclerView.Adapter<LessonInfoObjectAdapter.LessonInfoObjectViewHolder>() {

    override fun getItemCount() = lessonInfoObjects.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonInfoObjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lesson_info_object, parent, false)
        return LessonInfoObjectViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: LessonInfoObjectViewHolder, position: Int) {
        holder.bind(lessonInfoObjects[position])
    }


    class LessonInfoObjectViewHolder(itemView: View, private val onItemClick: (group: String) -> Unit = { }) : RecyclerView.ViewHolder(itemView) {
        private val viewBinding by viewBinding(ItemLessonInfoObjectBinding::bind)

        fun bind(lessonInfoObject: LessonInfoObject) {
            with(viewBinding) {
                viewBinding.root.setOnClickListener {
                    onItemClick(lessonInfoObject.title)
                }
                textviewObjectName.text = lessonInfoObject.title
                textviewObjectDescription.text = lessonInfoObject.description
                imageviewAvatar.setImageResource(lessonInfoObject.avatar)
            }
        }
    }
}