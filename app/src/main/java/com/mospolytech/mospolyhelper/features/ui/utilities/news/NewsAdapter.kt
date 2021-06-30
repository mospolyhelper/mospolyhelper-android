package com.mospolytech.mospolyhelper.features.ui.utilities.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemNewsBinding
import com.mospolytech.mospolyhelper.domain.utilities.news.model.NewsPreview
import com.mospolytech.mospolyhelper.features.utils.diff_utils.NewsDiffUtils
import java.time.format.DateTimeFormatter

class NewsAdapter : PagingDataAdapter<NewsPreview, NewsAdapter.NewsViewHolder>(NewsDiffUtils)  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_news, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM")
        }
        private val viewBinding by viewBinding(ItemNewsBinding::bind)

        fun bind(news: NewsPreview?) {
            if (news != null) {
                viewBinding.textviewNewsDate.text = news.date.format(dateFormatter)
                viewBinding.textviewNewsTitle.text = news.title
            } else {
                viewBinding.textviewNewsDate.text = ""
                viewBinding.textviewNewsTitle.text = ""
            }
        }
    }
}