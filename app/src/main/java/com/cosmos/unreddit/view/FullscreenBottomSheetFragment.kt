package com.cosmos.unreddit

import android.util.DisplayMetrics
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class FullscreenBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onResume() {
        super.onResume()
        expandToFullscreen()
    }

    private fun expandToFullscreen() {
        val bottomSheetDialog = dialog as BottomSheetDialog

        val bottomSheet = bottomSheetDialog
            .findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?

        bottomSheet?.let {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val windowHeight = displayMetrics.heightPixels

            val layoutParams = it.layoutParams
            layoutParams?.apply { height = windowHeight }

            it.layoutParams = layoutParams

            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}