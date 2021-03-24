package com.arcadan.dodgetheenemies.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.arcadan.dodgetheenemies.BuildConfig
import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.interfaces.BottomSheetCallBack
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_hearts_reward.go_to_shop
import kotlinx.android.synthetic.main.bottom_sheet_hearts_reward.watch_video_image

class HeartsRewardFragment : BottomSheetDialogFragment() {

    private lateinit var rewardedAd: RewardedAd
    private lateinit var callBack: BottomSheetCallBack
    private var areAdsLoaded = true

    companion object {
        fun newInstance(callBack: BottomSheetCallBack): HeartsRewardFragment =
            HeartsRewardFragment().apply {
                this.callBack = callBack
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_hearts_reward, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Rewarded Ad
        rewardedAd = RewardedAd(
            requireContext(),
            BuildConfig.rewardVideoAds
        )

        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                LogHelper.log(TAG, "onRewardedAdLoaded", Log.VERBOSE)
            }

            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                LogHelper.log(TAG, "onRewardedAdFailedToLoad", Log.VERBOSE)
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)

        watch_video_image.setOnClickListener {
            if (Persistence.instance.getBool(Keys.CONNECTION_STATE) && DataManager.instance.user.value != null) {
                if (DataManager.instance.user.value!!.heartsCount.counter < 25) {
                    startAdsAction()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getText(R.string.day_video_limits),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    Dodgers.context,
                    getString(R.string.check_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        go_to_shop.setOnClickListener {
            if (Persistence.instance.getBool(Keys.CONNECTION_STATE)) {
                val navHost: Fragment =
                    requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
                val navController = NavHostFragment.findNavController(navHost)
                navController.navigate(R.id.shopFragment)
                dismiss()
            } else {
                Toast.makeText(
                    Dodgers.context,
                    getString(R.string.check_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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

    private fun startAdsAction() {
        if (rewardedAd.isLoaded) {

            callBack.adsActionStarted()

            val adCallback = object : RewardedAdCallback() {
                override fun onRewardedAdOpened() {
                    LogHelper.log(TAG, "onRewardedAdOpened", Log.VERBOSE)
                }

                override fun onRewardedAdClosed() {
                    giveTheReward()
                }

                override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                    LogHelper.log(TAG, "onUserEarnedReward", Log.VERBOSE)
                }

                override fun onRewardedAdFailedToShow(errorCode: Int) {
                    LogHelper.log(TAG, "onRewardedAdFailedToShow", Log.VERBOSE)
                }
            }
            rewardedAd.show(requireActivity(), adCallback)
        } else {
            LogHelper.log(TAG, "not loaded", Log.VERBOSE)
            areAdsLoaded = false
            Toast.makeText(
                requireContext(),
                "Wait a moment, we are loading your video :-)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun giveTheReward() {
        LogHelper.log(TAG, "onRewardedAdClosed", Log.VERBOSE)
        areAdsLoaded = true

        callBack.giveHeartsReward()
        dismiss()
    }
}
