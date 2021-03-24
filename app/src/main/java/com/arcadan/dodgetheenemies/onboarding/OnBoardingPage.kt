package com.arcadan.dodgetheenemies.onboarding
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.arcadan.dodgetheenemies.R

enum class OnBoardingPage(
    @StringRes val titleResource: Int,
    @StringRes val subTitleResource: Int,
    @StringRes val descriptionResource: Int,
    @DrawableRes val logoResource: Int
) {

    ONE(R.string.onboarding_slide1_title, R.string.onboarding_slide1_subtitle, R.string.how_to_play_desc, R.drawable.rotate),
    TWO(R.string.onboarding_slide1_title, R.string.onboarding_slide2_subtitle, R.string.how_to_play_desc_2, R.drawable.on_boarding_image_2),
    THREE(R.string.onboarding_slide1_title, R.string.onboarding_slide3_subtitle, R.string.how_to_play_desc_3, R.drawable.on_boarding_image_3)
}
