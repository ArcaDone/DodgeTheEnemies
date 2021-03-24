package com.arcadan.dodgetheenemies.util

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.arcadan.dodgetheenemies.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ViewUtils {

    companion object {
        fun performBackClick(
            lifecycleOwner: LifecycleOwner,
            fragmentActivity: FragmentActivity,
            view: View
        ) {
            fragmentActivity.onBackPressedDispatcher.addCallback(
                lifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        view.findNavController().navigate(R.id.mainFragment)
                    }
                }
            )
        }

        fun showBottomSheet(view: View) {
            val bottomSheetBehavior = BottomSheetBehavior.from(view)

            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }
}
