package com.cosmos.unreddit.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import coil.Coil
import coil.request.ImageRequest
import com.cosmos.unreddit.R
import com.cosmos.unreddit.post.Award
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class AwardGroup
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ChipGroup(context, attrs, defStyleAttr) {

    fun setAwards(awards: List<Award>) {
        removeAllViews()

        for (award in awards) {
            val chip = Chip(context).apply {
                Coil.imageLoader(context).enqueue(ImageRequest.Builder(context).apply {
                    data(award.icon)
                    allowHardware(false)
                    target { drawable ->
                        chipIcon = drawable
                    }
                }.build())
                chipIconSize = 42F
                chipEndPadding = 0F
                textSize = 12F
                text = context.getString(R.string.award_count, award.count)
                setChipBackgroundColorResource(R.color.chip_background_color)
            }
            addView(chip as View)
        }

        if (awards.isEmpty()) {
            visibility = View.GONE
        }
    }
}