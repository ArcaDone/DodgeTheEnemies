package com.arcadan.dodgetheenemies.viewmodel

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.data.models.User
import com.arcadan.dodgetheenemies.fragments.MainFragmentDirections
import com.arcadan.dodgetheenemies.game.GameParameters
import com.arcadan.dodgetheenemies.graphics.ObjectsAnimation.Companion.scaling
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class MainViewModel : ViewModel() {
    private var _heartsTextItem = MutableLiveData<String>()

    lateinit var anim: AnimationDrawable

    var observableBoolean = ObservableBoolean(true)
    var observableBooleanLevelUp = ObservableBoolean(false)
    var observableBooleanRewardText = ObservableBoolean(false)
    var observableVisibilityPlayButton = ObservableBoolean(true)
    val heartsTextItem: LiveData<String>
        get() = _heartsTextItem

    init {
        syncDatabaseAndManageHearts()
    }

    private fun showConfetti(celebrationView: KonfettiView) {
        celebrationView.build()
            .addColors(Color.YELLOW, Color.BLUE, Color.RED)
            .setDirection(0.0, 359.0)
            .setSpeed(10f, 16f)
            .setFadeOutEnabled(true)
            .setTimeToLive(1000L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12))
            .setPosition(-50f, celebrationView.width + 50f, -50f, -50f)
            .streamFor(1000, 1000L)
    }

    private fun syncDatabaseAndManageHearts() {
        viewModelScope.launch {
            if (Persistence.instance.getInt(Keys.USER_ID) != 0) {
//                DataManager.instance.initDataManager(Persistence.instance.getInt(Keys.USER_ID))
                delay(2000)
                if (DataManager.instance.user.value != null) {
                    checkHeartsValue()
                    checkHeartsCounter()
                }
            }
        }
    }

    private fun checkHeartsValue() {
        if (DataManager.instance.user.value!!.hearts >= 100) {
            // stop count date time. we are full
            DataManager.instance.user.value!!.heartsCount.timerHasStarted = false
            return
        }
        val currentTime = System.currentTimeMillis() / 1000L

        // check time difference in minutes
        val differenceDateInMinutes =
            (currentTime - DataManager.instance.user.value!!.heartsCount.localDateTime) / 60

        val heartsToRegenerate = differenceDateInMinutes / GameParameters.instance.restoreHeartsRate
        if (heartsToRegenerate > 0) {
            DataManager.instance.user.value!!.heartsCount.localDateTime = currentTime
        }

        var totalHearts = DataManager.instance.user.value!!.hearts + heartsToRegenerate

        if (totalHearts >= GameParameters.instance.maxHearts) {
            totalHearts = GameParameters.instance.maxHearts.toLong()
            DataManager.instance.user.value!!.heartsCount.timerHasStarted = false
        }

        DataManager.instance.user.value!!.hearts = totalHearts.toInt()
    }

    private fun checkHeartsCounter() {
        val currentTime = System.currentTimeMillis() / 1000L
        // check time difference in days
        val differenceDateInDays =
            (currentTime - DataManager.instance.user.value!!.heartsCount.daysCount) / (60 * 60 * 24)
        if (differenceDateInDays > 0) {
            DataManager.instance.user.value!!.heartsCount.counter = 0
        }
    }

    private fun animateImage(levelUpImage: ImageView) {
        scaling(levelUpImage)
    }

    fun manageArrowAnim() {
        if (!Persistence.instance.getBool(Keys.STOP_ANIMATION))
            anim.start()
        else
            observableBoolean.set(false)
    }

    fun playButtonAnimation(playButton: Button) {
        scaling(playButton, false)
    }

    fun playButtonClick(view: View) {
        if (Persistence.instance.getBool(Keys.CONNECTION_STATE)) {
            view.findNavController()
                .navigate(MainFragmentDirections.actionMainFragmentToLevelFragment())
        } else {
            Snackbar.make(
                view,
                Dodgers.context!!.getString(R.string.check_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    fun goToHowToPlay(view: View) {
        view.findNavController()
            .navigate(MainFragmentDirections.actionMainFragmentToHowToPlayFragment())
    }

    fun goToSkin(view: View) {
        view.findNavController()
            .navigate(MainFragmentDirections.actionMainFragmentToSkinsFragment())
    }

    fun goToSettings(view: View) {
        view.findNavController().navigate(R.id.settingsFragment)
    }

    fun goToInventory(view: View) {
        if (Persistence.instance.getBool(Keys.CONNECTION_STATE)) {
            view.findNavController()
                .navigate(MainFragmentDirections.actionMainFragmentToInventoryFragment())
        } else {
            Snackbar.make(
                view,
                Dodgers.context!!.getString(R.string.check_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    fun goToShop(view: View) {
        if (Persistence.instance.getBool(Keys.CONNECTION_STATE)) {
            anim.stop()
            observableBoolean.set(false)
            view.findNavController()
                .navigate(MainFragmentDirections.actionMainFragmentToShopFragment())
            Persistence.instance.saveBool(Keys.STOP_ANIMATION, true)
        } else {
            Snackbar.make(
                view,
                Dodgers.context!!.getString(R.string.check_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    fun setExperienceLevel(
        userValue: User,
        experienceConstraint: ConstraintLayout
    ) {
        val levelValue =
            (userValue.experience * GameParameters.instance.levelFull) / DataManager.instance.user.value!!.nextExpLevel
        experienceConstraint.background?.level = levelValue.toInt() // from 0 to 10000 = 100%
    }

    fun heartsItemAnimation(view: View) {
        scaling(view)
    }

    fun updateUIAndDatabase() {
        if (DataManager.instance.user.value!!.heartsCount.counter == 0) {
            DataManager.instance.user.value!!.heartsCount.daysCount =
                System.currentTimeMillis() / 1000L
        }
        DataManager.instance.user.value!!.hearts += 20
        DataManager.instance.user.value!!.heartsCount.counter += 1
        _heartsTextItem.value = DataManager.instance.user.value!!.hearts.toString()
    }

    fun celebrateNewLevel(
        celebrationView: KonfettiView,
        levelUpImage: ImageView
    ) {
        observableBooleanLevelUp.set(true)
        observableVisibilityPlayButton.set(false)
        animateImage(levelUpImage)
        showConfetti(celebrationView)

        updateReward()
    }

    private fun updateReward() {
        viewModelScope.launch {
            delay(2000)
            observableBooleanLevelUp.set(false)
            observableBooleanRewardText.set(true)
            observableVisibilityPlayButton.set(true)
            delay(500)
            observableBooleanRewardText.set(false)
            DataManager.instance.user.value!!.gems += 20
            DataManager.instance.user.value!!.coins += 200
            DataManager.instance.user.value!!.hearts += 10
        }
    }
}
