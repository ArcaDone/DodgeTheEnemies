package com.arcadan.dodgetheenemies.graphics

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.enums.AnimationImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ObjectsAnimation {

    companion object {

        /**
         * Create a new star view in a random X position above the container.
         */
        fun shower(view: ImageView, imageType: AnimationImage) {
            val parent = view.parent as ViewGroup
            val containerW = parent.width
            val containerH = parent.height

            CoroutineScope(Dispatchers.Main.immediate).launch {
                repeat(30) {
                    // Make it rotateButton about its center as it falls to the bottom.
                    // Local variables we'll need in the code below
                    var starW: Float = view.width.toFloat()
                    var starH: Float = view.height.toFloat()

                    // Create the new view (an ImageView holding our drawable) and add it to the container
                    val newView = ImageView(Dodgers.context!!)
                    newView.setImageResource(imageType.drawable)

                    newView.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                    parent.addView(newView)

                    // Scale the view randomly between 10-160% of its default size
                    newView.scaleX = Math.random().toFloat() * 1.5f + .1f
                    newView.scaleY = newView.scaleX
                    starW *= newView.scaleX
                    starH *= newView.scaleY

                    // Position the view at a random place between the left and right edges of the container
                    newView.translationX = Math.random().toFloat() * containerW - starW / 2

                    // Create an animator that moves the view from a starting position right about the container
                    // to an ending position right below the container. Set an accelerate interpolator to give
                    // it a gravity/falling feel
                    val mover = ObjectAnimator.ofFloat(newView, View.TRANSLATION_Y, -starH, containerH + starH)
                    mover.interpolator = AccelerateInterpolator(1f)

                    // Create an animator to rotateButton the view around its center up to three times
                    val rotation = ObjectAnimator.ofFloat(
                        newView, View.ROTATION,
                        (Math.random() * 1080).toFloat()
                    )
                    rotation.interpolator = LinearInterpolator()

                    // Use an AnimatorSet to play the falling and rotating animators in parallel for a duration
                    // of a half-second to two seconds
                    val set = AnimatorSet()
                    set.playTogether(mover, rotation)
                    set.duration = (Math.random() * 1500 + 500).toLong()

                    // When the animation is done, remove the created view from the container
                    set.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            parent.removeView(newView)
                        }
                    })

                    set.start()
                }
            }
        }

        /**
         * Scale the view up to 2x its default size and back
         */
        fun scaling(view: View, actionFromDisabledToEnabled: Boolean = true, actionFromHideToVisible: Boolean = false) {
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f)
            val animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY)
            animator.repeatCount = 3
            animator.repeatMode = ObjectAnimator.REVERSE
            animator.disableViewDuringAnimation(view, actionFromDisabledToEnabled, actionFromHideToVisible)
            animator.start()
        }

        /**
         * Rotate the view for a second around its center once
         */
        fun rotation(view: View) {
            val animator = ObjectAnimator.ofFloat(view, View.ROTATION, -360f, 0f)
            animator.duration = 1000
            animator.disableViewDuringAnimation(view)
            animator.start()
        }

        /**
         * Translate the view 200 pixels
         */
        fun translation(view: View, floatTranslation: Float = 200f, durationMilliSec: Long = 1000) {
            val animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, floatTranslation)
            animator.duration = durationMilliSec
            animator.repeatCount = 1
            animator.repeatMode = ObjectAnimator.REVERSE
            animator.disableViewDuringAnimation(view)
            animator.start()
        }

        /**
         * Fade the view out to completely transparent and then back to completely opaque
         */
        fun fadeDirection(view: View) {
            val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f)
            animator.repeatCount = 1
            animator.repeatMode = ObjectAnimator.REVERSE
            animator.disableViewDuringAnimation(view)
            animator.start()
        }

        fun blink(view: View) {
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 50 // You can manage the blinking time with this parameter
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = 6
            view.startAnimation(anim)
        }

        /**
         * This extension method listens for start/end events on an animation and disables
         * the given view for the entirety of that animation.
         */
        private fun ObjectAnimator.disableViewDuringAnimation(view: View, actionFromDisabledToEnabled: Boolean = true, actionFromHideToVisible: Boolean = false) {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    if (actionFromDisabledToEnabled) {
                        view.isEnabled = false
                    }
                    if (actionFromHideToVisible) {
                        view.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (actionFromDisabledToEnabled) {
                        view.isEnabled = true
                    }
                    if (actionFromHideToVisible) {
                        view.visibility = View.INVISIBLE
                    }
                }
            })
        }
    }
}
