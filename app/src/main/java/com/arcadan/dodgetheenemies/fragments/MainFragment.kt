package com.arcadan.dodgetheenemies.fragments

import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.arcadan.dodgetheenemies.BuildConfig
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.data.DatabaseManager
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.databinding.FragmentMainBinding
import com.arcadan.dodgetheenemies.interfaces.BottomSheetCallBack
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.bindMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.musicService
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.pauseMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.resumeMusic
import com.arcadan.dodgetheenemies.viewmodel.MainViewModel
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_main.ad_view
import kotlinx.android.synthetic.main.fragment_main.animation
import kotlinx.android.synthetic.main.fragment_main.celebration_view
import kotlinx.android.synthetic.main.fragment_main.inventory
import kotlinx.android.synthetic.main.fragment_main.level_up_image
import kotlinx.android.synthetic.main.fragment_main.playButton
import kotlinx.android.synthetic.main.fragment_main.settings
import kotlinx.android.synthetic.main.fragment_main.shop_image
import kotlinx.android.synthetic.main.top_bar_vertical.coins_main_text
import kotlinx.android.synthetic.main.top_bar_vertical.experience_constraint
import kotlinx.android.synthetic.main.top_bar_vertical.gem_main_text
import kotlinx.android.synthetic.main.top_bar_vertical.heart_main_item
import kotlinx.android.synthetic.main.top_bar_vertical.heart_main_text
import kotlinx.android.synthetic.main.top_bar_vertical.item_coins_veil_layout
import kotlinx.android.synthetic.main.top_bar_vertical.item_gem_veil_layout
import kotlinx.android.synthetic.main.top_bar_vertical.item_heart_veil_layout
import kotlinx.android.synthetic.main.top_bar_vertical.item_record_veil_layout
import kotlinx.android.synthetic.main.top_bar_vertical.record_item
import kotlinx.android.synthetic.main.top_bar_vertical.record_text
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : Fragment(), BottomSheetCallBack {

    private lateinit var viewModel: MainViewModel
    private lateinit var adView: AdView

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        LogHelper.log(TAG, "User Observer Exception: ${exception.stackTrace}", Log.ERROR)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        return FragmentMainBinding.inflate(inflater).apply {
            this@MainFragment.adView = this.adView
            adView.loadAd(AdRequest.Builder().build())
            mainViewModel = viewModel
            lifecycleOwner = this@MainFragment
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DataManager.instance.user.observe(viewLifecycleOwner, { user ->
            user?.let { userValue ->
                try {
                    item_gem_veil_layout?.unVeil()
                    item_coins_veil_layout?.unVeil()
                    item_heart_veil_layout?.unVeil()
                    item_record_veil_layout?.unVeil()
                    playButton.isEnabled = true
                    settings.isEnabled = true
                    inventory.isEnabled = true
                    shop_image.isEnabled = true
                    gem_main_text?.text = userValue.gems.toString()
                    coins_main_text?.text = userValue.coins.toString()
                    heart_main_text?.text = userValue.hearts.toString()
                    record_text?.text =
                        resources.getString(R.string.player_level, userValue.levelPlayer.toString())
                    viewModel.setExperienceLevel(userValue, experience_constraint)
                    // Set build version
                    DatabaseManager.setValue("buildVersion", BuildConfig.VERSION_NAME)
                    var userDeviceInfo = "Debug-infos:"
                    userDeviceInfo += "OS Version: ${System.getProperty("os.version")}(${Build.VERSION.INCREMENTAL})"
                    userDeviceInfo += "OS API Level: ${Build.VERSION.SDK_INT}"
                    userDeviceInfo += "Device: ${Build.DEVICE}"
                    userDeviceInfo += "Model (and Product): ${Build.MODEL} (${Build.PRODUCT})"
                    Log.d(TAG, "info device test: $userDeviceInfo")
                    DatabaseManager.setValue("userDeviceInfo", userDeviceInfo)
                } catch (e: Exception) {
                    LogHelper.log(TAG, "User Observer Exception: ${e.stackTrace}")
                }
            }
        })

        viewModel.heartsTextItem.observe(viewLifecycleOwner, {
            it?.let {
                heart_main_text?.text = it
            }
        })

        startNewLevelAnimation()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindMusic()

        heart_main_item.setOnClickListener {
            val fragment = HeartsRewardFragment.newInstance(this)
            fragment.show(parentFragmentManager, "watch_me")
        }

        // Animation
        animation.setBackgroundResource(R.drawable.animation)

        viewModel.anim = animation.background as AnimationDrawable
        viewModel.manageArrowAnim()

        viewModel.playButtonAnimation(playButton)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity!!.moveTaskToBack(true)
                    activity!!.finish()
                }
            }
        )

        if (BuildConfig.DEBUG)
            adView.visibility = View.INVISIBLE
        // Show totPlayers
        record_item.setOnClickListener {
            if (BuildConfig.DEBUG) {
                CoroutineScope(Dispatchers.Default).launch {
                    val robe = Firebase.database.getReference("users")
                    robe.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val totPlayers = dataSnapshot.getValue<HashMap<String, Any>>()
                            LogHelper.log(TAG, "totPlayers are: ${totPlayers?.size}")
                            Toast.makeText(
                                requireContext(),
                                "Attualmente ci sono ${totPlayers?.size} giocatori :-)",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            LogHelper.log(TAG, "Failed to read value user", Log.ERROR)
                        }
                    })
                }
            }
        }

        CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            delay(1000)
            val showCarousel = Persistence.instance.getBool(Keys.SKIPPED_CAROUSEL)
            val didReview = Persistence.instance.getBool(Keys.REVIEWVED)
            if (!showCarousel) {
                val fragment = OnBoardingFragment.newInstance(this@MainFragment)
                fragment.show(parentFragmentManager, "OnBoarding")
            } else if (!didReview) {
                showInAppReview()
            }
        }
    }

    private fun showInAppReview() {
        val manager = ReviewManagerFactory.create(requireContext())
        val request = manager.requestReviewFlow()
        var reviewInfo: ReviewInfo?
        request.addOnCompleteListener { requestTest ->
            if (requestTest.isSuccessful) {
                // We got the ReviewInfo object
                reviewInfo = requestTest.result
                reviewFlow(manager, reviewInfo)
                LogHelper.log(TAG, "success: ${requestTest.result}")
                Persistence.instance.saveBool(Keys.REVIEWVED, true)
            } else {
                LogHelper.log(TAG, "ERROR: ${requestTest.exception}")
            }
        }
    }

    private fun reviewFlow(
        manager: ReviewManager,
        reviewInfo: ReviewInfo?
    ) {
        val flow = manager.launchReviewFlow(requireActivity(), reviewInfo!!)
        flow.addOnCompleteListener { flowRes ->
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.

            LogHelper.log(TAG, "success: ${flowRes.result}")
        }
    }

    override fun onResume() {
        super.onResume()
        ad_view.resume()
        if (musicService != null && Persistence.instance.getBool(Keys.MUSIC_STATE)) {
            musicService!!.resumeMusic()
        }
    }

    override fun onDestroy() {
        Persistence.instance.saveBool(Keys.STOP_ANIMATION, false)
        Persistence.instance.saveInt(Keys.SELECTED_LEVEL, 0)
        ad_view?.destroy()
        super.onDestroy()
    }

    override fun giveHeartsReward() {
        resumeMusic()
        viewModel.heartsItemAnimation(heart_main_item)
        viewModel.updateUIAndDatabase()
    }

    override fun adsActionStarted() {
        pauseMusic()
    }

    private fun startNewLevelAnimation() {
        if (Persistence.instance.getBool(Keys.NEW_LEVEL_ACHIEVED)) {
            lifecycleScope.launch {
                delay(200)
                viewModel.celebrateNewLevel(celebration_view, level_up_image)
                Persistence.instance.saveBool(Keys.NEW_LEVEL_ACHIEVED, false)
            }
        }
    }
}
