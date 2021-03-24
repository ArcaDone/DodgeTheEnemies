package com.arcadan.dodgetheenemies.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.arcadan.dodgetheenemies.BuildConfig
import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.databinding.FragmentWonBinding
import com.arcadan.dodgetheenemies.dialogs.PowerUpDialog
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.dodgetheenemies.models.Level
import com.arcadan.dodgetheenemies.service.MusicManager
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.bindMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.resumeMusic
import com.arcadan.dodgetheenemies.util.ViewUtils
import com.arcadan.dodgetheenemies.viewmodel.GameWonViewModel
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlin.random.Random
import kotlinx.android.synthetic.main.fragment_won.coin_won_image
import kotlinx.android.synthetic.main.fragment_won.coins_game_won_text
import kotlinx.android.synthetic.main.fragment_won.coins_won_text
import kotlinx.android.synthetic.main.fragment_won.double_coins_earned
import kotlinx.android.synthetic.main.fragment_won.gem_won_image
import kotlinx.android.synthetic.main.fragment_won.gem_won_text
import kotlinx.android.synthetic.main.fragment_won.hearts_won_image
import kotlinx.android.synthetic.main.fragment_won.hearts_won_text
import kotlinx.android.synthetic.main.fragment_won.play_game
import kotlinx.android.synthetic.main.fragment_won.progressBar
import kotlinx.android.synthetic.main.fragment_won.recordText
import kotlinx.android.synthetic.main.fragment_won.retryLevel
import kotlinx.android.synthetic.main.fragment_won.star
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameWonFragment : Fragment(), PowerUpDialog.PowerUpDialogListener {

    private lateinit var gameModelJson: String
    private lateinit var viewModel: GameWonViewModel
    private lateinit var level: Level
    private lateinit var rewardedAd: RewardedAd
    private lateinit var interstitialAd: InterstitialAd
    private var showInterstitialAds: Boolean = false
    private var areAdsLoaded = true
    private var coinEarned = 0

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        LogHelper.log(TAG, "User Observer Exception: ${exception.stackTrace}", Log.ERROR)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(GameWonViewModel::class.java)

        return FragmentWonBinding.inflate(inflater).apply {
            gameWonViewModel = viewModel
            lifecycleOwner = this@GameWonFragment
        }.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindMusic()

        // get score and record value
        coinEarned = arguments?.getInt("coins", 0)!!
        val score: Int? = arguments?.getInt("score", 0)
        gameModelJson = arguments?.getString("model")!!
        viewModel.gameModelJson = gameModelJson

        level = Gson().fromJson(gameModelJson, Level::class.java)

        recordText.text = score.toString()
        coins_game_won_text.text = coinEarned.toString()
        gem_won_text.text = "+${level.rewards.gems}"
        coins_won_text.text = "+${level.rewards.coins}"
        hearts_won_text.text = "+${level.rewards.hearts}"

        viewModel.starsShowerAnimation(star)
        viewModel.rewardsAnimation(gem_won_text, gem_won_image, 600)
        viewModel.rewardsAnimation(coins_won_text, coin_won_image, 800)
        viewModel.rewardsAnimation(hearts_won_text, hearts_won_image, 1000)

        loadRewardedAd()
        loadInterstitial()

        play_game.setOnClickListener {
            if (level.id == 29) {
                Snackbar.make(requireView(), "Next Level coming soon", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                manageAdsShowAndProgressBarPlayGame()
            }
        }
        retryLevel.setOnClickListener {
            manageAdsShowAndProgressBarRetry()
        }

        double_coins_earned.setOnClickListener {
            if (Persistence.instance.getBool(Keys.CONNECTION_STATE)) {
                startAdsAction()
            } else {
                Toast.makeText(
                    Dodgers.context,
                    getString(R.string.check_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.errorState.observe(viewLifecycleOwner, { errorState ->
            if (errorState) {
                Snackbar.make(requireView(), viewModel.errorMessage.value!!, Snackbar.LENGTH_SHORT)
                    .show()
            }
        })

        ViewUtils.performBackClick(viewLifecycleOwner, requireActivity(), requireView())
    }

    private fun manageAdsShowAndProgressBarPlayGame() {
        CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            showInterstitialAds = Random.nextBoolean()
            progressBar.visibility = View.VISIBLE
            delay(600)
            progressBar.visibility = View.INVISIBLE

            if (interstitialAd.isLoaded &&
                showInterstitialAds &&
                Persistence.instance.getBool(Keys.CONNECTION_STATE)
            ) {
                interstitialAd.show()
            } else {
                viewModel.retry = false

                PowerUpDialog.newInstance(this@GameWonFragment, level.id + 1)
                    .show(requireActivity().supportFragmentManager, "powerUpDialog")
            }
        }
    }

    private fun manageAdsShowAndProgressBarRetry() {
        CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            showInterstitialAds = Random.nextBoolean()
            progressBar.visibility = View.VISIBLE
            delay(600)
            progressBar.visibility = View.INVISIBLE

            if (interstitialAd.isLoaded &&
                showInterstitialAds &&
                Persistence.instance.getBool(Keys.CONNECTION_STATE)
            ) {
                interstitialAd.show()
            } else {
                viewModel.retry = true
                PowerUpDialog.newInstance(this@GameWonFragment, level.id)
                    .show(requireActivity().supportFragmentManager, "powerUpDialog")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeMusic()
    }

    private fun loadInterstitial() {
        interstitialAd = InterstitialAd(requireContext())
        interstitialAd.adUnitId = BuildConfig.interstitialAds
        interstitialAd.loadAd(AdRequest.Builder().build())
    }

    private fun loadRewardedAd() {
        // RewardedAd
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
    }

    private fun startAdsAction() {
        if (rewardedAd.isLoaded) {

            MusicManager.pauseMusic()

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
        try {
            DataManager.instance.user.value!!.coins += coinEarned
            coinEarned += coinEarned
        } catch (e: Exception) {
            LogHelper.log(TAG, "User Observer Exception: ${e.stackTrace}")
        }
        double_coins_earned.visibility = View.GONE
        coins_game_won_text.text = coinEarned.toString()
    }

    override fun onDialogPlayClick(dialog: DialogFragment) {
        if (viewModel.retry) {
            viewModel.playAgain(requireView())
        } else {
            viewModel.playNextLevel(requireView())
        }
        LogHelper.log(TAG, "level actual from play button: ${level.id}")
    }

    override fun onDialogDiscardClick(dialog: DialogFragment) {
        viewModel.backpack.clear()

        LogHelper.log(TAG, "level actual from play button: ${level.id}")
    }

    override fun onDialogSlurpRedClick(dialog: DialogFragment) {
        viewModel.fillBackpack(PowerUp.SLURP_RED)
    }

    override fun onDialogSpeedUpClick(dialog: DialogFragment) {
        viewModel.fillBackpack(PowerUp.SPEED_UP)
    }

    override fun onDialogMegaJumpClick(dialog: DialogFragment) {
        viewModel.fillBackpack(PowerUp.MEGA_JUMP)
    }

    override fun onDialogDoubleCoinsClick(dialog: DialogFragment) {
        viewModel.fillBackpack(PowerUp.DOUBLE_COINS)
    }
}
