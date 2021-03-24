package com.arcadan.dodgetheenemies.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.interfaces.BottomSheetCallBack
import com.arcadan.dodgetheenemies.onboarding.OnBoardingCallBack
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.on_boarding.container_on_boarding

class OnBoardingFragment : BottomSheetDialogFragment(), OnBoardingCallBack {

    companion object {
        fun newInstance(callBack: BottomSheetCallBack): OnBoardingFragment =
            OnBoardingFragment().apply {
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.on_boarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container_on_boarding.callBacks = this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val d = dialogInterface as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!).state =
                BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }

    override fun closeOnBoardingSheet() {
        dismiss()
    }
}
