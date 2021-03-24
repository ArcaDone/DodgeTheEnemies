package com.arcadan.dodgetheenemies.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.arcadan.dodgetheenemies.BuildConfig
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.databinding.FragmentGameOverBinding
import com.arcadan.dodgetheenemies.dialogs.PowerUpDialog
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.bindMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.resumeMusic
import com.arcadan.dodgetheenemies.util.ViewUtils
import com.arcadan.dodgetheenemies.viewmodel.GameOverViewModel
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random
import kotlinx.android.synthetic.main.fragment_won.play_game
import kotlinx.android.synthetic.main.fragment_won.progressBar
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameOverFragment : Fragment(), PowerUpDialog.PowerUpDialogListener {

    private lateinit var viewModel: GameOverViewModel
    private lateinit var interstitialAd: InterstitialAd
    private var showInterstitialAds: Boolean = false
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        LogHelper.log(TAG, "User Observer Exception: ${exception.stackTrace}", Log.ERROR)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(GameOverViewModel::class.java)

        return FragmentGameOverBinding.inflate(inflater).apply {
            gameOverViewModel = viewModel
            lifecycleOwner = this@GameOverFragment
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindMusic()
        loadInterstitial()
        viewModel.gameModelJson = arguments?.getString("model")!!

        play_game.setOnClickListener {
            manageAdsShowAndProgressBar()
        }
        viewModel.errorState.observe(viewLifecycleOwner, { errorState ->
            if (errorState) {
                Snackbar.make(requireView(), viewModel.errorMessage.value!!, Snackbar.LENGTH_SHORT)
                    .show()
            }
        })

        ViewUtils.performBackClick(viewLifecycleOwner, requireActivity(), requireView())
    }

    override fun onResume() {
        super.onResume()
        resumeMusic()
    }

    override fun onDialogPlayClick(dialog: DialogFragment) {
        viewModel.playAgain(requireView())
    }

    override fun onDialogDiscardClick(dialog: DialogFragment) {
        viewModel.backpack.clear()
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

    private fun loadInterstitial() {
        interstitialAd = InterstitialAd(requireContext())
        interstitialAd.adUnitId = BuildConfig.interstitialAds
        interstitialAd.loadAd(AdRequest.Builder().build())
    }

    private fun manageAdsShowAndProgressBar() {
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
                PowerUpDialog.newInstance(
                    this@GameOverFragment,
                    Persistence.instance.getInt(Keys.SELECTED_LEVEL)
                )
                    .show(requireActivity().supportFragmentManager, "powerUpDialog")
            }
        }
    }
}
