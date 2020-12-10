package com.cosmos.unreddit.subscriptions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Precision
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.cosmos.unreddit.databinding.ItemSubscriptionBinding
import com.cosmos.unreddit.subreddit.Subscription

class SubscriptionsAdapter : RecyclerView.Adapter<SubscriptionsAdapter.SubscriptionViewHolder>() {

    private val subscriptions: MutableList<Subscription> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val inflater =  LayoutInflater.from(parent.context)
        return SubscriptionViewHolder(ItemSubscriptionBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        holder.bind(subscriptions[position])
    }

    override fun getItemCount(): Int {
        return subscriptions.size
    }

    fun submitData(subscriptions: List<Subscription>) {
        this.subscriptions.clear()
        this.subscriptions.addAll(subscriptions)
        notifyDataSetChanged()
    }

    inner class SubscriptionViewHolder(private val binding: ItemSubscriptionBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(subscription: Subscription) {
            binding.subscription = subscription

            binding.subscriptionImage.load(subscription.icon) {
                crossfade(true)
                scale(Scale.FILL)
                precision(Precision.AUTOMATIC)
                transformations(CircleCropTransformation())
            }
        }
    }
}