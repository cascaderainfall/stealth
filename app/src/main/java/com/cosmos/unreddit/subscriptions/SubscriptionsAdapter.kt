package com.cosmos.unreddit.subscriptions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Precision
import coil.size.Scale
import com.cosmos.unreddit.R
import com.cosmos.unreddit.databinding.ItemSubscriptionBinding
import com.cosmos.unreddit.subreddit.Subscription

class SubscriptionsAdapter(
    val listener: (String) -> Unit
) : RecyclerView.Adapter<SubscriptionsAdapter.SubscriptionViewHolder>() {

    private val subscriptions: MutableList<Subscription> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SubscriptionViewHolder(ItemSubscriptionBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        holder.bind(subscriptions[position])
    }

    override fun getItemCount(): Int {
        return subscriptions.size
    }

    fun submitData(subscriptions: List<Subscription>) {
        val result =
            DiffUtil.calculateDiff(SubscriptionsDiffCallback(this.subscriptions, subscriptions))

        this.subscriptions.clear()
        this.subscriptions.addAll(subscriptions)

        result.dispatchUpdatesTo(this)
    }

    inner class SubscriptionViewHolder(
        private val binding: ItemSubscriptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(subscription: Subscription) {
            binding.subscription = subscription

            binding.subscriptionImage.load(subscription.icon) {
                crossfade(true)
                scale(Scale.FILL)
                precision(Precision.AUTOMATIC)
                placeholder(R.drawable.icon_reddit_placeholder)
                error(R.drawable.icon_reddit_placeholder)
                fallback(R.drawable.icon_reddit_placeholder)
            }

            itemView.setOnClickListener {
                listener(subscription.name)
            }
        }
    }

    inner class SubscriptionsDiffCallback(
        private val oldList: List<Subscription>,
        private val newList: List<Subscription>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
