package com.mospolytech.mospolyhelper.features.ui.utilities.news

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemNewsBinding
import com.mospolytech.mospolyhelper.domain.utilities.news.model.NewsPreview
import com.mospolytech.mospolyhelper.features.utils.CustomGlideModule
import com.mospolytech.mospolyhelper.features.utils.GlideApp
import com.mospolytech.mospolyhelper.features.utils.diff_utils.NewsDiffUtils
import com.mospolytech.mospolyhelper.utils.TAG
import com.mospolytech.mospolyhelper.utils.dp
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

    override fun onViewRecycled(holder: NewsViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
        }
        private val viewBinding by viewBinding(ItemNewsBinding::bind)

        fun bind(news: NewsPreview?) {
            if (news != null) {
                viewBinding.textviewNewsDate.text = news.date.format(dateFormatter)
                viewBinding.textviewNewsTitle.text = news.title
                GlideApp
                    .with(itemView.context)
                    .load(news.imageUrl)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(26.dp(itemView.context).toInt())))
                    .into(viewBinding.imageviewNews)
            } else {
                viewBinding.textviewNewsDate.text = ""
                viewBinding.textviewNewsTitle.text = ""
            }
        }

        fun recycle() {
            Glide.with(itemView.context)
                .clear(viewBinding.imageviewNews)
        }
    }
}