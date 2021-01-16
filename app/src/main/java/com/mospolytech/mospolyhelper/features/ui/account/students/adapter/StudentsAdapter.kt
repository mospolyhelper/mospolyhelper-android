package com.mospolytech.mospolyhelper.features.ui.account.students.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.features.ui.common.PagingAdapter
import com.mospolytech.mospolyhelper.utils.inflate
import kotlinx.android.synthetic.main.item_error.view.*
import java.util.*

class StudentsAdapter(diffCallback: DiffUtil.ItemCallback<Student>
)
    : PagingDataAdapter<Student, RecyclerView.ViewHolder>(diffCallback), PagingAdapter {

    companion object {
        var retryClickListener: () -> Unit = {}
    }

    private lateinit var context: Context
    private val loadingViewType = 123
    private val errorViewType = 321
    private var isLoading = false
    private var isError = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            loadingViewType -> LoadingViewHolder(parent.inflate(R.layout.item_loading))
            errorViewType -> ErrorViewHolder(parent.inflate(R.layout.item_error))
            else -> ViewHolderStudents(parent.inflate(R.layout.item_student))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            loadingViewType -> { }
            errorViewType -> (holder as ErrorViewHolder).bind()
            else -> getItem(position)?.let { (holder as ViewHolderStudents).name.text = it.name }
        }
    }

    override fun getItemCount() = super.getItemCount() + (if (isLoading) 1 else 0) + (if (isError) 1 else 0)

    override fun itemCount() = itemCount

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1) {
            if (isLoading) {
                return loadingViewType
            }
            if (isError) {
                return errorViewType
            }
        }
        return super.getItemViewType(position)
    }

    override fun addLoading() {
        if (isLoading) {
            return
        }
        isLoading = true
        notifyItemInserted(itemCount)
    }

    override fun removeLoading() {
        if (!isLoading) {
            return
        }
        isLoading = false
        notifyItemRemoved(itemCount)
    }

    override fun addError() {
        if (isError) {
            return
        }
        isError = true
        notifyItemInserted(itemCount)
    }

    override fun removeError() {
        if (!isError) {
            return
        }
        isError = false
        notifyItemRemoved(itemCount)
    }

    inner class LoadingViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    inner class ErrorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.buttonRetry.setOnClickListener { retryClickListener.invoke() }
        }
    }

}