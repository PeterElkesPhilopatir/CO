package com.peter.co.framework.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.co.R
import com.peter.co.databinding.RowRepositoryBinding
import com.peter.co.domain.models.Repository
class RepositoryAdapter :
    PagingDataAdapter<Repository, NewsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            NewsViewHolder {
        return NewsViewHolder(RowRepositoryBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        getItem(position)?.let { holder.bind(it) }

    }

    companion object DiffCallback : DiffUtil.ItemCallback<Repository>() {
        override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean {
            return oldItem.id == newItem.id
        }
    }
}

class NewsViewHolder(private var binding: RowRepositoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Repository) {
        binding.data = item
        binding.executePendingBindings()
    }
}
