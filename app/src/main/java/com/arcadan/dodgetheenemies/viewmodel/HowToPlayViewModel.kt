package com.arcadan.dodgetheenemies.viewmodel

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.arcadan.dodgetheenemies.fragments.HowToPlayFragmentDirections
import com.arcadan.dodgetheenemies.graphics.ObjectsAnimation.Companion.fadeDirection
import com.arcadan.dodgetheenemies.graphics.ObjectsAnimation.Companion.rotation
import com.arcadan.dodgetheenemies.graphics.ObjectsAnimation.Companion.translation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HowToPlayViewModel : ViewModel() {
    private var canDoAnim = true
    val observedImageState = ObservableBoolean(false)

    fun rotateImage(view: View) {
        rotation(view)
    }

    fun dodgerJump(view: View) {
        translation(view, -200f, 200)
    }

    fun backButtonClick(view: View) {
        view.findNavController()
            .navigate(HowToPlayFragmentDirections.actionHowToPlayFragmentToMainFragment())
    }

    fun doItemsAnimation(
        slurpView: View,
        speedUpView: View,
        megaJumpView: View,
        doubleCoinView: View
    ) {
        observedImageState.set(true)

        if (canDoAnim) {
            CoroutineScope(Dispatchers.Main).launch {
                canDoAnim = false
                fadeDirection(slurpView)
                fadeDirection(speedUpView)
                delay(200)
                fadeDirection(megaJumpView)
                fadeDirection(doubleCoinView)
                delay(300)
                canDoAnim = true
            }
        }
    }
}
