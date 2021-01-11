package com.cosmos.unreddit.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cosmos.unreddit.databinding.ItemListContentBinding

class RecyclerViewStateAdapter :
    ListAdapter<RecyclerViewStateAdapter.Page, RecyclerViewStateAdapter.ViewHolder>(
        SEARCH_COMPARATOR
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemListContentBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position).adapter)
    }

    inner class ViewHolder(
        private val binding: ItemListContentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
            binding.listContent.apply {
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter
            }
        }
    }

    data class Page(
        @StringRes val title: Int,
        val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>
    )

    companion object {
        private val SEARCH_COMPARATOR = object : DiffUtil.ItemCallback<Page>() {

            override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
                return oldItem == newItem
            }
        }
    }
}
