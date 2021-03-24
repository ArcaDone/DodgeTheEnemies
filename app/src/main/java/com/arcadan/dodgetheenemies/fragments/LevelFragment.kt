package com.arcadan.dodgetheenemies.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arcadan.dodgetheenemies.adapters.LevelAdapter
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.databinding.FragmentLevelBinding
import com.arcadan.dodgetheenemies.dialogs.PowerUpDialog
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.dodgetheenemies.game.GameParameters
import com.arcadan.dodgetheenemies.graphics.ObjectsAnimation.Companion.scaling
import com.arcadan.dodgetheenemies.graphics.ObjectsAnimation.Companion.translation
import com.arcadan.dodgetheenemies.interfaces.BottomSheetCallBack
import com.arcadan.dodgetheenemies.models.Level
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.bindMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.pauseMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.resumeMusic
import com.arcadan.dodgetheenemies.util.ViewUtils
import com.arcadan.dodgetheenemies.viewmodel.LevelViewModel
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_level.heart_value_animation
import kotlinx.android.synthetic.main.top_bar.coins_value
import kotlinx.android.synthetic.main.top_bar.gem_value
import kotlinx.android.synthetic.main.top_bar.heart_item
import kotlinx.android.synthetic.main.top_bar.heart_value
import kotlinx.android.synthetic.main.top_bar.item_coins_veil_layout
import kotlinx.android.synthetic.main.top_bar.item_gem_veil_layout
import kotlinx.android.synthetic.main.top_bar.item_heart_veil_layout
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LevelFragment : Fragment(), BottomSheetCallBack, PowerUpDialog.PowerUpDialogListener {

    private lateinit var viewModel: LevelViewModel

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        LogHelper.log(TAG, "User Observer Exception: ${exception.stackTrace}", Log.ERROR)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(LevelViewModel::class.java)

        viewModel.errorState.observe(viewLifecycleOwner, Observer { errorState ->
            if (errorState) {
                Snackbar.make(requireView(), viewModel.errorMessage.value!!, Snackbar.LENGTH_SHORT)
                    .show()
            }
        })

        return FragmentLevelBinding.inflate(inflater).apply {
            levelViewModel = viewModel
            lifecycleOwner = this@LevelFragment

            itemList.adapter = LevelAdapter(LevelAdapter.OnLevelClickListener {
                levelClickCallBack(it)
            })
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindMusic()

        CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            item_gem_veil_layout?.unVeil()
            item_coins_veil_layout?.unVeil()
            item_heart_veil_layout?.unVeil()
            gem_value?.text = DataManager.instance.user.value!!.gems.toString()
            coins_value?.text = DataManager.instance.user.value!!.coins.toString()
            heart_value?.text = DataManager.instance.user.value!!.hearts.toString()
        }

        ViewUtils.performBackClick(viewLifecycleOwner, requireActivity(), requireView())
    }

    override fun onResume() {
        super.onResume()
        resumeMusic()
    }

    private fun levelClickCallBack(level: Level) {
        viewModel.selectedLevel.value = level
        Persistence.instance.saveInt(Keys.SELECTED_LEVEL, level.id)
        when {
            level.id > DataManager.instance.user.value!!.unlockedLevel -> {
                viewModel.unlockedLevelError()
            }
            DataManager.instance.user.value!!.hearts >= GameParameters.instance.deltaHearts -> {
                viewModel.backpack.clear()
                PowerUpDialog.newInstance(this@LevelFragment, level.id)
                    .show(requireActivity().supportFragmentManager, "powerUpDialog")
            }
            else -> HeartsRewardFragment.newInstance(this).show(parentFragmentManager, "watch_me")
        }
    }

    override fun giveHeartsReward() {
        resumeMusic()
        scaling(heart_item)
        CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            if (DataManager.instance.user.value!!.heartsCount.counter == 0) {
                DataManager.instance.user.value!!.heartsCount.daysCount =
                    System.currentTimeMillis() / 1000L
            }
            DataManager.instance.user.value!!.hearts += 20
            DataManager.instance.user.value!!.heartsCount.counter += 1
            heart_value?.text = DataManager.instance.user.value!!.hearts.toString()
        }
    }

    override fun adsActionStarted() {
        pauseMusic()
    }

    override fun onDialogPlayClick(dialog: DialogFragment) {
        if (Persistence.instance.getBool(Keys.CONNECTION_STATE)) {
            decrementHeartsAnimation()
            viewModel.play()
            viewModel.startLevel(requireView())
        }
    }

    override fun onDialogDiscardClick(dialog: DialogFragment) {
        viewModel.discard()
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

    private fun decrementHeartsAnimation() {
        if (heart_value_animation != null) {
            viewModel.decrementHeartsAnimation.value = true
            translation(heart_value_animation)
        }
    }
}
