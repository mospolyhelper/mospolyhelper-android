package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemColorPickerBinding
import com.mospolytech.mospolyhelper.utils.TAG
import com.mospolytech.mospolyhelper.utils.WeakMutableSet


class LessonTagColorAdapter : RecyclerView.Adapter<LessonTagColorAdapter.ViewHolder>() {

    private var colors: List<LessonTagColors> = LessonTagColors.values().toList()
    private var checkedPosition = LessonTagColors.ColorDefault.ordinal
    private val activeViewHolders: MutableSet<ViewHolder?> = WeakMutableSet()

    var onItemChecked: (position: Int) -> Unit = { }

    override fun getItemCount() = colors.size

    fun setChecked(position: Int) {
        checkedPosition = position
        try {
            for (viewHolder in activeViewHolders) {
                viewHolder?.setChecked(viewHolder.bindingAdapterPosition == position)
            }
        } catch (e: Exception) {
            Log.e(TAG, "WeakReference exception", e)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_color_picker, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        activeViewHolders.add(holder)
        holder.bind(colors[position].colorId, checkedPosition == position) {
            onItemChecked(it)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        activeViewHolders.remove(holder)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(ItemColorPickerBinding::bind)

        private var isChecked = false

        fun bind(@ColorRes colorId: Int, isChecked: Boolean, onChecked: (position: Int) -> Unit = { }) {
            viewBinding.root.imageTintList = ColorStateList.valueOf(
                viewBinding.root.context.getColor(
                    colorId
                )
            )
            setChecked(isChecked)
            viewBinding.root.setOnClickListener {
                if (!this.isChecked) {
                    onChecked(bindingAdapterPosition)
                    //setChecked(!isChecked)
                }
            }
        }

        fun setChecked(isChecked: Boolean) {
            val oldIsChecked = this.isChecked
            this.isChecked = isChecked
            if (oldIsChecked != isChecked) {
                val colorFrom: Int
                val colorTo: Int
                val scaleFrom: Float
                val scaleTo: Float
                if (isChecked) {
                    colorFrom = 0x00FFFFFF.toInt()
                    colorTo = 0xFFFFFFFF.toInt()
                    scaleFrom = 1.0f
                    scaleTo = 1.2f
                } else {
                    colorFrom = 0xFFFFFFFF.toInt()
                    colorTo = 0x00FFFFFF.toInt()
                    scaleFrom = 1.2f
                    scaleTo = 1.0f
                }
                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
                    .apply {
                        duration = 200
                        addUpdateListener { animator ->
                            viewBinding.root.foregroundTintList =
                                ColorStateList.valueOf(animator.animatedValue as Int)
                        }
                    }

                val scaleAnimation = ValueAnimator.ofFloat(scaleFrom, scaleTo)
                    .apply {
                        duration = 200
                        addUpdateListener { animator ->
                            viewBinding.root.scaleX = animator.animatedValue as Float
                            viewBinding.root.scaleY = animator.animatedValue as Float
                        }
                    }

                AnimatorSet().apply {
                    playTogether(colorAnimation, scaleAnimation)
                    start()
                }
            }
        }
    }
}