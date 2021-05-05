package com.cosmos.unreddit.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cosmos.unreddit.databinding.ItemListContentBinding
import com.cosmos.unreddit.ui.loadstate.NetworkLoadStateAdapter
import com.cosmos.unreddit.util.extension.addLoadStateListener

class RecyclerViewStateAdapter(val onError: () -> Unit) :
    ListAdapter<RecyclerViewStateAdapter.Page, RecyclerViewStateAdapter.ViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemListContentBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adapter = getItem(position).adapter
        if (adapter is PagingDataAdapter<out Any, out RecyclerView.ViewHolder>) {
            holder.bindPaging(adapter)
        } else {
            holder.bind(adapter)
        }
    }

    inner class ViewHolder(
        private val binding: ItemListContentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindPaging(adapter: PagingDataAdapter<out Any, out RecyclerView.ViewHolder>) {
            adapter.addLoadStateListener(binding.listContent, binding.loadingState, onError)

            binding.listContent.apply {
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = NetworkLoadStateAdapter { adapter.retry() },
                    footer = NetworkLoadStateAdapter { adapter.retry() }
                )
            }
        }

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
        private val COMPARATOR = object : DiffUtil.ItemCallback<Page>() {

            override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
                return oldItem == newItem
            }
        }
    }
}
